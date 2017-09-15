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
package org.phenotips.variantstore.db.solr;

/**
 * @version $Id$
 */
public final class VariantsSchema
{
    //  a hash of chrom, pos, ref, alt
    public static final String ID = "id";

    public static final String CHROM = "chrom";
    //  start = pos - 1 (0-indexed)
    public static final String START = "start";
    //  end = start + len(ref) (exclusive)
    public static final String END = "end";
    public static final String REF = "ref";
    public static final String REF_LENGTH = "ref_length";
    public static final String ALT = "alt";
    public static final String ALT_LENGHT = "alt_length";
    //  length = max(len(ref), len(alt))
    public static final String LENGTH = "length";

    //  Fields with a single value per variant (chrom, pos, ref, alt)
    public static final String GENE = "gene";
    public static final String GENE_EFFECT = "gene_effect";
    public static final String GENE_HGVS = "gene_hgvs";
    public static final String EXAC_AF = "exac_af";
    //  aggregate statistics
    public static final String AC_TOT = "ac_tot";
    public static final String GT_HET = "gt_het";
    public static final String GT_HOM = "gt_hom";

    //  Multi-valued callset ids (individuals)
    public static final String CALLSET_IDS = "callset_ids";

    //  Fields with a separate value per-callset
    //  Each of these fields has a corresponding field
    //    that maps onto that callset's value, generated by getCallsetsFieldName
    public static final String PUBLIC = "is_public";
    public static final String AC = "ac";
    public static final String QUAL = "qual";
    public static final String FILTER = "filter";
    public static final String EXOMISER_VARIANT_SCORE = "exomiser_variant_score";
    public static final String EXOMISER_GENE_VARIANT_SCORE = "exomiser_gene_variant_score";
    public static final String EXOMISER_GENE_PHENO_SCORE = "exomiser_gene_pheno_score";
    // TODO this is the important score. sort on it
    public static final String EXOMISER_GENE_COMBINED_SCORE = "exomiser_gene_combined_score";

    private VariantsSchema()
    {
        throw new AssertionError();
    }

    /**
     * Create the field name for an individual.
     * @param individual the individual
     * @param fieldName the name of the field to access for that individual.
     * @return the individual's field name
     */
    public static String getCallsetsFieldName(String individual, String fieldName)
    {
        return individual + "__" + fieldName;
    }

}
