package org.phenotips.variantstore;

import org.phenotips.variantstore.db.AbstractDatabaseController;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.vcf.VCFManager;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;

/**
 * The Variant Store is capable of storing a large number of individuals genomic variants for further
 * querying and sorting
 */
public class VariantStore implements VariantStoreInterface
{
    private static Logger logger = Logger.getLogger(VariantStore.class);
    private Path path;
    private InputManager inputManager;
    private AbstractDatabaseController db;

    /**
     * Use VCF and Solr by default
     */
    public VariantStore() {
        this.inputManager = new VCFManager();
        this.db = new SolrController();
    }

    /**
     * Specify your own DB and Input format
     *
     * @param inputManager
     * @param db
     */
    public VariantStore(InputManager inputManager, AbstractDatabaseController db) {
        this.path = path;
        this.inputManager = inputManager;
        this.db = db;
    }

    @Override
    public void init(Path path) throws VariantStoreException {
        this.path = path;
        db.init(this.path.resolve("db"));
        inputManager.init(this.path.resolve("vcf"));
    }

    public void stop() {
        db.stop();
    }

    @Override
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException {
        // copy file to file cache
        inputManager.addIndividual(id, file);

        return this.db.addIndividual(this.inputManager.getIteratorForIndividual(id, isPublic));
    }

    @Override
    public Future removeIndividual(String id) throws VariantStoreException {
        return this.db.removeIndividual(id);
    }

    /*TODO: other query methods*/

    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        return null;
    }

    public static void main(String[] args) {
        logger.debug("Starting");
        VariantStore vs = null;

        vs = new VariantStore(
                new VCFManager(),
                new SolrController()
        );

        try {
            vs.init(Paths.get("/data/dev-variant-store"));
        } catch (VariantStoreException e) {
            logger.error("Error initializing VariantStore", e);
            return;
        }

        logger.debug("Started");

        String id = "P000001";
        try {
            logger.debug("Adding");
            vs.addIndividual(id, true, Paths.get("/data/vcf/P0000210/P0000210-original.vcf")).get();
            logger.debug("Added.");
            vs.removeIndividual(id).get();
            logger.debug("Removed.");

        } catch (VariantStoreException | ExecutionException e) {
            logger.error("ERROR!!", e);
        } catch (InterruptedException e) {
            logger.error("Shouldn't happen", e);
        }

        vs.stop();
        logger.debug("Stopped");
    }

}
