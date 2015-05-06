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
package org.phenotips.variantstore.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ga4gh.GAVariant;

/**
 * Utility functions for working with GAVariants.
 *
 * @version $Id$
 */
public final class VariantUtils
{
    private VariantUtils() {
        throw new AssertionError();
    }

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
