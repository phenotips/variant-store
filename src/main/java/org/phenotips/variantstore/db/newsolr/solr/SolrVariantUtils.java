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
package org.phenotips.variantstore.db.newsolr.solr;

import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import static org.phenotips.variantstore.shared.VariantUtils.addInfo;
import static org.phenotips.variantstore.shared.VariantUtils.getInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.StrUtils;
import org.apache.solr.update.processor.Lookup3Signature;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;

/**
 * @version $Id$
 */
public final class SolrVariantUtils
{
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
     * Get all the individual variants out of a doc.
     *
     * @param doc the doc
     *
     * @return a map of callsetid to variant
     */
    public static Map<String, GAVariant> docToVariantMap(SolrDocument doc) {
        Map<String, GAVariant> map = new HashMap<>();

        for (String callsetId : (List<String>) doc.get(NewVariantsSchema.CALLSET_IDS)) {
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
        variant.setReferenceName(doc.get(NewVariantsSchema.CHROM).toString());
        variant.setReferenceBases(doc.get(NewVariantsSchema.REF).toString());
        variant.setStart(Long.valueOf(doc.get(NewVariantsSchema.START).toString()));
        variant.setEnd(Long.valueOf(doc.get(NewVariantsSchema.END).toString()));
        variant.setAlternateBases(Collections.singletonList(doc.get(NewVariantsSchema.ALT).toString()));

        addInfo(variant, GAVariantInfoFields.GENE, doc.get(NewVariantsSchema.GENE));
        addInfo(variant, GAVariantInfoFields.GENE_EFFECT, doc.get(NewVariantsSchema.GENE_EFFECT));

        if (doc.containsKey(NewVariantsSchema.EXAC_AF)) {
            addInfo(variant, GAVariantInfoFields.EXAC_AF, doc.get(NewVariantsSchema.EXAC_AF));
        }

        GACall call = new GACall();
        addInfo(call, GACallInfoFields.QUALITY,
                getCallsetField(doc, callsetId, NewVariantsSchema.QUAL));
        addInfo(call, GACallInfoFields.FILTER,
                getCallsetField(doc, callsetId, NewVariantsSchema.FILTER));
        addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE,
                getCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE,
                getCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_GENE_PHENO_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE,
                getCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_GENE_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE,
                getCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_GENE_COMBINED_SCORE));
        variant.setCalls(Collections.singletonList(call));

        variant.setAlternateBases(Collections.singletonList(doc.get(NewVariantsSchema.ALT).toString()));
        if ((int) getCallsetField(doc, callsetId, NewVariantsSchema.AC) == 2) {
            call.setGenotype(Arrays.asList(1, 1));
        } else {
            call.setGenotype(Arrays.asList(0, 1));
        }

        return variant;
    }

    private static Object getCallsetField(SolrDocument doc, String callsetId, String fieldName) {
        return doc.get(NewVariantsSchema.getCallsetsFieldName(callsetId, fieldName));
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

        doc.setField(NewVariantsSchema.HASH, getHash(variant));
        doc.setField(NewVariantsSchema.CHROM, variant.getReferenceName());
        doc.setField(NewVariantsSchema.START, variant.getStart());
        doc.setField(NewVariantsSchema.END, variant.getStart() + variant.getReferenceBases().length());
        doc.setField(NewVariantsSchema.REF, variant.getReferenceBases());
        doc.setField(NewVariantsSchema.REF_LENGTH, variant.getReferenceBases().length());
        doc.setField(NewVariantsSchema.ALT, variant.getAlternateBases().get(0));
        doc.setField(NewVariantsSchema.ALT_LENGHT, variant.getAlternateBases().get(0).length());
        doc.setField(NewVariantsSchema.LENGTH, Math.max((int) doc.get(NewVariantsSchema.REF_LENGTH),
                (int) doc.get(NewVariantsSchema.ALT_LENGHT)));

        doc.setField(NewVariantsSchema.GENE, getInfo(variant, GAVariantInfoFields.GENE));
        doc.setField(NewVariantsSchema.GENE_EFFECT, getInfo(variant, GAVariantInfoFields.GENE_EFFECT));

        doc.setField(NewVariantsSchema.EXAC_AF, safeValueOf(getInfo(variant, GAVariantInfoFields.EXAC_AF)));

        doc.setField(NewVariantsSchema.AC_TOT, 0);
        doc.setField(NewVariantsSchema.GT_HET, 0);
        doc.setField(NewVariantsSchema.GT_HOM, 0);
        return doc;
    }

    /**
     * Add callset-specific fields from a variant to an existing document.
     *
     * @param doc       The existing document
     * @param variant   the variant
     * @param callsetId the id of the callset
     * @param isPublic  whether these variants can be used in an aggregate search.
     */
    public static void addVariantToDoc(SolrDocument doc, GAVariant variant, String callsetId, boolean isPublic) {
        doc.setField(NewVariantsSchema.CALLSET_IDS, callsetId);

        GACall call = variant.getCalls().get(0);
        int copies = 0;
        for (int i : call.getGenotype()) {
            if (i == 1) {
                copies++;
            }
        }
        doc.setField(NewVariantsSchema.AC_TOT, (int) doc.getFieldValue(NewVariantsSchema.AC_TOT) + copies);
        if (copies == 1) {
            doc.setField(NewVariantsSchema.AC_TOT, (int) doc.getFieldValue(NewVariantsSchema.GT_HET) + 1);
        } else if (copies == 2) {
            doc.setField(NewVariantsSchema.AC_TOT, (int) doc.getFieldValue(NewVariantsSchema.GT_HOM) + 1);
        }

        setCallsetField(doc, callsetId, NewVariantsSchema.PUBLIC, isPublic);
        setCallsetField(doc, callsetId, NewVariantsSchema.AC, copies);
        setCallsetField(doc, callsetId, NewVariantsSchema.QUAL, getInfo(call, GACallInfoFields.QUALITY));
        setCallsetField(doc, callsetId, NewVariantsSchema.FILTER, getInfo(call, GACallInfoFields.FILTER));
        setCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE)));
        setCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_GENE_PHENO_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE)));
        setCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_GENE_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE)));
        setCallsetField(doc, callsetId, NewVariantsSchema.EXOMISER_GENE_COMBINED_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE)));
    }

    private static void setCallsetField(SolrDocument doc, String callsetId, String fieldName, Serializable value) {
        doc.setField(NewVariantsSchema.getCallsetsFieldName(callsetId, fieldName), value);
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
        Lookup3Signature signatureBuilder = new Lookup3Signature();
        signatureBuilder.add(variant.getReferenceName());
        signatureBuilder.add(variant.getStart().toString());
        signatureBuilder.add(variant.getReferenceBases());
        signatureBuilder.add(variant.getAlternateBases().get(0));

        return byteArrayToString(signatureBuilder.getSignature());
    }

    /**
     * Turns bytes[] into a hex String. Copied from {@link org.apache.solr.update.processor.SignatureUpdateProcessorFactory}.
     *
     * @param bytes a byte array
     *
     * @return a String
     */
    private static String byteArrayToString(byte[] bytes) {
        char[] arr = new char[bytes.length << 1];

        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            int idx = i << 1;
            arr[idx] = StrUtils.HEX_DIGITS[(b >> 4) & 0xf];
            arr[idx + 1] = StrUtils.HEX_DIGITS[b & 0xf];
        }
        return new String(arr);

    }
}
