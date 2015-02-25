package org.phenotips.variantstore.storage.solr.tasks;

import java.io.IOException;
import java.util.concurrent.Callable;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.phenotips.variantstore.storage.StorageException;

/**
 * Created by meatcar on 2/24/15.
 */
public class RemoveIndividualTask implements Callable<Object> {

    private SolrServer server;
    private String id;

    public RemoveIndividualTask(SolrServer server, String id) {
        this.server = server;
        this.id = id;
    }

    @Override
    public Object call() throws Exception {
        try {
            server.deleteByQuery(String.format("patient:%s", id));
        } catch (SolrServerException e) {
            throw new StorageException(String.format("Error removing individual from solr"), e);
        } catch (IOException e) {
            throw new StorageException(String.format("Error removing individual from solr"), e);
        }
        return null;
    }
}
