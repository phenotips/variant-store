package org.phenotips;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import java.io.File;
import htsjdk.variant.variantcontext.VariantContext;
import java.io.IOException;
import java.util.*;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;
import parquet.avro.AvroParquetOutputFormat;
import parquet.avro.AvroParquetWriter;
import parquet.avro.AvroSchemaConverter;
import parquet.schema.*;


/**
 * Hello world!
 *
 */
public class App 
{
    private final static String devDir = "/home/meatcar/dev/drill/variant-store/";


    /**************
     * BATTLE PLAN
     **************
     *
     * Pre-runtime:
     * - turn avro schema into parquet schema
     * - TODO: add typed info fields as parquet fields (not values in an array)
     *
     * Runtime:
     *
     * == Import step ==
     * - Parse VCF using Picard/htsjdk
     * - TODO: Insert attributes into some sort of object
     * - Write object into parquet file using schema
     *
     * == TODO: Query step ==
     * - Copy ./drill to /tmp/drill, specifically drill/sys.storage_plugins/phenotips.sys.drill
     * - Initialize drillbit using java in embedded mode, not sh/scripts (note: see jar calls in scripts)
     *   - make sure
     * - Connect to it over jdbc
     * - compose your queries!!
     *
     */

    public static void main( String[] args )
    {
//        String vcfFileName = "test.vcf";
        String vcfFileName = "../vcf/F0000009.ezr2";
        VCFFileReader vcfReader = new VCFFileReader(new File(devDir + vcfFileName), false);
        VCFHeader vcfHeader = vcfReader.getFileHeader();
        Iterator<VariantContext> it;
        it = vcfReader.iterator();


//        while (it.hasNext()) {
//        }
        VariantContext ctx = it.next(); //.fullyDecode(vcfHeader, true);

        GAVariant avro = new GAVariant();

        /**
         * Build AVRO object
         */
        avro.setId(ctx.getID());
        avro.setVariantSetId("test");

        List<CharSequence> names = new ArrayList<>();
        for (String n : ctx.getSampleNamesOrderedByName()) {
            names.add(n);
        }
        avro.setNames(names);

        avro.setReferenceName(ctx.getChr());
        avro.setStart((long) ctx.getStart());
        avro.setEnd((long) ctx.getEnd());

        avro.setReferenceBases(ctx.getReference().getBaseString());

        // ALT
        List<CharSequence> alts = stringifyAlleles(ctx.getAlternateAlleles());
        avro.setAlternateBases(alts);

        // INFO
        Map<String, Object> attrs = ctx.getCommonInfo().getAttributes();

        Map<CharSequence, List<CharSequence>> info = getGAInfoMap(attrs);
        info.put("SSEN", new ArrayList<CharSequence>(alts));
        avro.setInfo(info);
//        avro.setInfo(new HashMap<CharSequence, List<CharSequence>>());

        // Calls
        List<GACall> calls = new ArrayList<>();
        for (Genotype g : ctx.getGenotypes()) {
            GACall call = new GACall();

            call.setGenotype(new ArrayList<Integer>());
            for (Allele a : g.getAlleles()) {
                call.getGenotype().add(ctx.getAlleleIndex(a));
            }

            call.setCallSetId(g.getSampleName());
            call.setCallSetName(g.getSampleName());

            // Likelihood. Wrangle double[] to List<Double>
            call.setGenotypeLikelihood(new ArrayList<Double>());
            if (g.getLikelihoods() != null) {
                List<Double> likelihood = Arrays.asList(ArrayUtils.toObject(g.getLikelihoods().getAsVector()));
                call.setGenotypeLikelihood(likelihood);
            }

            call.setInfo(getGAInfoMap(g.getExtendedAttributes()));

            calls.add(call);
        }
        avro.setCalls(calls);
//        avro.setCalls(new ArrayList<GACall>());

        System.out.println(avro.toString().replace(",", ",\n"));

        /**
         * Write Parquet file
         */

        /**
         * Modify Avro->Parquet schema to include typed INFO fields.
         * This is nescessary because otherwise, INFO shows up as {map: {array: [{key: "foo", value: "bar"}..]}},
         * and drill can only query the inner array by index.
         *      ("SELECT * FROM table.info.map.array[0]" == "bar")
         * We need the values to be columns, so we can access them by name:
         *      ("SELECT * FROM table.infoTyped.foo" == "bar")
         */
        MessageType parquetSchema = new AvroSchemaConverter().convert(avro.getSchema());
        System.out.println(parquetSchema.toString());
        List<Type> infoTypedFields = new ArrayList<>();
        infoTypedFields.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.DOUBLE, "exomiserGeneVariantScore"));

        List<Type> fields = parquetSchema.getFields();
        fields.add(new GroupType(Type.Repetition.OPTIONAL, "infoTyped", OriginalType.MAP_KEY_VALUE, infoTypedFields));

        parquetSchema = new MessageType(parquetSchema.getName(), fields);
        System.out.println(parquetSchema.toString());



        AvroParquetWriter avroParquetWriter;
        Job j;
        try {
            j = new Job();

            //TODO: remove file if it exists.
            Path path = new Path(devDir + "parquet/" + vcfFileName + ".parquet");
            avroParquetWriter = new AvroParquetWriter(
                    path, GAVariant.getClassSchema());
            avroParquetWriter.write(avro);
            avroParquetWriter.close();
        } catch (IOException e) {
            System.out.println("ERRROOORR!!");
            e.printStackTrace();
            return;
        }
    }

    private static List<CharSequence> stringifyAlleles(List<Allele> alleles) {
        List<CharSequence> alts = new ArrayList<>();
        for (Allele a : alleles) {
            alts.add(a.getBaseString());
        }
        return alts;
    }

    private static Map<CharSequence, List<CharSequence>> getGAInfoMap(Map<String, Object> arg) {
        Map<CharSequence,List<CharSequence>> info = new HashMap<>();

        for (Map.Entry<String, Object> entry : arg.entrySet()) {
            Object v = entry.getValue();
            List<CharSequence> list = new ArrayList<>();
            if (v != null) {
                if (v.getClass() != ArrayList.class) {
                    list.add(v.toString());
                } else {
                    // v is an array
                    for (Object o : (List) v) {
                        if (o == null) {
                            list.add("");
                        } else {
                            list.add(o.toString());
                        }
                    }

                }
            }

            info.put(entry.getKey(), list);
        }
        return info;
    }
}
