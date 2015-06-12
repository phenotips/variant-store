/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.phenotips.variantStoreIntegration.internal;

import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.util.List;

import org.ga4gh.GACall;
import org.ga4gh.GAVariant;

import net.sf.json.JSONObject;

/**
 * A class of static methods for translating {@link org.ga4gh.GAVariant} to {@link net.sf.json.JSON}.
 * @version $Id$
 */
public final class VariantJSONUtils
{
    private VariantJSONUtils()
    {
        throw new AssertionError();
    }

    /**Converts a {@link GAVariant} to a {@link JSONObject}.
     *
     * @param gaVariant The representation of the variant
     * @return A JSON representation of the variant in the following form:
     *
     */
    public static JSONObject convertGAVariantToJSON(GAVariant gaVariant) {
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("start", gaVariant.getStart());
        resultJSON.put("referenceBases", gaVariant.getReferenceBases());
        resultJSON.put("referenceName", gaVariant.getReferenceName());
        // We are only showing the first possible alternates.
        List<String> alternates = gaVariant.getAlternateBases();
        String alternateValue;
        if (alternates != null && !alternates.isEmpty()) {
            alternateValue = alternates.get(0);
        } else {
            alternateValue = "";
        }
        resultJSON.put("alternateBases", alternateValue);


        GACall call = gaVariant.getCalls().get(0);
        JSONObject info = new JSONObject();

        String infoField = VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE);
        if (infoField != null) {
            info.put("EXOMISER_VARIANT_SCORE", Double.parseDouble(infoField));
        }

        infoField = VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE);
        if (infoField != null) {
            info.put("EXOMISER_GENE_VARIANT_SCORE", Double.parseDouble(infoField));
        }

        infoField = VariantUtils.getInfo(gaVariant, GAVariantInfoFields.GENE_EFFECT);
        if (infoField != null) {
            info.put("GENE_EFFECT", infoField);
        }

        infoField = VariantUtils.getInfo(gaVariant, GAVariantInfoFields.GENE);
        if (infoField != null) {
            info.put("GENE", infoField);
        }

        infoField = VariantUtils.getInfo(gaVariant, GAVariantInfoFields.EXAC_AF);
        if (infoField != null) {
            info.put(GAVariantInfoFields.EXAC_AF, infoField);
        }
        resultJSON.element("info", info);
        return resultJSON;
    }
}
