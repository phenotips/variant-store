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

        variant.setReferenceName(doc.get(SolrSchema.CHROM).toString());
        variant.setReferenceBases(doc.get(SolrSchema.REF).toString());
        variant.setStart(Long.valueOf(doc.get(SolrSchema.POS).toString()));
        variant.setEnd(variant.getStart() + variant.getReferenceBases().length());

        addInfo(variant, GAVariantInfoFields.GENE, doc.get(SolrSchema.GENE));
        addInfo(variant, GAVariantInfoFields.GENE_EFFECT, doc.get(SolrSchema.GENE_EFFECT));

        if (doc.containsKey(SolrSchema.EXAC_AF)) {
            addInfo(variant, GAVariantInfoFields.EXAC_AF, doc.get(SolrSchema.EXAC_AF));
        }

        GACall call = new GACall();
        addInfo(call, GACallInfoFields.QUALITY, doc.get(SolrSchema.QUAL));
        addInfo(call, GACallInfoFields.FILTER, doc.get(SolrSchema.FILTER));
        addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE, doc.get(SolrSchema.EXOMISER_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE, doc.get(SolrSchema.EXOMISER_GENE_PHENO_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE, doc.get(SolrSchema.EXOMISER_GENE_VARIANT_SCORE));
        addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE, doc.get(SolrSchema.EXOMISER_GENE_COMBINED_SCORE));
        variant.setCalls(Collections.singletonList(call));

        List<String> alts;
        if (doc.get(SolrSchema.COPIES) == 2) {
            alts = Arrays.asList(doc.get(SolrSchema.ALT).toString(), doc.get(SolrSchema.ALT).toString());
            call.setGenotype(Arrays.asList(1, 1));
        } else {
            alts = Collections.singletonList(doc.get(SolrSchema.ALT).toString());
            call.setGenotype(Arrays.asList(0, 1));
        }
        variant.setAlternateBases(alts);

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

        doc.setField(SolrSchema.CHROM, variant.getReferenceName());
        doc.setField(SolrSchema.POS, variant.getStart());
        doc.setField(SolrSchema.REF, variant.getReferenceBases());

        doc.setField(SolrSchema.GENE, getInfo(variant, GAVariantInfoFields.GENE));
        doc.setField(SolrSchema.GENE_EFFECT, getInfo(variant, GAVariantInfoFields.GENE_EFFECT));

        String value = getInfo(variant, GAVariantInfoFields.EXAC_AF);
        if (value != null) {
            doc.setField(SolrSchema.EXAC_AF, Double.valueOf(value));
        }

        GACall call = variant.getCalls().get(0);
        doc.setField(SolrSchema.QUAL, getInfo(call, GACallInfoFields.QUALITY));
        doc.setField(SolrSchema.FILTER, getInfo(call, GACallInfoFields.FILTER));

        doc.setField(SolrSchema.EXOMISER_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_PHENO_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_VARIANT_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_COMBINED_SCORE,
                safeValueOf(getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE)));

        return doc;
    }

    /**
     * Given a single variant, create at least one doc, where each doc has a single alt from the variant, as well
     * as the number of copies that of that alt that the variant exhibits.
     * @param variant the GAVariant
     * @return a list of solr documents
     */
    public static List<SolrDocument> variantToDocs(GAVariant variant) {
        Map<String, SolrDocument> map = new HashMap<>();
        for (int i : variant.getCalls().get(0).getGenotype()) {
            // genotype field has 1-based indeces. Convert them to 0-based
            i -= 1;
            String alt = variant.getAlternateBases().get(i);
            SolrDocument doc;

            if (map.containsKey(alt)) {
                doc = map.get(alt);
                doc.setField(SolrSchema.COPIES, (int) doc.getFieldValue(SolrSchema.COPIES) + 1);
            } else {
                doc = variantToDoc(variant);
                doc.setField(SolrSchema.ALT, alt);
                doc.setField(SolrSchema.COPIES, 1);
                map.put(alt, doc);
            }
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Avoid NullPointerExceptions when parsing doubles
     * @param s the string
     * @return a double or null
     */
    private static Double safeValueOf(String s) {
        if (s == null) {
            return null;
        }

        return Double.valueOf(s);
    }
}
