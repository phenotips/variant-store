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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phenotips.variantstore.db.solr;

/**
 * @version $Id$
 */
public final class SolrSchema
{
    public static final String CHROM = "chrom";
    public static final String POS = "pos";
    public static final String REF = "ref";
    public static final String ALT = "alt";
    public static final String QUAL = "qual";
    public static final String FILTER = "filter";
    public static final String EXOMISER_VARIANT_SCORE = "exomiser_variant_score";
    public static final String EXOMISER_GENE_PHENO_SCORE = "exomiser_gene_pheno_score";
    public static final String EXOMISER_GENE_VARIANT_SCORE = "exomiser_gene_variant_score";
    public static final String EXOMISER_GENE_COMBINED_SCORE = "exomiser_gene_combined_score";
    public static final String GENE = "gene";
    public static final String GENE_EFFECT = "gene_effect";
    public static final String EXAC_AF = "exac_af";
    public static final String INDIVIDUAL = "individual";
    public static final String ID = "id";
    public static final String COPIES = "copies";

    private SolrSchema() {
        throw new AssertionError();
    }
}
