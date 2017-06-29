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

import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.nio.file.Path;

import org.ga4gh.GACall;
import org.ga4gh.GAVariant;

/**
 * Parse the `*.variants.tsv` files returned by [Exomiser 7.2.2](http://www.sanger.ac.uk/science/tools/exomiser).
 * Expose each line as a ga4gh GAVariant object.
 *
 * @version $Id$
 */
public class ExomiserTSVIterator extends AbstractTSVIterator
{
    private double maxExacFreq;

    /**
     * Create a new iterator over Exomiser TSVs.
     *
     * @param path          the path to the file
     * @param variantHeader the header with file meta-information
     */
    public ExomiserTSVIterator(Path path, VariantHeader variantHeader) {
        super(path, variantHeader);
    }

    protected void finalizeVariant(GAVariant variant) {
        super.finalizeVariant(variant);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXAC_AF, String.valueOf(maxExacFreq));
    }

    protected void processField(GAVariant variant, GACall call, String column, String field) {
        switch (column) {
            case "EXOMISER_GENE":
                String ensemblId = VariantUtils.getEnsemblId(field);
                VariantUtils.addInfo(variant, GAVariantInfoFields.GENE, ensemblId);
                break;
            case "FUNCTIONAL_CLASS":
                VariantUtils.addInfo(variant, GAVariantInfoFields.GENE_EFFECT, field);
                break;
            case "HGVS":
                VariantUtils.addInfo(variant, GAVariantInfoFields.GENE_HGVS, field);
                break;
            case "EXAC_AFR_FREQ":
            case "EXAC_AMR_FREQ":
            case "EXAC_EAS_FREQ":
            case "EXAC_FIN_FREQ":
            case "EXAC_NFE_FREQ":
            case "EXAC_SAS_FREQ":
            case "EXAC_OTH_FREQ":
                try {
                    // Exomiser outputs percentages instead of frequencies
                    maxExacFreq = Math.max(maxExacFreq, Double.parseDouble(field) / 100);
                } catch (NumberFormatException e) {
                    // do nothing, stay with default 0.0 value
                }
                break;
            case "EXOMISER_VARIANT_SCORE":
                VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE, field);
                break;
            case "EXOMISER_GENE_PHENO_SCORE":
                VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE, field);
                break;
            case "EXOMISER_GENE_COMBINED_SCORE":
                VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE, field);
                break;
            case "EXOMISER_GENE_VARIANT_SCORE":
                VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE, field);
                break;
            default:
                super.processField(variant, call, column, field);
        }
    }
}
