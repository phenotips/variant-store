package org.phenotips.variantstore.input.tsv;

/**
 * The columns in the supported CSV file, in the order that they come.
 */
public enum ExomiserTSVColumn {
    CHROM,
    POS,
    REF,
    ALT,
    QUAL,
    FILTER,
    GENOTYPE,
    COVERAGE,
    FUNCTIONAL_CLASS,
    HGVS,
    EXOMISER_GENE,
    CADD,
    POLYPHEN,
    MUTATIONTASTER,
    SIFT,
    DBSNP_ID,
    MAX_FREQUENCY,
    DBSNP_FREQUENCY,
    EVS_EA_FREQUENCY,
    EVS_AA_FREQUENCY,
    EXAC_AFR_FREQ,
    EXAC_AMR_FREQ,
    EXAC_EAS_FREQ,
    EXAC_FIN_FREQ,
    EXAC_NFE_FREQ,
    EXAC_SAS_FREQ,
    EXAC_OTH_FREQ,
    EXOMISER_VARIANT_SCORE,
    EXOMISER_GENE_PHENO_SCORE,
    EXOMISER_GENE_VARIANT_SCORE,
    EXOMISER_GENE_COMBINED_SCORE
}
