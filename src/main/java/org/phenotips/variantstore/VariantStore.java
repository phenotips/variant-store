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
        this.inputManager.removeIndividual(id);
        return this.db.removeIndividual(id);
    }

    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        return this.db.getTopHarmfullVariants(id, n);
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol, List<String> variantEffects, Map<String, Double> alleleFrequencies) {
        return this.db.getIndividualsWithGene(geneSymbol, variantEffects, alleleFrequencies, 5);
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt) {
        return this.db.getIndividualsWithVariant(chr, pos, ref, alt);
    }

    @Override
    public List<String> getIndividuals() {
        return this.inputManager.getAllIndividuals();
    }

    /**
     * The implementation method for the GA4GH
     * @param chr
     * @param pos
     * @param allele
     * @return
     */
    public double beacon(String chr, int pos, String allele) {
        Map<String, List<GAVariant>> map = this.getIndividualsWithVariant(chr, pos, null, allele);

        if (map.size() == 0) {
            map = this.getIndividualsWithVariant(chr, pos, allele, null);
        }

        return (double) map.size() / (double) this.getIndividuals().size();
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



        try {
            String id = "F0000009";
            logger.debug("Adding " + id);
            vs.addIndividual(id, true, Paths.get("/data/vcf/c4r/tsvs/" + id + ".variants.tsv")).get();
            logger.debug("Removing");
            vs.removeIndividual(id).get();
        } catch (InterruptedException | ExecutionException | VariantStoreException e) {
            logger.error("Error", e);
        }

        Map<String, List<GAVariant>> map;

        Map<String, Double> af = new HashMap<>();
        af.put("EXAC", (double) 0.1);
        map = vs.getIndividualsWithGene("MED12", Arrays.asList("SPLICING"), af);
        logger.debug("Individuals w Genes: " + map);
        logger.debug("Total individuals: " + vs.getIndividuals().size());



        vs.stop();
        logger.debug("Stopped");
    }

}
