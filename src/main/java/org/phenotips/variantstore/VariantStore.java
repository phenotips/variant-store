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
            for (String id : Arrays.asList(
                    "F0000009", "F0000010", "F0000011", "F0000012", "F0000013", "F0000027", "F0000028", "F0000030",
                    "F0000031", "F0000032", "F0000033", "F0000034", "F0000035", "F0000036", "F0000039", "F0000040",
                    "F0000041", "F0000042", "F0000044", "F0000045", "F0000047", "F0000048", "F0000051", "F0000052",
                    "P0000054", "P0000055", "P0000056", "P0000057", "P0000058", "P0000059", "P0000060", "P0000061",
                    "P0000063", "P0000064", "P0000066", "P0000068", "P0000069", "P0000071", "P0000073", "P0000088",
                    "P0000089", "P0000091", "P0000093", "P0000095", "P0000097", "P0000098", "P0000099", "P0000100",
                    "P0000105", "P0000106", "P0000108", "P0000109", "P0000110", "P0000112", "P0000118", "P0000119",
                    "P0000120", "P0000121", "P0000122", "P0000123", "P0000124", "P0000160", "P0000161", "P0000162",
                    "P0000163", "P0000164", "P0000168", "P0000169", "P0000170", "P0000174", "P0000175", "P0000176",
                    "P0000192", "P0000193", "P0000194", "P0000196", "P0000197", "P0000198", "P0000199", "P0000200",
                    "P0000204", "P0000205", "P0000206", "P0000207", "P0000210", "P0000211", "P0000212", "P0000213",
                    "P0000215", "P0000216", "P0000221", "P0000223", "P0000229", "P0000230", "P0000231", "P0000233",
                    "P0000235", "P0000236", "P0000237", "P0000238", "P0000239", "P0000240", "P0000241", "P0000243",
                    "P0000245", "P0000246", "P0000247", "P0000250", "P0000251", "P0000253", "P0000293", "P0000294",
                    "P0000295", "P0000296", "P0000297", "P0000298", "P0000304", "P0000307", "P0000308", "P0000310",
                    "P0000311", "P0000312", "P0000314", "P0000317", "P0000318", "P0000319", "P0000320", "P0000321",
                    "P0000322", "P0000323", "P0000324", "P0000325", "P0000326", "P0000327", "P0000328", "P0000329",
                    "P0000330", "P0000331", "P0000333", "P0000334", "P0000335", "P0000336", "P0000338", "P0000340",
                    "P0000342", "P0000347", "P0000348", "P0000351", "P0000352", "P0000353", "P0000354", "P0000355",
                    "P0000357", "P0000358", "P0000359", "P0000361", "P0000365", "P0000367", "P0000454", "P0000455",
                    "P0000460", "P0000463", "P0000464", "P0000465", "P0000475", "P0000476", "P0000482", "P0000744",
                    "P0000746", "P0000747", "P0000749", "P0000750", "P0000751", "P0000752", "P0000753", "P0000754",
                    "P0000889", "P0000890", "P0000891", "P0000892", "P0000893", "P0000902", "P0000904", "P0000905")) {
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
