package org.phenotips.variantstore;

import org.phenotips.variantstore.db.AbstractDatabaseController;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.tsv.ExomiserTSVManager;
import org.phenotips.variantstore.input.vcf.VCFManager;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
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
        inputManager.init(this.path.resolve("tsv"));
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

    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        return this.db.getTopHarmfullVariants(id, n);
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol, List<String> variantEffects, Map<String, Double> alleleFrequencies) {
        Map<String, List<GAVariant>> map = new HashMap<>();

        for (String id: this.getIndividuals()) {
            map.put(id, this.db.getTopHarmfulWithGene(id, 5, geneSymbol, variantEffects, alleleFrequencies));
        }

        return map;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt) {
        return this.db.getIndividualsWithVariant(chr, pos, ref, alt);
    }

    @Override
    public List<String> getIndividuals() {
        return this.inputManager.getAllIndividuals();
    }

    public static void main(String[] args) {
        logger.debug("Starting");
        VariantStore vs = null;

        vs = new VariantStore(
                new ExomiserTSVManager(),
                new SolrController()
        );

        try {
            vs.init(Paths.get("/data/dev-variant-store"));
        } catch (VariantStoreException e) {
            logger.error("Error initializing VariantStore", e);
            return;
        }

        logger.debug("Started");

        final List<String> ids = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get("/data/vcf/c4r/tsvs/"), new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isDirectory()) {
                        return FileVisitResult.CONTINUE;
                    }
                    String id = file.getFileName().toString();
                    id = StringUtils.removeEnd(id, ".variant.tsv");
                    ids.add(id);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Error getting all individuals", e);
        }

        try {
            for (String id: ids) {
                Path path = Paths.get("/data/vcf/c4r/vcfs/" + id + ".variants.tsv");
                logger.debug("Adding " + path);
                vs.addIndividual(id, true, path).get();
                logger.debug("Added.");
            }


//            vs.removeIndividual(id).get();
//            logger.debug("Removed.");
        } catch (VariantStoreException | ExecutionException e) {
            logger.error("ERROR!!", e);
        } catch (InterruptedException e) {
            logger.error("Shouldn't happen", e);
        }

        vs.getIndividualsWithVariant("chr1", 246859033, "AGTGT", "AGTGTGT");
        Map<String, Double> af = new HashMap<>();
        af.put("EXAC", (double) 0.1);
        vs.getIndividualsWithGene("CNST", Arrays.asList("MISSENSE", "INTERGENIC"), af);

        vs.stop();
        logger.debug("Stopped");
    }

}
