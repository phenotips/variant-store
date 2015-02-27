package org.phenotips.variantstore.storage.solr;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.storage.AbstractStorageController;
import org.phenotips.variantstore.storage.StorageException;
import org.phenotips.variantstore.storage.shared.ResourceManager;
import org.phenotips.variantstore.storage.solr.tasks.AddIndividualTask;
import org.phenotips.variantstore.storage.solr.tasks.RemoveIndividualTask;

/**
 * Manages an embedded instance of solr.
 */
public class SolrController extends AbstractStorageController {
    private Logger logger = Logger.getLogger(getClass());

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private CoreContainer cores;
    private SolrServer server;

    /**
     * Create a SolrController, that will store it's files and configuration in a directory inside of rootPath.
     * @param rootPath
     * @throws StorageException
     */
    public SolrController(Path rootPath) throws StorageException {
        super(rootPath);

        ResourceManager.copyResourcesToPath(this.getStoragePathSuffix(), rootPath);

        // Spin Solr up
        logger.debug(this.storePath);
        cores = new CoreContainer(this.storePath.toString());
        cores.load();
        server = new EmbeddedSolrServer(cores, "variants");
    }

    @Override
    protected Path getStoragePathSuffix() {
        return Paths.get("solr/");
    }

    @Override
    public void stop() {
        executor.shutdownNow();
        server.shutdown();
        cores.shutdown();
    }

    @Override
    public Future addIndividual(final AbstractVariantIterator iterator) {
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
