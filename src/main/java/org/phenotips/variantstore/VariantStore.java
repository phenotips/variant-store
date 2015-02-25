package org.phenotips.variantstore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputHandler;
import org.phenotips.variantstore.input.csv.CSVHandler;
import org.phenotips.variantstore.storage.StorageController;
import org.phenotips.variantstore.storage.StorageException;
import org.phenotips.variantstore.storage.solr.SolrController;

/**
 * The Variant Store is capable of storing a large number of individuals genomic variants for further
 * querying and sorting
 */
public class VariantStore {
    private static Logger logger = Logger.getLogger(VariantStore.class);
    private InputHandler inputHandler;
    private StorageController storageController;

    public VariantStore(InputHandler inputHandler, StorageController storageController) {
        this.inputHandler = inputHandler;
        this.storageController = storageController;
    }

    public void stop() {
        storageController.stop();
    }

    /**
     * Add an individual to the variant store. This is an asynchronous operation.
     * In case of application failure, the individual would have to be remove and re-inserted.
     * @param id a unique id that represents the individual.
     * @param isPublic whether to include this individual's data in aggregate queries.
     *                 This does not prevent the data to be queried by the individual's id.
     * @param file the path to the file on the local filesystem where the data is stored.
     * @return a Future that completes when the individual is fully inserted into the variant store,
     *         and is ready to be queried.
     */
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException {
        return this.storageController.addIndividual(this.inputHandler.getIteratorForFile(file, id, isPublic));
    }

    /**
     * Remove any information associated with the specified individual from the variant store
     * @param id the individual's id
     * @return a Future that completes when the individual is fully removed from the variant store.
     */
    public Future removeIndividual(String id) throws VariantStoreException {
        return this.storageController.removeIndividual(id);
    }

    /**
     * Get the top n most harmful variants for a specified individual.
     * @param id the individuals id
     * @param n the number of variants to return
     * @return a List of harmful variants for the specified individual
     */
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        return null;
    }

    /*TODO: other query methods*/

    public static void main(String[] args) {
        VariantStore vs = new VariantStore(
                new CSVHandler(),
                new SolrController(Paths.get("/data/"))
        );

        logger.debug("Started");

        String id = "P000001";
        try {
            vs.addIndividual(id, true, Paths.get("/data/vcf/completegenomics/vcfBeta-HG00731-200-37-ASM.csv")).get();
            logger.debug("Added.");
            vs.removeIndividual(id).get();
            logger.debug("Removed.");
        } catch (VariantStoreException e) {
            logger.error("ERROR!!", e);
        } catch (InterruptedException e) {
            logger.error("Shouldn't happen", e);
        } catch (ExecutionException e) {
            logger.error("ERROR!!", e);
        }

        vs.stop();
        logger.debug("Stopped");
    }
}
