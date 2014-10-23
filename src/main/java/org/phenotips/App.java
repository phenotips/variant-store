package org.phenotips;

import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import java.io.File;
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
    private final static String vcfDir = "/home/meatcar/dev/drill/vcf/";

    /**************
     * BATTLE PLAN
     **************
     *
     * Runtime:
     *
     * == Import step ==
     * - Parse VCF using Picard/htsjdk
     * - Insert attributes into some sort of object
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
        File dir = new File(vcfDir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File vcfFile : directoryListing) {
                System.out.println("Processing: " + vcfFile.getAbsolutePath());
                vcfToParquet(vcfFile, devDir + "parquet/");
            }
        } else {
            System.err.println("Directory " + vcfDir + "is empty!");
        }
    }

    private static void vcfToParquet(File vcfFile, String outputDirPath) {
        VCFFileReader vcfReader = new VCFFileReader(vcfFile, false);
        VCFHeader vcfHeader = vcfReader.getFileHeader();
        Iterator<VariantContext> it;
        it = vcfReader.iterator();

        AvroParquetWriter variantWriter = null;
        AvroParquetWriter infoWriter = null;

        try {
            variantWriter = new AvroParquetWriter(
                    new Path(devDir + "parquet/" + vcfFile.getName() + ".parquet"), GAVariant.getClassSchema());
            infoWriter = new AvroParquetWriter(
                    new Path(devDir + "parquet/" + vcfFile.getName() + ".info.parquet"), Info.getClassSchema());
        } catch (IOException e) {
            e.printStackTrace();
        }

        GAVariant gaVariant;
        Info typedInfo;

        VariantContext vcfRow;
        while (it.hasNext()) {
            try {
                vcfRow = it.next(); //.fullyDecode(vcfHeader, true);
            } catch (Exception e) {
                System.err.println("Error encountered while processing " + vcfFile.getAbsolutePath());
                e.printStackTrace();
                continue;
            }

            gaVariant = getGaVariant(vcfRow);

            typedInfo = getTypedInfo(vcfRow, gaVariant.getId());

            /**
             * Write Parquet file
             */

            try {
                //TODO: remove file if it exists.
                variantWriter.write(gaVariant);
                infoWriter.write(typedInfo);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        try {
            variantWriter.close();
            infoWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Build GAVariant given a VCF Row
     */
    private static GAVariant getGaVariant(VariantContext vcfRow) {
        GAVariant avro = new GAVariant();

        avro.setId(vcfRow.getID());
        avro.setVariantSetId("test");

        List<CharSequence> names = new ArrayList<>();
        for (String n : vcfRow.getSampleNamesOrderedByName()) {
            names.add(n);
        }
        avro.setNames(names);

        avro.setReferenceName(vcfRow.getChr());
        avro.setStart((long) vcfRow.getStart());
        avro.setEnd((long) vcfRow.getEnd());

        avro.setReferenceBases(vcfRow.getReference().getBaseString());

        // ALT
        List<CharSequence> alts = stringifyAlleles(vcfRow.getAlternateAlleles());
        avro.setAlternateBases(alts);

        // INFO
        Map<String, Object> attrs = vcfRow.getCommonInfo().getAttributes();

        Map<CharSequence, List<CharSequence>> info = getGAInfoMap(attrs);
        info.put("SSEN", new ArrayList<CharSequence>(alts));
        avro.setInfo(info);
//        avro.setInfo(new HashMap<CharSequence, List<CharSequence>>());

        // Calls
//        avro.setCalls(getGaCalls(ctx));
        avro.setCalls(new ArrayList<GACall>());
        return avro;
    }

    private static Info getTypedInfo(VariantContext vcfRow, CharSequence variantId) {
        Info typedInfo = new Info();
        final CommonInfo commonInfo = vcfRow.getCommonInfo();

        typedInfo.setVariantId(variantId);
        typedInfo.setExomiserGene((CharSequence) commonInfo.getAttribute("EXOMISER_GENE"));
        typedInfo.setExomiserGenePhenoScore(Double.valueOf((String) commonInfo.getAttribute("EXOMISER_GENE_PHENO_SCORE")));
        typedInfo.setExomiserGeneCominedScore(Double.valueOf((String) commonInfo.getAttribute("EXOMISER_GENE_COMBINED_SCORE")));
        typedInfo.setExomiserGeneVariantScore(Double.valueOf((String) commonInfo.getAttribute("EXOMISER_GENE_VARIANT_SCORE")));
        typedInfo.setExomiserVariantScore(Double.valueOf((String) commonInfo.getAttribute("EXOMISER_VARIANT_SCORE")));

        return typedInfo;
    }


    private static List<GACall> getGaCalls(VariantContext vcfRow) {
        List<GACall> calls = new ArrayList<>();
        for (Genotype g : vcfRow.getGenotypes()) {
            GACall call = new GACall();

            call.setGenotype(new ArrayList<Integer>());
            for (Allele a : g.getAlleles()) {
                call.getGenotype().add(vcfRow.getAlleleIndex(a));
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
        return calls;
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
