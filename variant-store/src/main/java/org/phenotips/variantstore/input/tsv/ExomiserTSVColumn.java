/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.variantstore.input.tsv;

/**
 * The columns in the supported TSV file, in the order that they come.
 *
 * @version $Id$
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
    REMM,
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


