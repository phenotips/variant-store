package org.phenotips.variantstore.shared;

/**
 * @version $Id$
 */
public final class GAVariantInfoFields
{
    public static final String EXAC_AF = "EXAC_AF";
    public static final String EXOMISER_GENE_COMBINED_SCORE = "EXOMISER_GENE_COMBINED_SCORE";
    public static final String EXOMISER_GENE_PHENO_SCORE = "EXOMISER_GENE_PHENO_SCORE";
    public static final String EXOMISER_GENE_VARIANT_SCORE = "EXOMISER_GENE_VARIANT_SCORE";
    public static final String EXOMISER_VARIANT_SCORE = "EXOMISER_VARIANT_SCORE";
    public static final String FILTER = "FILTER";
    public static final String GENE = "GENE";
    public static final String GENE_EFFECT = "GENE_EFFECT";
    public static final String QUALITY = "QUAL";

    private GAVariantInfoFields() {
        throw new AssertionError();
    }
}
