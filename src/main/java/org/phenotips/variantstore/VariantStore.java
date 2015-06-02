/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phenotips.variantstore;

import org.phenotips.variantstore.db.DatabaseController;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.tsv.ExomiserTSVManager;
import org.phenotips.variantstore.input.vcf.VCFManager;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;

/**
 * The Variant Store is capable of storing a large number of individuals genomic variants for further
 * querying and sorting.
 *
 * @version $Id$
 */
public class VariantStore implements VariantStoreInterface
{
    private static Logger logger = Logger.getLogger(VariantStore.class);
    private Path path;
    private InputManager inputManager;
    private DatabaseController db;

    /**
     * Use VCF and Solr by default.
     */
    public VariantStore() {
        this.inputManager = new VCFManager();
        this.db = new SolrController();
    }

    /**
     * Specify your own DB and Input format.
     *
     * @param inputManager an input format manager
     * @param db           the db implementation
     */
    public VariantStore(InputManager inputManager, DatabaseController db) {
        this.path = path;
        this.inputManager = inputManager;
        this.db = db;
    }

    /**
     * Main method for testing purposes. TODO:REMOVE
     *
     * @param args commandline args
     */
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
            vs.stop();
            return;
        }

        logger.debug("Started");


        try {
            for (String id : new ArrayList<String>()) {
                //Arrays.asList("F0000010", "F0000011")) {
                vs.addIndividual(id, true, Paths.get("/data/vcf/c4r/tsvs/" + id + ".variants.tsv")).get();
            }
        } catch (InterruptedException | ExecutionException | VariantStoreException e) {
            logger.error("Error", e);
            vs.stop();
            return;
        }

        Map<String, List<GAVariant>> map;

        Map<String, Double> af = new HashMap<>();
        af.put("EXAC", (double) 1.0);
        af.put("PHENOTIPS", (double) 0.5);
        map = vs.getIndividualsWithGene("SRCAP", Arrays.asList("STOPGAIN"), af);
        logger.debug("Individuals w Genes: " + map);

//        try {
//            logger.debug("Removing");
//            vs.removeIndividual(id).get();
//        } catch (InterruptedException | ExecutionException | VariantStoreException e) {
//            logger.error("Eror", e);
//            vs.stop();
//            return;
//        }

        vs.stop();
        logger.debug("Stopped");
    }

    @Override
    public void init(Path path) throws VariantStoreException {
        this.path = path;
        db.init(this.path.resolve("db"));
        inputManager.init(this.path.resolve("tsv"));
    }

    @Override
    public void stop() {
        db.stop();
    }

    @Override
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException {
        logger.debug("Adding " + id);
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
    public Map<String, List<GAVariant>> getIndividualsWithGene(
            String geneSymbol,
            List<String> variantEffects,
            Map<String, Double> alleleFrequencies) {
        return this.db.getIndividualsWithGene(geneSymbol,
                variantEffects,
                alleleFrequencies,
                5,
                this.getIndividuals().size());
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
     * The implementation method for the GA4GH.
     *
     * @param chr    chromosome
     * @param pos    position
     * @param allele allele
     * @return the allele frequency of this variant in the db.
     */
    public double beacon(String chr, int pos, String allele) {
        Map<String, List<GAVariant>> map = this.getIndividualsWithVariant(chr, pos, null, allele);

        if (map.size() == 0) {
            map = this.getIndividualsWithVariant(chr, pos, allele, null);
        }

        //TODO: THIS MATH IS SO WRONG
        return (double) map.size() / (double) this.getIndividuals().size();
    }

}
