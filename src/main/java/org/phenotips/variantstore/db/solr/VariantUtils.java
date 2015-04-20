package org.phenotips.variantstore.db.solr;

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
public class VariantUtils
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

        variant.setReferenceName(doc.get("chrom").toString());
        variant.setReferenceBases(doc.get("ref").toString());
        variant.setStart(Long.valueOf(doc.get("pos").toString()));
        variant.setEnd(variant.getStart() + variant.getReferenceBases().length());
        variant.setAlternateBases((List<String>) doc.get("alts"));

        Map<String, List<String>> info = new HashMap<>();
        info.put("QUAL", Collections.singletonList(doc.get("qual").toString()));
        info.put("FILTER", Collections.singletonList(doc.get("filter").toString()));
        info.put("EXOMISER_VARIANT_SCORE", Collections.singletonList(doc.get("exomiser_variant_score").toString()));
        info.put("EXOMISER_GENE_PHENO_SCORE", Collections.singletonList(doc.get("exomiser_gene_pheno_score").toString()));
        info.put("EXOMISER_GENE_VARIANT_SCORE", Collections.singletonList(doc.get("exomiser_gene_variant_score").toString()));
        info.put("EXOMISER_GENE_COMBINED_SCORE", Collections.singletonList(doc.get("exomiser_gene_combined_score").toString()));

        info.put("GENE", Collections.singletonList(doc.get("gene").toString()));
        info.put("GENE_EFFECT", Collections.singletonList(doc.get("gene_effect").toString()));

        if (doc.containsKey("exac_af")) {
            info.put("EXAC_AF", Collections.singletonList(doc.get("exac_af").toString()));
        }

        variant.setInfo(info);

        return variant;
    }

    /**
     * Turn a GAVariant into a SolrDocument
     * @param variant the GAVariant
     * @return the SolrDocument
     */
    public static SolrDocument variantToDoc(GAVariant variant) {
        SolrDocument doc = new SolrDocument();
        Map<String, List<String>> info = variant.getInfo();

        doc.setField("chrom", variant.getReferenceName());
        doc.setField("pos", variant.getStart());
        doc.setField("ref", variant.getReferenceBases());
        doc.setField("alts", variant.getAlternateBases());

        doc.setField("qual", info.get("QUAL").get(0));
        doc.setField("filter", info.get("FILTER").get(0));

        doc.setField("exomiser_variant_score", Double.valueOf(info.get("EXOMISER_VARIANT_SCORE").get(0)));
        doc.setField("exomiser_gene_pheno_score", info.get("EXOMISER_GENE_PHENO_SCORE").get(0));
        doc.setField("exomiser_gene_variant_score", info.get("EXOMISER_GENE_VARIANT_SCORE").get(0));
        doc.setField("exomiser_gene_combined_score", info.get("EXOMISER_GENE_COMBINED_SCORE").get(0));
        doc.setField("gene", info.get("GENE").get(0));
        doc.setField("gene_effect", info.get("GENE_EFFECT").get(0));

        if (info.containsKey("EXAC_AF")) {
            doc.setField("exac_af", info.get("EXAC_AF").get(0));
        }

        return doc;
    }
}
