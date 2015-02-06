package org.phenotips.variantstore.writers;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;
import parquet.avro.AvroSchemaConverter;

/**
 * Created by meatcar on 1/16/15.
 */
public class VariantWriter extends AbstractParquetWriter {

    private Logger logger = Logger.getLogger(VariantWriter.class);

    public VariantWriter(Path outFile) throws Exception {
        super(outFile, GAVariant.getClassSchema());
    }

    /**
     * Build GAVariant given a VCF Row and write it out
     * @param vcfRow the row to build a variant out of
     * @return the ga4gh GAVariant object
     */
    @Override
    public void write(VariantContext vcfRow) throws IOException {
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
        avro.setCalls(getGaCalls(vcfRow)); //TODO: seems like nulls in arrays break drill. Investigate
        avro.setCalls(new ArrayList<GACall>());

        super.writeAvro(avro);
    }

    /**
     * Build a list of calls in a variant.
     * @param vcfRow the VCF row representing the variant
     * @return a list of GACalls, where each GACall is a ga4gh object that represents a call made in the variant
     */
    private List<GACall> getGaCalls(VariantContext vcfRow) {
        List<GACall> calls = new ArrayList<>();
        for (Genotype g : vcfRow.getGenotypes()) {
            GACall call = new GACall();

            call.setGenotype(new ArrayList<Integer>());
            for (Allele a : g.getAlleles()) {
                call.getGenotype().add(vcfRow.getAlleleIndex(a));
            }

            call.setCallSetId(g.getSampleName());
            call.setCallSetName(g.getSampleName());

            // TODO: this seems to error out in drill.. investigate
            // Likelihood. Wrangle double[] to List<Double>
            call.setGenotypeLikelihood(new ArrayList<Double>());
            if (g.getLikelihoods() != null) {
                List<Double> likelihood = Arrays.asList(ArrayUtils.toObject(g.getLikelihoods().getAsVector()));
                for (Double d : likelihood) {
                    if (d == null) {
                        logger.error(g.getLikelihoodsString());
                    }
                }
                call.setGenotypeLikelihood(likelihood);
            }

            call.setInfo(getGAInfoMap(g.getExtendedAttributes()));

            calls.add(call);
        }
        return calls;
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
    private Map<CharSequence, List<CharSequence>> getGAInfoMap(Map<String, Object> objectMap) {
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
        info.put("EXOMISER_GENE", Arrays.<CharSequence>asList("HYDIN"));
        info.put("EXOMISER_VARIANT_SCORE", Arrays.<CharSequence>asList(String.valueOf(Math.random())));
        info.put("EXOMISER_GENE_PHENO_SCORE", Arrays.<CharSequence>asList(String.valueOf(Math.random())));
        info.put("EXOMISER_GENE_VARIANT_SCORE", Arrays.<CharSequence>asList(String.valueOf(Math.random())));
        info.put("EXOMISER_GENE_COMBINED_SCORE", Arrays.<CharSequence>asList(String.valueOf(Math.random())));
        info.put("EXOMISER_EFFECT", Arrays.<CharSequence>asList("MISSENSE"));
        return info;
    }

    /**
     * Turn a list of htsjdk Alleles into a list of Strings/CharSequences
     * @param alleles a list of htsjdk Alleles
     * @return a list of String representations of each allele
     */
    public static List<CharSequence> stringifyAlleles(List<Allele> alleles) {
        List<CharSequence> alts = new ArrayList<>();
        for (Allele a : alleles) {
            alts.add(a.getDisplayString());
        }
        return alts;
    }
}
