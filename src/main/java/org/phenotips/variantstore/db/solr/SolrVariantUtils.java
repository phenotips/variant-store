package org.phenotips.variantstore.db.solr;

import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.util.ArrayList;
import java.util.Collections;
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
 * Created by meatcar on 4/20/15.
 */
public class SolrVariantUtils
{
    /**
     * Add a GroupResponse to a map where the keys are the values of the grouped-by field,
     * and the values are the list of variants in each group.
     * @param groupResponse the returned group response
     * @param map the map to add the result to
     * @return the passed map
     */
    public static Map<String, List<GAVariant>> appendGroupResponseToMap(GroupResponse groupResponse, Map<String, List<GAVariant>> map) {
        GroupCommand groupCommand = groupResponse.getValues().get(0);

        // no matches, don't do any work.
        if (groupCommand.getMatches() <= 0) {
            return map;
        }

        for (Group group : groupCommand.getValues()) {
            map.put(group.getGroupValue(), appendDocumentListToList(group.getResult(), new ArrayList<GAVariant>()));
        }

        return map;
    }

    /**
     * Add the documents in a SolrDocumentList to a list of GAVariants.
     * @param documentList the SolrDocumentList
     * @param list the list of GAVariants
     * @return the list of GAVariants
     */
    public static List<GAVariant> appendDocumentListToList(SolrDocumentList documentList, List<GAVariant> list) {
        for (SolrDocument doc : documentList) {
            GAVariant variant = docToVariant(doc);

            list.add(variant);
        }

        return list;
    }


    /**
     * Turn a SolrDocument to a GAVariant
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

        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.QUALITY, doc.get(SolrSchema.QUAL));
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.FILTER, doc.get(SolrSchema.FILTER));
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_VARIANT_SCORE, doc.get(SolrSchema.EXOMISER_VARIANT_SCORE));
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE, doc.get(SolrSchema.EXOMISER_GENE_PHENO_SCORE));
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE, doc.get(SolrSchema.EXOMISER_GENE_VARIANT_SCORE));
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE, doc.get(SolrSchema.EXOMISER_GENE_COMBINED_SCORE));
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.GENE, doc.get(SolrSchema.GENE));
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.GENE_EFFECT, doc.get(SolrSchema.GENE_EFFECT));

        if (doc.containsKey(SolrSchema.EXAC_AF)) {
            VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXAC_AF, doc.get(SolrSchema.EXAC_AF));
        }

        return variant;
    }

    /**
     * Turn a GAVariant into a SolrDocument
     * @param variant the GAVariant
     * @return the SolrDocument
     */
    public static SolrDocument variantToDoc(GAVariant variant) {
        SolrDocument doc = new SolrDocument();

        doc.setField(SolrSchema.CHROM, variant.getReferenceName());
        doc.setField(SolrSchema.POS, variant.getStart());
        doc.setField(SolrSchema.REF, variant.getReferenceBases());
        doc.setField(SolrSchema.ALTS, variant.getAlternateBases());

        doc.setField(SolrSchema.QUAL, VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.QUALITY));
        doc.setField(SolrSchema.FILTER, VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.FILTER));

        doc.setField(SolrSchema.EXOMISER_VARIANT_SCORE, Double.valueOf(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_VARIANT_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_PHENO_SCORE, Double.valueOf(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_VARIANT_SCORE, Double.valueOf(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE)));
        doc.setField(SolrSchema.EXOMISER_GENE_COMBINED_SCORE, Double.valueOf(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE)));
        doc.setField(SolrSchema.GENE, VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.GENE));
        doc.setField(SolrSchema.GENE_EFFECT, VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.GENE_EFFECT));

        String value = VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXAC_AF);
        if (value != null) {
            doc.setField(SolrSchema.EXAC_AF, Double.valueOf(value));
        }

        return doc;
    }
}
