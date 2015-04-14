package org.phenotips.variantstore.db.solr.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.ga4gh.GAVariant;
import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.db.DatabaseException;

/**
 * Created by meatcar on 2/24/15.
 */
public class AddIndividualTask implements Callable<Object> {

    private final SolrServer server;
    private final AbstractVariantIterator iterator;

    public AddIndividualTask(SolrServer server, AbstractVariantIterator iterator) {
        this.server = server;
        this.iterator = iterator;
    }

    @Override
    public Object call() throws Exception {
        GAVariant variant;
        Map<String, List<String>> info;

        while (iterator.hasNext()) {
            variant = iterator.next();
            info = variant.getInfo();

            // we will be reusing the document to speed up inserts as per SolrJ docs.
            SolrInputDocument doc = new SolrInputDocument();
            doc.setField("individual", iterator.getHeader().getIndividualId());

            doc.setField("chrom", variant.getReferenceName());
            doc.setField("pos", variant.getStart());
            doc.setField("ref", variant.getReferenceBases());
            doc.setField("alt", StringUtils.join(variant.getAlternateBases(), ","));

            if (iterator.getHeader().isPublic()) {
                doc.setField("is_public", true);
            }

            doc.setField("qual", info.get("QUAL").get(0));
            doc.setField("filter", info.get("FILTER").get(0));

            doc.setField("exomiser_variant_score", Double.valueOf(info.get("EXOMISER_VARIANT_SCORE").get(0)));
            doc.setField("exomiser_gene_pheno_score", info.get("EXOMISER_GENE_PHENO_SCORE").get(0));
            doc.setField("exomiser_gene_variant_score", info.get("EXOMISER_GENE_VARIANT_SCORE").get(0));
            doc.setField("exomiser_gene_combined_score", info.get("EXOMISER_GENE_COMBINED_SCORE").get(0));
            doc.setField("gene", info.get("GENE").get(0));
            doc.setField("gene_effect", info.get("GENE_EFFECT").get(0));

            doc.setField("exac_af", info.get("EXAC_AF").get(0));


            addDoc(doc);
        }

        // Solr should commit the fields at it's own optimal pace.
        // We want to commit once at the end to make sure any leftovers in solr buffers are available for querying.
        server.commit();
        return null;
    }

    private void addDoc(SolrInputDocument doc) throws DatabaseException {
        try {
            server.add(doc);
            doc.clear();
        } catch (SolrServerException | IOException e) {
            throw new DatabaseException(String.format("Error adding variants to Solr"), e);
        }
    }
}
