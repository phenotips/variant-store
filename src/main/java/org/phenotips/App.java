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


/**
 * Hello world!
 *
 */
public class App 
{
    private final static String devDir = "/home/meatcar/dev/drill/variant-store/";



    public static void main( String[] args )
    {
//        File vcfFile = new File(devDir + "vcf/F0000009.ezr2");
        String fileName = "test.vcf";
        File vcfFile = new File(devDir + fileName);
        VCFFileReader vcfReader = new VCFFileReader(vcfFile, false);
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

        System.out.println(avro);

        /**
         * Write Parquet file
         */

        System.out.println(new AvroSchemaConverter().convert(avro.getSchema()).toString());

        String parquerSchema = "message org.ga4gh.GAVariant {\n" +
                "  required binary id (UTF8);\n" +
                "  required binary variantSetId (UTF8);\n" +
                "  required group names (LIST) {\n" +
                "    repeated binary array (UTF8);\n" +
                "  }\n" +
                "  optional int64 created;\n" +
                "  optional int64 updated;\n" +
                "  required binary referenceName (UTF8);\n" +
                "  required int64 start;\n" +
                "  required int64 end;\n" +
                "  required binary referenceBases (UTF8);\n" +
                "  required group alternateBases (LIST) {\n" +
                "    repeated binary array (UTF8);\n" +
                "  }\n" +
                "  required group info (MAP) {\n" +
                "    repeated group map (MAP_KEY_VALUE) {\n" +
                "      required binary key (UTF8);\n" +
                "      required group value (LIST) {\n" +
                "        repeated binary array (UTF8);\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "  required group calls (LIST) {\n" +
                "    repeated group array {\n" +
                "      optional binary callSetId (UTF8);\n" +
                "      optional binary callSetName (UTF8);\n" +
                "      required group genotype (LIST) {\n" +
                "        repeated int32 array;\n" +
                "      }\n" +
                "      optional binary phaseset (UTF8);\n" +
                "      required group genotypeLikelihood (LIST) {\n" +
                "        repeated double array;\n" +
                "      }\n" +
                "      required group info (MAP) {\n" +
                "        repeated group map (MAP_KEY_VALUE) {\n" +
                "          required binary key (UTF8);\n" +
                "          required group value (LIST) {\n" +
                "            repeated binary array (UTF8);\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        AvroParquetWriter avroParquetWriter;
        Job j;
        try {
            j = new Job();

            //TODO: remove file if it exists.
            Path path = new Path(devDir + "parquet/" + fileName + ".parquet");
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
