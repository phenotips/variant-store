package org.phenotips.variantstore.db.solr.tasks;

import org.phenotips.variantstore.db.DatabaseException;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

/**
 * @version $Id$
 */
public class RemoveIndividualTask implements Callable<Object>
{

    private SolrServer server;
    private String id;

    /**
     * Remove an individual from solr.
     * @param server the solr server to run the task on
     * @param id the id of the individual
     */
    public RemoveIndividualTask(SolrServer server, String id) {
        this.server = server;
        this.id = id;
    }

    @Override
    public Object call() throws Exception {
        try {
            server.deleteByQuery(String.format("individual:%s", id));
            server.commit();
        } catch (SolrServerException | IOException e) {
            throw new DatabaseException("Error removing individual from solr", e);
        }
        return null;
    }
}
