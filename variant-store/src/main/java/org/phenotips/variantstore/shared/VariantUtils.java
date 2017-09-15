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

import org.phenotips.components.ComponentManagerRegistry;
import org.phenotips.vocabulary.Vocabulary;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ga4gh.GACall;
import org.ga4gh.GAVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for working with GAVariants.
 *
 * @version $Id$
 */
public final class VariantUtils
{
    /** Manager to allow access to HGNC vocabulary gene data. */
    private static VocabularyManager vocabularyManager;

    /** Logging helper object. */
    private static Logger logger = LoggerFactory.getLogger(VariantUtils.class);

    static {
        VocabularyManager vm = null;
        try {
            ComponentManager ccm = ComponentManagerRegistry.getContextComponentManager();
            vm = ccm.getInstance(VocabularyManager.class);
        } catch (ComponentLookupException e) {
            logger.error("Error loading static components: {}", e.getMessage(), e);
        }
        vocabularyManager = vm;
    }

    private VariantUtils()
    {
        throw new AssertionError();
    }

    /**
     * Add a value to the the variant's info map. Creates a new map if it doesn't exist.
     *
     * @param variant   the GAVariant
     * @param infoField the info field's name
     * @param value     the field's value
     */
    public static void addInfo(GAVariant variant, String infoField, Object value)
    {
        if (variant.getInfo() == null) {
            variant.setInfo(new HashMap<String, List<String>>());
        }

        variant.getInfo().put(infoField, Collections.<String>singletonList(String.valueOf(value)));
    }

    /**
     * Add a value to the call's info map. Creates a new map if it doesn't exist.
     * @param call the call
     * @param infoField the info field name
     * @param value the info field value
     */
    public static void addInfo(GACall call, String infoField, Object value)
    {
        if (call.getInfo() == null) {
            call.setInfo(new HashMap<String, List<String>>());
        }

        call.getInfo().put(infoField, Collections.<String>singletonList(String.valueOf(value)));
    }

    /**
     * Get a value from the info field. Assumes that each info field stores a single value.
     *
     * @param variant the GAVariant
     * @param field   the field to fetch.
     * @return null if field is not found, the String value otherwise.
     */
    public static String getInfo(GAVariant variant, String field)
    {
        return getFirstElementValue(variant.getInfo(), field);
    }

    /**
     * Get a value from the info field. Assumes that each info field stores a single value
     * @param call the GACall
     * @param field the field to fetch
     * @return null if field is not found, the String value otherwise
     */
    public static String getInfo(GACall call, String field)
    {
        return getFirstElementValue(call.getInfo(), field);
    }

    /**
     * Given a map, retrieve the array value for the given key, and return the first element of the array, or null.
     * @param map the map
     * @param key the key
     * @return null if map is null or the key is not in the map. The first element of the value otherwise.
     */
    private static String getFirstElementValue(Map<String, List<String>> map, String key)
    {
        if (map == null || map.get(key) == null) {
            return null;
        }

        return map.get(key).get(0);
    }

    /**
     * Gets EnsemblID corresponding to the HGNC symbol.
     *
     * @param geneSymbol the string representation of a gene symbol (e.g. NOD2).
     * @return the string representation of the corresponding Ensembl ID.
     */
    public static String getEnsemblId(String geneSymbol)
    {
        String symbol = null;
        if (vocabularyManager != null) {
            Vocabulary hgnc = vocabularyManager.getVocabulary("HGNC");
            VocabularyTerm term = hgnc.getTerm(geneSymbol);
            @SuppressWarnings("unchecked")
            List<String> ensemblIdList = term != null ? (List<String>) term.get("ensembl_gene_id") : null;
            String ensemblId = ensemblIdList != null && !ensemblIdList.isEmpty() ? ensemblIdList.get(0) : null;
            // Retain information as is if we can't find Ensembl ID.
            symbol = ensemblId != null ? ensemblId : geneSymbol;
        }
        return symbol;
    }
}
