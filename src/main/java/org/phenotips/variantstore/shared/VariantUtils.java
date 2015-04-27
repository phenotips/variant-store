package org.phenotips.variantstore.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ga4gh.GAVariant;

/**
 * Utility functions for working with GAVariants
 */
public class VariantUtils
{
    /**
     * Add a value to the the variant's info map. Creates a new map if it doesn't exist.
     *
     * @param variant   the GAVariant
     * @param infoField the info field's name
     * @param value     the field's value
     */
    public static void addInfoToVariant(GAVariant variant, String infoField, Object value) {
        if (variant.getInfo() == null) {
            variant.setInfo(new HashMap<String, List<String>>());
        }

        variant.getInfo().put(infoField, Collections.<String>singletonList(String.valueOf(value)));
    }

    /**
     * Get a value from the info field. Assumes that each info field stores a single value.
     *
     * @param variant the GAVariant
     * @param field   the field to fetch.
     * @return null if field is not found, the String value otherwise.
     */
    public static String getInfoFromVariant(GAVariant variant, String field) {
        Map<String, List<String>> info = variant.getInfo();

        if (info == null || info.get(field) == null) {
            return null;
        }

        return info.get(field).get(0);
    }
}
