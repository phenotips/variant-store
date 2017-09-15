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
package org.phenotips.variantstore.shared;

/**
 * The keys in the {{@link org.ga4gh.GACall}} Info field.
 *
 * @version $Id$
 */
public final class GACallInfoFields
{
    public static final String EXOMISER_GENE_COMBINED_SCORE = "EXOMISER_GENE_COMBINED_SCORE";
    public static final String EXOMISER_GENE_PHENO_SCORE = "EXOMISER_GENE_PHENO_SCORE";
    public static final String EXOMISER_GENE_VARIANT_SCORE = "EXOMISER_GENE_VARIANT_SCORE";
    public static final String EXOMISER_VARIANT_SCORE = "EXOMISER_VARIANT_SCORE";
    public static final String FILTER = "FILTER";
    public static final String QUALITY = "QUAL";

    private GACallInfoFields()
    {
        throw new AssertionError();
    }
}
