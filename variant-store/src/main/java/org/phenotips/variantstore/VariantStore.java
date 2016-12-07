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
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.variantstore;

import org.phenotips.variantstore.db.DatabaseController;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.vcf.VCFManager;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.ga4gh.GAVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Variant Store is capable of storing a large number of individuals genomic variants for further querying and
 * sorting.
 *
 * @version $Id$
 */
public class VariantStore implements VariantStoreInterface
{
    private static Logger logger = LoggerFactory.getLogger(VariantStore.class);
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
        logger.debug("Adding " + id + " from " + file.toString());
        // copy file to file cache
        inputManager.addIndividual(id, file);

        return this.db.addIndividual(this.inputManager.getIteratorForIndividual(id, isPublic));
    }

    @Override
    public Future removeIndividual(String id) throws VariantStoreException {
        // delete file
        this.inputManager.removeIndividual(id);
        // update variants in db
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
                this.getAllIndividuals().size());
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt) {
        return this.db.getIndividualsWithVariant(chr, pos, ref, alt);
    }

    @Override
    public List<String> getAllIndividuals() {
        return this.db.getAllIndividuals();
    }

    @Override
    public Set<String> getAllGenesForIndividual(String id) {
        return this.db.getAllGenesForIndividual(id);
    }

    @Override
    public Double getGeneScore(String id, String gene) {
        return this.db.getGeneScore(id, gene);
    }

    @Override
    public List<GAVariant> getTopHarmfullVariantsForGene(String id, String gene, Integer k) {
        return this.db.getTopHarmfullVariantsForGene(id, gene, k);
    }

    @Override
    public List<String> getTopGenesForIndividual(String id, Integer k) {
        return this.db.getTopGenesForIndividual(id, k);
    }

    /**
     * The implementation method for the GA4GH.
     *
     * @param chr    chromosome
     * @param pos    position
     * @param allele allele
     *
     * @return the allele frequency of this variant in the db.
     */
    public double beacon(String chr, long pos, String allele) {
        return (double) this.db.beacon(chr, pos, allele) / ((double) this.getAllIndividuals().size() * 2);
    }

    /**
     * Get the total number of variants in the db.
     *
     * @return the total number of variants in the db.
     */
    public long getTotNumVariants() {
        return db.getTotNumVariants();
    }
}
