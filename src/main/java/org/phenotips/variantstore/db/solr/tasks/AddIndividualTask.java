package org.phenotips.variantstore.db.solr.tasks;

import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.db.solr.SolrVariantUtils;
import org.phenotips.variantstore.input.VariantIterator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.ga4gh.GAVariant;

/**
 * @version $Id$
 */
public class AddIndividualTask implements Callable<Object>
{

    private final SolrServer server;
    private final VariantIterator iterator;

    /**
     * Initialize the task.
     * @param server the SolrServer to run the task on
     * @param iterator the variants to add
     */
    public AddIndividualTask(SolrServer server, VariantIterator iterator) {
        this.server = server;
        this.iterator = iterator;
    }

    @Override
    public Object call() throws Exception {
        GAVariant variant;
        Map<String, List<String>> info;

        while (iterator.hasNext()) {
            variant = iterator.next();

            SolrInputDocument doc = ClientUtils.toSolrInputDocument(SolrVariantUtils.variantToDoc(variant));
            // we will be reusing the document to speed up inserts as per SolrJ docs.

            doc.setField("individual", iterator.getHeader().getIndividualId());

            if (iterator.getHeader().isPublic()) {
                doc.setField("is_public", true);
            }

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
