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

    public static void main( String[] args ) {
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
         */

        // Nuke all parquet files. Really, we should be overwriting them..
        File dir;
        File[] directoryListing;

        String outDir = devDir + "parquet/";

        dir = new File(outDir);
        directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File vcfFile : directoryListing) {
                if (vcfFile.getName().endsWith(".parquet")) {
                    vcfFile.delete();
                }
            }
        }

        dir = new File(vcfDir);
        directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File vcfFile : directoryListing) {
                if (vcfFile.getName().endsWith("ASM.vcf")) {
                    System.out.println("Processing: " + vcfFile.getAbsolutePath());
                    vcfToParquet(vcfFile, outDir);
                }
            }
        } else {
            System.err.println("Directory " + vcfDir + "is empty!");
            return;
        }

        /*
         * == TODO: Query step ==
         * - Copy ./drill to /tmp/drill, specifically drill/sys.storage_plugins/phenotips.sys.drill
         * - Initialize drillbit using java in embedded mode, not sh/scripts (note: see jar calls in scripts)
         *   - make sure
         * - Connect to it over jdbc
         * - compose your queries!!
         *
         */

    }

    /**
     * Parse a VCF File into parquet format, and write the parquet file out to a directory
     * @param vcfFile the VCF file to parse
     * @param outputDirPath the directory to write the parquet file to
     */
    private static void vcfToParquet(File vcfFile, String outputDirPath){
        VCFFileReader vcfReader = new VCFFileReader(vcfFile, false);
        VCFHeader vcfHeader = vcfReader.getFileHeader();
        String id = null;
        if (vcfHeader.getSampleNamesInOrder().size() > 1) {
            System.err.println("Multi-sample VCF unsupported");
            return;
        } else if (vcfHeader.getSampleNamesInOrder().size() == 1) {
            id = vcfHeader.getSampleNamesInOrder().get(0);
            //TODO: pass this to getGaVariant
        } else {
            //TODO: get patient name
        }

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

            /**
             * Parse VCF row to ga4gh schema + our own metadata schema
             */

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
     * @param vcfRow the row to build a variant out of
     * @return the ga4gh GAVariant object
     */
    private static GAVariant getGaVariant(VariantContext vcfRow) {
        GAVariant avro = new GAVariant();

        avro.setId(vcfRow.getID()); //TODO: what happens when ID = '.'? (missing value) (maybe generate our own?)
                                    //      Note that the names field is the one that stores RefSNP Ids.
        avro.setVariantSetId("test"); //TODO: probably should be some sort of patient identifier

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

        // Calls
//        avro.setCalls(getGaCalls(vcfRow)); // TODO: seems like nulls in arrays break drill. Investigate
        avro.setCalls(new ArrayList<GACall>());
        return avro;
    }

    /**
     * Build an Info object, which contains all the info fields
     * @param vcfRow the VCF row whose info field should be parsed
     * @param variantId the id of the variant we are parsing
     * @return the Info object
     */
    private static Info getTypedInfo(VariantContext vcfRow, CharSequence variantId) {
        Info typedInfo = new Info();
        Map<String, Object> info = vcfRow.getCommonInfo().getAttributes();
        Object tmp;

        typedInfo.setVariantId(variantId);

        if ((tmp = info.get("EXOMISER_GENE")) != null) {
            typedInfo.setExomiserGene((CharSequence) tmp);
        }
        if ((tmp = info.get("EXOMISER_GENE_PHENO_SCORE")) != null) {
            typedInfo.setExomiserGenePhenoScore(Double.valueOf((String) tmp));
        }
        if ((tmp = info.get("EXOMISER_GENE_COMBINED_SCORE")) != null) {
            typedInfo.setExomiserGeneCominedScore(Double.valueOf((String) tmp));
        }
        if ((tmp = info.get("EXOMISER_GENE_VARIANT_SCORE")) != null) {
            typedInfo.setExomiserGeneVariantScore(Double.valueOf((String) tmp));
        }
        if ((tmp = info.get("EXOMISER_VARIANT_SCORE")) != null) {
            typedInfo.setExomiserVariantScore(Double.valueOf((String) tmp));
        }

        //TODO: ADD OTHER INFO FIELDS! what else do we care about?

        return typedInfo;
    }


    /**
     * Build a list of calls in a variant.
     * @param vcfRow the VCF row representing the variant
     * @return a list of GACalls, where each GACall is a ga4gh object that represents a call made in the variant
     */
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
            // TODO: this seems to error out in drill.. investigate
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

    /**
     * Turn a list of htsjdk Alleles into a list of Strings/CharSequences
     * @param alleles a list of htsjdk Alleles
     * @return a list of String representations of each allele
     */
    private static List<CharSequence> stringifyAlleles(List<Allele> alleles) {
        List<CharSequence> alts = new ArrayList<>();
        for (Allele a : alleles) {
            alts.add(a.getDisplayString());
        }
        return alts;
    }

    /**
     * Convert a map with un-typed objects into a map of lists of strings.
     * The rules for converting the objects are as follows:
     *
     *  null   -> []
     *  x      -> [x.toString()]
     *  [x, y] -> [x.toString(), y.toString()]
     *
     * @param objectMap a String->Object map to convert
     * @return a String->List[String..] map
     */
    private static Map<CharSequence, List<CharSequence>> getGAInfoMap(Map<String, Object> objectMap) {
        Map<CharSequence,List<CharSequence>> info = new HashMap<>();

        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
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
