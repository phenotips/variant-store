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

import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import static org.phenotips.variantstore.shared.VariantUtils.addInfo;
import static org.phenotips.variantstore.shared.VariantUtils.getInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
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
     * Add a GroupResponse to a map where the keys are the values of the grouped-by field,
     * and the values are the list of variants in each group.
     *
     * @param groupResponse the returned group response
     * @return the passed map
     */
    public static Map<String, List<GAVariant>> groupResponseToMap(GroupResponse groupResponse) {
        GroupCommand groupCommand = groupResponse.getValues().get(0);
        Map<String, List<GAVariant>> map = new HashMap<>();

        // no matches, don't do any work.
        if (groupCommand.getMatches() <= 0) {
            return map;
        }

        for (Group group : groupCommand.getValues()) {
            map.put(group.getGroupValue(), documentListToList(group.getResult()));
        }

        return map;
    }

    /**
     * Add the documents in a SolrDocumentList to a list of GAVariants.
     *
     * @param documentList the SolrDocumentList
     * @return the list of GAVariants
     */
    public static List<GAVariant> documentListToList(SolrDocumentList documentList) {
        List<GAVariant> list = new ArrayList<>();

        for (SolrDocument doc : documentList) {
            GAVariant variant = docToVariant(doc);

            list.add(variant);
        }

        return list;
    }

    /**
     * Turn a SolrDocument to a GAVariant.
     *
     * @param doc the SolrDocument
     * @return a new GAVariant
     */
    public static GAVariant docToVariant(SolrDocument doc) {
        GAVariant variant = new GAVariant();

        variant.setReferenceName(doc.get(VariantsSchema.CHROM).toString());
        variant.setReferenceBases(doc.get(VariantsSchema.REF).toString());
        variant.setStart(Long.valueOf(doc.get(VariantsSchema.POS).toString()));
        variant.setEnd(variant.getStart() + variant.getReferenceBases().length());
        variant.setAlternateBases(Collections.singletonList(doc.get(VariantsSchema.ALT).toString()));

        addInfo(variant, GAVariantInfoFields.GENE, doc.get(VariantsSchema.GENE));
        addInfo(variant, GAVariantInfoFields.GENE_EFFECT, doc.get(VariantsSchema.GENE_EFFECT));

        if (doc.containsKey(VariantsSchema.EXAC_AF)) {
            addInfo(variant, GAVariantInfoFields.EXAC_AF, doc.get(VariantsSchema.EXAC_AF));
        }

        GACall call = new GACall();
        addInfo(call, GACallInfoFields.QUALITY,
                doc.get(VariantsSchema.QUAL));
        addInfo(call, GACallInfoFields.FILTER,
                doc.get(VariantsSchema.FILTER));
        addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE,
                doc.get(VariantsSchema.EXOMISER_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE,
                doc.get(VariantsSchema.EXOMISER_GENE_PHENO_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE,
                doc.get(VariantsSchema.EXOMISER_GENE_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE,
                doc.get(VariantsSchema.EXOMISER_GENE_COMBINED_SCORE));
        variant.setCalls(Collections.singletonList(call));

        variant.setAlternateBases(Collections.singletonList(doc.get(VariantsSchema.ALT).toString()));
        if ((int) doc.get(VariantsSchema.COPIES) == 2) {
            call.setGenotype(Arrays.asList(1, 1));
        } else {
            call.setGenotype(Arrays.asList(0, 1));
        }

        return variant;
    }

    /**
     * Turn a GAVariant into a SolrDocument.
     *
     * @param variant the GAVariant
     * @return the SolrDocument
     */
    public static SolrDocument variantToDoc(GAVariant variant) {
        SolrDocument doc = new SolrDocument();

        doc.setField(VariantsSchema.CHROM, variant.getReferenceName());
        doc.setField(VariantsSchema.POS, variant.getStart());
        doc.setField(VariantsSchema.REF, variant.getReferenceBases());
        doc.setField(VariantsSchema.ALT, variant.getAlternateBases().get(0));

        doc.setField(VariantsSchema.GENE, getInfo(variant, GAVariantInfoFields.GENE));
        doc.setField(VariantsSchema.GENE_EFFECT, getInfo(variant, GAVariantInfoFields.GENE_EFFECT));

        doc.setField(VariantsSchema.EXAC_AF, safeValueOf(getInfo(variant, GAVariantInfoFields.EXAC_AF)));

        GACall call = variant.getCalls().get(0);
        int copies = 0;
        for (int i : call.getGenotype()) {
            if (i == 1) {
                copies++;
            }
        }
        doc.setField(VariantsSchema.COPIES, copies);
        doc.setField(VariantsSchema.QUAL, getInfo(call, GACallInfoFields.QUALITY));
        doc.setField(VariantsSchema.FILTER, getInfo(call, GACallInfoFields.FILTER));

        doc.setField(VariantsSchema.EXOMISER_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE)));
        doc.setField(VariantsSchema.EXOMISER_GENE_PHENO_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE)));
        doc.setField(VariantsSchema.EXOMISER_GENE_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE)));
        doc.setField(VariantsSchema.EXOMISER_GENE_COMBINED_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE)));

        return doc;
    }

    /**
     * Avoid NullPointerExceptions when parsing doubles.
     *
     * @param s the string
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
     * @param variant the variant
     * @param callsetID the callset identifier
     * @return the signature
     */
    public static String getHash(GAVariant variant, String callsetID) {
        Lookup3Signature signatureBuilder = new Lookup3Signature();
        signatureBuilder.add(variant.getReferenceName());
        signatureBuilder.add(variant.getStart().toString());
        signatureBuilder.add(variant.getReferenceBases());
        signatureBuilder.add(variant.getAlternateBases().get(0));
        signatureBuilder.add(callsetID);

        return byteArrayToString(signatureBuilder.getSignature());
    }

    /**
     * Make variant signature from chr + pos + ref + alt.
     * @param variant the variant
     * @return the signature
     */
    public static String getVariantSignature(GAVariant variant) {
        Lookup3Signature signatureBuilder = new Lookup3Signature();
        signatureBuilder.add(variant.getReferenceName());
        signatureBuilder.add(variant.getStart().toString());
        signatureBuilder.add(variant.getReferenceBases());
        signatureBuilder.add(variant.getAlternateBases().get(0));

        return byteArrayToString(signatureBuilder.getSignature());
    }

    /**
     * Turns bytes[] into a hex String.
     * Copied from {@link org.apache.solr.update.processor.SignatureUpdateProcessorFactory}.
     * @param bytes a byte array
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
