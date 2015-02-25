package org.phenotips.variantstore.storage.solr;

import java.nio.file.Path;
import java.util.concurrent.*;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.storage.StorageController;
import org.phenotips.variantstore.storage.StorageException;
import org.phenotips.variantstore.storage.solr.tasks.AddIndividualTask;
import org.phenotips.variantstore.storage.solr.tasks.RemoveIndividualTask;

/**
 * Created by meatcar on 2/20/15.
 */
public class SolrController extends StorageController {
    private Logger logger = Logger.getLogger(SolrController.class);
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private CoreContainer cores;
    private SolrServer server;

    public SolrController(Path storePath) {
        super(storePath.resolve("solr/"));
        //TODO: move resources/solr/* to storePath

        // Spin Solr up
        cores = new CoreContainer(this.storePath.toString());
        cores.load();
        server = new EmbeddedSolrServer(cores, "variants");
    }

    @Override
    public void stop() {
        executor.shutdownNow();
        server.shutdown();
        cores.shutdown();
    }

    @Override
    public Future addIndividual(final VariantIterator iterator) {
        FutureTask task = new FutureTask<Object>(new AddIndividualTask(server, iterator));

        executor.submit(task);

        return task;
    }

    @Override
    public Future removeIndividual(String id) throws StorageException {
        FutureTask task = new FutureTask<Object>(new RemoveIndividualTask(server, id));

        executor.submit(task);

        return task;
    }

}
