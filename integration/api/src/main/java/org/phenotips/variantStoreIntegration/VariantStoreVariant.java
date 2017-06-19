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
package org.phenotips.variantStoreIntegration;

import org.phenotips.data.similarity.internal.AbstractVariant;
import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;

/**
 * A variant from the variant store. Annotated by Exomiser.
 *
 * @version $Id$
 */
public class VariantStoreVariant extends AbstractVariant
{
    private static DecimalFormat df = new DecimalFormat("#.####");

    /**
     * Create a {@link Variant} from a {@link GAVariant} returned by a {@link
     * org.phenotips.variantstore.VariantStoreInterface}.
     *
     * @param gaVariant a {@link GAVariant}
     * @param totIndividuals number of individuals stored in the variant store
     */
    public VariantStoreVariant(GAVariant gaVariant, Integer totIndividuals) {
        setChrom(gaVariant.getReferenceName());
        setPosition((int) (gaVariant.getStart() + 1));

        GACall call = gaVariant.getCalls().get(0);
        List<Integer> genotype = call.getGenotype();

        setGenotype(gaVariant.getReferenceBases(),
                StringUtils.join(gaVariant.getAlternateBases(), ','),
                StringUtils.join(genotype, '/'));

        setEffect(VariantUtils.getInfo(gaVariant, GAVariantInfoFields.GENE_EFFECT));

        String value = VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE);
        if (value == null || "null".equals(value)) {
            setScore(null);
        } else {
            setScore(Double.valueOf(value));
        }

        setAnnotation("geneScore", VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE));
        setAnnotation("geneSymbol", VariantUtils.getInfo(gaVariant, GAVariantInfoFields.GENE));
        setAnnotation("hgvs", VariantUtils.getInfo(gaVariant, GAVariantInfoFields.GENE_HGVS));
        value = VariantUtils.getInfo(gaVariant, GAVariantInfoFields.EXAC_AF);
        setAnnotation("exacAF", df.format(Double.valueOf(value)));
        setAnnotation("gtHet", VariantUtils.getInfo(gaVariant, GAVariantInfoFields.GT_HET));
        setAnnotation("gtHom", VariantUtils.getInfo(gaVariant, GAVariantInfoFields.GT_HOM));

        if (totIndividuals != null) {
            value = VariantUtils.getInfo(gaVariant, GAVariantInfoFields.AC_TOT);
            Double pcAF = Double.valueOf(value) / (totIndividuals * 2);
            setAnnotation("pcAF", df.format(pcAF));
        }
    }
}
