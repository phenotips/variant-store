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

import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;

import static org.phenotips.variantstore.shared.VariantUtils.addInfo;
import static org.phenotips.variantstore.shared.VariantUtils.getInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;

/**
 * @version $Id$
 */
public final class SolrVariantUtils
{
    /**
     * The ID of the metadocument that stores all individual ids.
     */
    public static final String METADATA_DOC_ID = "metadata";

    private SolrVariantUtils() {
        throw new AssertionError();
    }

    /**
     * Collect all the individuals into their own maps.
     *
     * @param mapList a list of maps of individual to variant.
     *
     * @return a map of callSetId to list of variants
     */
    public static Map<String, List<GAVariant>> variantListToCallsetMap(List<Map<String, GAVariant>> mapList) {
        Map<String, List<GAVariant>> callsetMap = new HashMap<>();
        for (Map<String, GAVariant> map : mapList) {
            for (String key : map.keySet()) {
                if (!callsetMap.containsKey(key)) {
                    callsetMap.put(key, new ArrayList<GAVariant>());
                }
                callsetMap.get(key).add(map.get(key));
            }
        }

        return callsetMap;
    }

    /**
     * Add the documents in a SolrDocumentList to a list of GAVariants.
     *
     * @param documentList the SolrDocumentList
     *
     * @return the list of GAVariants
     */
    public static List<Map<String, GAVariant>> documentListToMapList(SolrDocumentList documentList) {
        List<Map<String, GAVariant>> list = new ArrayList<>();

        for (SolrDocument doc : documentList) {
            Map<String, GAVariant> variantMap = docToVariantMap(doc);

            list.add(variantMap);
        }

        return list;
    }

    /**
     * Add the documents in a SolrDocumentList to a list of GAVariants for the individual ID.
     *
     * @param documentList the SolrDocumentList
     * @param callsetId the individual id
     *
     * @return the list of GAVariants
     */
    public static List<GAVariant> documentListToGAVarintList(SolrDocumentList documentList, String callsetId) {
        List<GAVariant> list = new ArrayList<>();

        for (SolrDocument doc : documentList) {
            list.add(docToVariant(doc, callsetId));
        }

        return list;
    }

    /**
     * Get all the individual variants out of a doc.
     *
     * @param doc the doc
     *
     * @return a map of callsetid to variant
     */
    public static Map<String, GAVariant> docToVariantMap(SolrDocument doc) {
        Map<String, GAVariant> map = new HashMap<>();

        for (String callsetId : (List<String>) doc.get(VariantsSchema.CALLSET_IDS)) {
            map.put(callsetId, docToVariant(doc, callsetId));
        }

        return map;
    }

    /**
     * Turn a SolrDocument to a GAVariant.
     *
     * @param doc       the SolrDocument
     * @param callsetId the id of the callset
     *
     * @return a new GAVariant
     */
    public static GAVariant docToVariant(SolrDocument doc, String callsetId) {
        GAVariant variant = new GAVariant();

        // TODO: Whole function needs to be newed, should be doc to list<Variant>
        variant.setReferenceName(doc.get(VariantsSchema.CHROM).toString());
        variant.setReferenceBases(doc.get(VariantsSchema.REF).toString());
        variant.setStart(Long.valueOf(doc.get(VariantsSchema.START).toString()));
        variant.setEnd(Long.valueOf(doc.get(VariantsSchema.END).toString()));
        variant.setAlternateBases(Collections.singletonList(doc.get(VariantsSchema.ALT).toString()));

        addInfo(variant, GAVariantInfoFields.GENE, doc.get(VariantsSchema.GENE));
        addInfo(variant, GAVariantInfoFields.GENE_EFFECT, doc.get(VariantsSchema.GENE_EFFECT));
        addInfo(variant, GAVariantInfoFields.GENE_HGVS, doc.get(VariantsSchema.GENE_HGVS));

        if (doc.containsKey(VariantsSchema.EXAC_AF)) {
            addInfo(variant, GAVariantInfoFields.EXAC_AF, doc.get(VariantsSchema.EXAC_AF));
        }
        if (doc.containsKey(VariantsSchema.GT_HET)) {
            addInfo(variant, GAVariantInfoFields.GT_HET, doc.get(VariantsSchema.GT_HET));
        }
        if (doc.containsKey(VariantsSchema.GT_HOM)) {
            addInfo(variant, GAVariantInfoFields.GT_HOM, doc.get(VariantsSchema.GT_HOM));
        }

        if (doc.containsKey(VariantsSchema.AC_TOT)) {
            addInfo(variant, GAVariantInfoFields.AC_TOT, doc.get(VariantsSchema.AC_TOT));
        }

        GACall call = new GACall();
        addInfo(call, GACallInfoFields.QUALITY,
                getCallsetField(doc, callsetId, VariantsSchema.QUAL));
        addInfo(call, GACallInfoFields.FILTER,
                getCallsetField(doc, callsetId, VariantsSchema.FILTER));
        addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE,
                getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE,
                getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_PHENO_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE,
                getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE,
                getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_COMBINED_SCORE));
        if ((int) getCallsetField(doc, callsetId, VariantsSchema.AC) == 2) {
            call.setGenotype(Arrays.asList(1, 1));
        } else {
            call.setGenotype(Arrays.asList(0, 1));
        }
        variant.setCalls(Collections.singletonList(call));

        return variant;
    }

    /**
     * Get the value of a field on the doc thats unique to a callset (i.e. not
     * share by two callsets). For example, a variant quality indicator would be
     * specific to an individual's read.
     *
     * @param doc       the doc to add a field do
     * @param callsetId the callset this field belongs to
     * @param fieldName the name of the field
     *
     * @return the value of the field
     */
    public static Object getCallsetField(SolrDocument doc, String callsetId, String fieldName) {
        return doc.get(VariantsSchema.getCallsetsFieldName(callsetId, fieldName));
    }

    /**
     * Turn a GAVariant into a SolrDocument.
     *
     * @param variant the GAVariant
     *
     * @return the SolrDocument
     */
    public static SolrDocument variantToDoc(GAVariant variant) {
        SolrDocument doc = new SolrDocument();

        doc.setField(VariantsSchema.ID, getHash(variant));
        doc.setField(VariantsSchema.CHROM, variant.getReferenceName());
        doc.setField(VariantsSchema.START, variant.getStart());
        doc.setField(VariantsSchema.END, variant.getStart() + variant.getReferenceBases().length());
        doc.setField(VariantsSchema.REF, variant.getReferenceBases());
        doc.setField(VariantsSchema.REF_LENGTH, variant.getReferenceBases().length());
        doc.setField(VariantsSchema.ALT, variant.getAlternateBases().get(0));
        doc.setField(VariantsSchema.ALT_LENGHT, variant.getAlternateBases().get(0).length());
        doc.setField(VariantsSchema.LENGTH, Math.max((int) doc.get(VariantsSchema.REF_LENGTH),
                (int) doc.get(VariantsSchema.ALT_LENGHT)));

        doc.setField(VariantsSchema.GENE, getInfo(variant, GAVariantInfoFields.GENE));
        doc.setField(VariantsSchema.GENE_EFFECT, getInfo(variant, GAVariantInfoFields.GENE_EFFECT));
        doc.setField(VariantsSchema.GENE_HGVS, getInfo(variant, GAVariantInfoFields.GENE_HGVS));

        doc.setField(VariantsSchema.EXAC_AF, safeValueOf(getInfo(variant, GAVariantInfoFields.EXAC_AF)));

        doc.setField(VariantsSchema.AC_TOT, 0);
        doc.setField(VariantsSchema.GT_HET, 0);
        doc.setField(VariantsSchema.GT_HOM, 0);

        // initialize multi-value field so that it clones as an empty list rather than a null value
        doc.setField(VariantsSchema.CALLSET_IDS, Collections.emptyList());
        return doc;
    }

    /**
     * Add callset-specific fields from a variant to an existing document.
     *
     * @param doc       The existing document
     * @param variant   the variant
     * @param callsetId the id of the callset
     * @param isPublic  whether these variants can be used in an aggregate
     *                  search.
     */
    public static void addVariantToDoc(SolrDocument doc, GAVariant variant, String callsetId, boolean isPublic) {
        addMultiFieldValue(doc, VariantsSchema.CALLSET_IDS, callsetId);

        GACall call = variant.getCalls().get(0);
        int copies = 0;
        for (int i : call.getGenotype()) {
            if (i == 1) {
                copies++;
            }
        }
        doc.setField(VariantsSchema.AC_TOT, (int) doc.getFieldValue(VariantsSchema.AC_TOT) + copies);
        if (copies == 1) {
            doc.setField(VariantsSchema.GT_HET, (int) doc.getFieldValue(VariantsSchema.GT_HET) + 1);
        } else if (copies == 2) {
            doc.setField(VariantsSchema.GT_HOM, (int) doc.getFieldValue(VariantsSchema.GT_HOM) + 1);
        }

        setCallsetField(doc, callsetId, VariantsSchema.PUBLIC, isPublic);
        setCallsetField(doc, callsetId, VariantsSchema.AC, copies);
        setCallsetField(doc, callsetId, VariantsSchema.QUAL, getInfo(call, GACallInfoFields.QUALITY));
        setCallsetField(doc, callsetId, VariantsSchema.FILTER, getInfo(call, GACallInfoFields.FILTER));
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE)));
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_PHENO_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE)));
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE)));
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_COMBINED_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE)));
    }

    /**
     * Add a field to a document.
     *
     * @param doc   the SolrDocument
     * @param key   the name of the field where the specified value to be added
     * @param value the specified value to be added
     */
    public static void addMultiFieldValue(SolrDocument doc, String key, Object value) {
        // clone array, sometimes it's unmodifiable
        List<Object> values = new ArrayList<>(doc.getFieldValues(key));
        if (!values.contains(value)) {
            values.add(value);
        }
        doc.setField(key, values);
    }

    /**
     * Set a field on the doc thats unique to a callset (i.e. not share by two
     * callsets). For example, a variant quality indicator would be specific to
     * an individual's read.
     *
     * @param doc       the doc to add a field do
     * @param callsetId the callset this field belongs to
     * @param fieldName the name of the field
     * @param value     the value
     */
    static void setCallsetField(SolrDocument doc, String callsetId, String fieldName, Object value) {
        doc.setField(VariantsSchema.getCallsetsFieldName(callsetId, fieldName), value);
    }

    /**
     * Avoid NullPointerExceptions when parsing doubles.
     *
     * @param s the string
     *
     * @return a double or null
     */
    private static Double safeValueOf(String s) {
        if (s == null) {
            return null;
        }

        return Double.valueOf(s);
    }

    /**
     * Make variant signature from chr + pos + ref + alt.
     *
     * @param variant the variant
     *
     * @return the signature
     */
    public static String getHash(GAVariant variant) {
        return variant.getReferenceName() + ":"
            + variant.getStart().toString() + ":"
            + variant.getReferenceBases() + ":"
            + variant.getAlternateBases().get(0);
    }

    /**
     * Remove a callset from a document.
     *
     * @param doc       the SolrDocument
     * @param callsetId the callsetId to remove
     */
    public static void removeCallsetFromDoc(SolrDocument doc, String callsetId) {

        /*
         *   a. remove the individual's value from multivalued fields (e.g., callsetIds)
         *   b. remove the individual-specific fields (e.g., P0000001_ac)
         *   c. adjust the aggregate counts (e.g., AC_TOT)
         */
        removeMultiFieldValue(doc, VariantsSchema.CALLSET_IDS, callsetId);

        int copies = (int) getCallsetField(doc, callsetId, VariantsSchema.AC);
        doc.setField(VariantsSchema.AC_TOT, (int) doc.getFieldValue(VariantsSchema.AC_TOT) - copies);
        if (copies == 1) {
            doc.setField(VariantsSchema.GT_HET, (int) doc.getFieldValue(VariantsSchema.GT_HET) - 1);
        } else if (copies == 2) {
            doc.setField(VariantsSchema.GT_HOM, (int) doc.getFieldValue(VariantsSchema.GT_HOM) - 1);
        }

        removeCallsetField(doc, callsetId, VariantsSchema.PUBLIC);
        removeCallsetField(doc, callsetId, VariantsSchema.AC);
        removeCallsetField(doc, callsetId, VariantsSchema.QUAL);
        removeCallsetField(doc, callsetId, VariantsSchema.FILTER);
        removeCallsetField(doc, callsetId, VariantsSchema.EXOMISER_VARIANT_SCORE);
        removeCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_PHENO_SCORE);
        removeCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_VARIANT_SCORE);
        removeCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_COMBINED_SCORE);
    }

    /**
     * Remove a field from a document.
     *
     * @param doc   the SolrDocument
     * @param key   the name of the field from which the specified value to be removed
     * @param value the specified value to be removed
     */
    public static void removeMultiFieldValue(SolrDocument doc, String key, Object value) {
        // clone array, sometimes it's unmodifiable
        List<Object> values = new ArrayList<>(doc.getFieldValues(key));
        values.remove(value);
        doc.setField(key, values);
    }

    private static void removeCallsetField(SolrDocument doc, String callsetId, String fieldName) {
        doc.removeFields(VariantsSchema.getCallsetsFieldName(callsetId, fieldName));
        // since callset fields are copied to multivalued fields, we need to remove that too.
        // removeMultiFieldValue(doc, fieldName, value);
    }

    /**
     * Retrieve a metadocument that stores all individual ids.
     *
     * @param server the solr server to assist communication with a Solr server
     *
     * @return a SolrDocument
     * @throws SolrServerException SolrServerException
     * @throws IOException IOException
     */
    public static SolrDocument getMetaDocument(SolrClient server) throws SolrServerException, IOException  {
        SolrDocument metaDoc = server.getById(METADATA_DOC_ID);
        if (metaDoc == null) {
            metaDoc = new SolrDocument();
            metaDoc.setField(VariantsSchema.ID, METADATA_DOC_ID);
            metaDoc.setField(VariantsSchema.CALLSET_IDS, Collections.emptyList());
        }
        return metaDoc;
    }

    /**
     * Add a document to a SolrClient.
     *
     * @param doc    the SolrDocument do be added
     * @param server the solr server to assist communication with a Solr server
     * @throws DatabaseException DatabaseException
     */
    public static void addDoc(SolrInputDocument doc, SolrClient server) throws DatabaseException {
        try {
            server.add(doc);
            doc.clear();
        } catch (SolrServerException | IOException e) {
            throw new DatabaseException("Error adding variants to Solr", e);
        }
    }
}
