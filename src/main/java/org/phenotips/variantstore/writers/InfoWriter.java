package org.phenotips.variantstore.writers;

import htsjdk.variant.variantcontext.VariantContext;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.phenotips.variantstore.models.Info;

/**
 * Created by meatcar on 1/19/15.
 */
public class InfoWriter extends AbstractParquetWriter {

    public InfoWriter(Path outFile) throws Exception {
        super(outFile, Info.getClassSchema());
    }

    @Override
    public void write(VariantContext vcfRow) throws IOException {
        Info avro = new Info();
        Map<String, Object> info = vcfRow.getCommonInfo().getAttributes();
        Object tmp;

        avro.setVariantId(vcfRow.getID());
        avro.setReferenceName(vcfRow.getChr());
        avro.setStart((long) vcfRow.getStart());
        avro.setEnd((long) vcfRow.getEnd());
        avro.setReferenceBases(vcfRow.getReference().getBaseString());
        // ALT
        List<CharSequence> alts = VariantWriter.stringifyAlleles(vcfRow.getAlternateAlleles());
        avro.setAlternateBases(alts);

        if ((tmp = info.get("EXOMISER_GENE")) != null) {
            avro.setExomiserGene((CharSequence) tmp);
        }
        if ((tmp = info.get("EXOMISER_GENE_PHENO_SCORE")) != null) {
            avro.setExomiserGenePhenoScore(Double.valueOf((String) tmp));
        }
        if ((tmp = info.get("EXOMISER_GENE_COMBINED_SCORE")) != null) {
            avro.setExomiserGeneCominedScore(Double.valueOf((String) tmp));
        }
        if ((tmp = info.get("EXOMISER_GENE_VARIANT_SCORE")) != null) {
            avro.setExomiserGeneVariantScore(Double.valueOf((String) tmp));
        }
        if ((tmp = info.get("EXOMISER_VARIANT_SCORE")) != null) {
            avro.setExomiserVariantScore(Double.valueOf((String) tmp));
        }

        //TODO: ADD OTHER INFO FIELDS! what else do we care about?
        super.writeAvro(avro);
    }
}
