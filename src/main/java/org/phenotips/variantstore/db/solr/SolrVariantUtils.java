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

import org.phenotips.variantstore.shared.GAVariantInfoFields;
import static org.phenotips.variantstore.shared.VariantUtils.addInfoToVariant;
import static org.phenotips.variantstore.shared.VariantUtils.getInfoFromVariant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
        variant.setAlternateBases((List<String>) doc.get(SolrSchema.ALTS));

        addInfoToVariant(variant,
                GAVariantInfoFields.QUALITY, doc.get(SolrSchema.QUAL));
        addInfoToVariant(variant,
                GAVariantInfoFields.FILTER, doc.get(SolrSchema.FILTER));
        addInfoToVariant(variant,
                GAVariantInfoFields.EXOMISER_VARIANT_SCORE, doc.get(SolrSchema.EXOMISER_VARIANT_SCORE));
        addInfoToVariant(variant,
                GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE, doc.get(SolrSchema.EXOMISER_GENE_PHENO_SCORE));
        addInfoToVariant(variant,
                GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE, doc.get(SolrSchema.EXOMISER_GENE_VARIANT_SCORE));
        addInfoToVariant(variant,
                GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE, doc.get(SolrSchema.EXOMISER_GENE_COMBINED_SCORE));
        addInfoToVariant(variant,
                GAVariantInfoFields.GENE, doc.get(SolrSchema.GENE));
        addInfoToVariant(variant,
                GAVariantInfoFields.GENE_EFFECT, doc.get(SolrSchema.GENE_EFFECT));

        if (doc.containsKey(SolrSchema.EXAC_AF)) {
            addInfoToVariant(variant, GAVariantInfoFields.EXAC_AF, doc.get(SolrSchema.EXAC_AF));
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

        doc.setField(SolrSchema.CHROM, variant.getReferenceName());
        doc.setField(SolrSchema.POS, variant.getStart());
        doc.setField(SolrSchema.REF, variant.getReferenceBases());
        doc.setField(SolrSchema.ALTS, variant.getAlternateBases());

        doc.setField(SolrSchema.QUAL, getInfoFromVariant(variant, GAVariantInfoFields.QUALITY));
        doc.setField(SolrSchema.FILTER, getInfoFromVariant(variant, GAVariantInfoFields.FILTER));

        doc.setField(SolrSchema.EXOMISER_VARIANT_SCORE,
                Double.valueOf(getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_VARIANT_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_PHENO_SCORE,
                Double.valueOf(getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_VARIANT_SCORE,
                Double.valueOf(getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_COMBINED_SCORE,
                Double.valueOf(getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE)));
        doc.setField(SolrSchema.GENE, getInfoFromVariant(variant, GAVariantInfoFields.GENE));
        doc.setField(SolrSchema.GENE_EFFECT, getInfoFromVariant(variant, GAVariantInfoFields.GENE_EFFECT));

        String value = getInfoFromVariant(variant, GAVariantInfoFields.EXAC_AF);
        if (value != null) {
            doc.setField(SolrSchema.EXAC_AF, Double.valueOf(value));
        }

        return doc;
    }
}
