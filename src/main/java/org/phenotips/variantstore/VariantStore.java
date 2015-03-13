package org.phenotips.variantstore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.csv.CSVManager;
import org.phenotips.variantstore.db.AbstractDatabaseController;
import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.vcf.VCFManager;

/**
 * The Variant Store is capable of storing a large number of individuals genomic variants for further
 * querying and sorting
 */
public class VariantStore {
    private static Logger logger = Logger.getLogger(VariantStore.class);
    private final Path path;
    private final VCFManager vcf;
    private final JannovarController jannovar;
    private InputManager inputManager;
    private AbstractDatabaseController db;

    public VariantStore(Path path, InputManager inputManager, AbstractDatabaseController db) {
        this.path = path;
        this.inputManager = inputManager;
        this.db = db;
        this.vcf = new VCFManager();
        this.jannovar = new JannovarController();
    }

    public void init() throws VariantStoreException {
//        db.init(this.path.resolve("db"));
        vcf.init(this.path.resolve("vcf"));
        jannovar.init(this.path.resolve("jannovar"));
    }

    public void stop() {
        db.stop();
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
        // copy file to file cache
        vcf.addIndividual(id, file);

        // annotate VCF with jannovar
        jannovar.annotate(vcf.getIndividual(id));
        // filter down to exonic variants
        // run them through exomiser
        // add them to solr
        // add all variants to solr
//        return this.db.addIndividual(this.inputHandler.getIteratorForFile(file, id, isPublic));
        return null;
    }

    /**
     * Remove any information associated with the specified individual from the variant store
     * @param id the individual's id
     * @return a Future that completes when the individual is fully removed from the variant store.
     */
    public Future removeIndividual(String id) throws VariantStoreException {
        return this.db.removeIndividual(id);
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

    public static void main(String[] args) throws DatabaseException {
        logger.debug("Starting");
        VariantStore vs = null;

        vs = new VariantStore(Paths.get("/data/dev-variant-store"),
                new CSVManager(),
                new SolrController()
        );

        try {
            vs.init();
        } catch (VariantStoreException e) {
            e.printStackTrace();
        }

        logger.debug("Started");

        String id = "P000001";
        try {
            logger.debug("Adding");
            vs.addIndividual(id, true, Paths.get("/data/vcf/completegenomics/vcfBeta-HG00731-200-37-ASM.vcf.gz")).get();
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
