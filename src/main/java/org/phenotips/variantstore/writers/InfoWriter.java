package org.phenotips.variantstore.writers;

import htsjdk.variant.variantcontext.VariantContext;
import java.io.IOException;
import java.util.Map;
import org.phenotips.variantstore.models.Info;

/**
 * Created by meatcar on 1/19/15.
 */
public class InfoWriter extends AbstractParquetWriter {

    public InfoWriter(String filename, String outdir) throws Exception {
        super(filename + ".info", outdir, Info.getClassSchema());
    }

    @Override
    public void write(VariantContext vcfRow) throws IOException {
        CharSequence variantId = vcfRow.getID();
        Info avro = new Info();
        Map<String, Object> info = vcfRow.getCommonInfo().getAttributes();
        Object tmp;

        avro.setVariantId(variantId);

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
