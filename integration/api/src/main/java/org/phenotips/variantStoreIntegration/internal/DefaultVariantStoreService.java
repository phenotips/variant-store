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
package org.phenotips.variantStoreIntegration.internal;

import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.variantstore.VariantStore;
import org.phenotips.variantstore.db.solr.SolrController;
import org.phenotips.variantstore.input.tsv.ExomiserTSVManager;
import org.phenotips.variantstore.shared.VariantStoreException;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.environment.Environment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ga4gh.GAVariant;

/**
 * @version $Id$
 */
@Component
@Singleton
public class DefaultVariantStoreService implements Initializable, VariantStoreService
{
    @Inject
    private Environment env;

    private VariantStore variantStore;

    @Override
    public void initialize() throws InitializationException {
        this.variantStore = new VariantStore(
                new ExomiserTSVManager(),
                new SolrController()
        );

        try {
            this.variantStore.init(Paths.get(this.env.getPermanentDirectory().getPath()).resolve("variant-store"));
        } catch (VariantStoreException e) {
            throw new InitializationException("Error setting up Variant Store", e);
        }
    }

    /**
     * Initialize the Service.
     *
     * @param path a path to store resources specific to this service
     *
     * @throws VariantStoreException if an error occurs
     */
    @Override
    public void init(Path path) throws VariantStoreException {
    }

    @Override
    public void stop() {
        this.variantStore.stop();
    }

    /**
     * Add an individual to the variant store. This is an asynchronous operation. In case of application failure, the
     * individual would have to be remove and re-inserted.
     *
     * @param id       a unique ID that represents the individual.
     * @param isPublic whether to include this individual's data in aggregate queries. This does not prevent the data to
     *                 be queried by the individual's ID.
     * @param file     the path to the file on the local filesystem where the data is stored.
     *
     * @return a Future that completes when the individual is fully inserted into the variant store, and is ready to be
     * queried.
     * @throws VariantStoreException If the variant store encountered a problem processing the request.
     */
    @Override
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException {
        return this.variantStore.addIndividual(id, isPublic, file);
    }

    /**
     * Remove any information associated with the specified individual from the variant store.
     *
     * @param id the individual's ID
     *
     * @return a Future that completes when the individual is fully removed from the variant store.
     * @throws VariantStoreException If the variant store encountered a problem processing the request.
     */
    @Override
    public Future removeIndividual(String id) throws VariantStoreException {
        return this.variantStore.removeIndividual(id);
    }

    /**
     * Get the top n most harmful variants for a specified individual.
     *
     * @param id the individuals ID
     * @param n  the number of variants to return
     *
     * @return a List of harmful variants for the specified individual
     */
    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        return this.variantStore.getTopHarmfullVariants(id, n);
    }

    /**
     * Get the individuals that have variants with the given gene symbol, exhibiting the given variant effects, and with
     * the given allele frequencies. Sort the list of patients by descending variant harmfulness
     *
     * @param geneSymbol        the gene symbol
     * @param variantEffects    the variant effects
     * @param alleleFrequencies the allele frequenceis
     *
     * @return a map of individuals and respective variants
     */
    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol, List<String> variantEffects,
                                                               Map<String, Double> alleleFrequencies) {
        return this.variantStore.getIndividualsWithGene(geneSymbol, variantEffects, alleleFrequencies);
    }

    /**
     * Get all the individuals that exhibit the given variant, as well as the variant itself.
     *
     * @param chr the chromosome
     * @param pos the position
     * @param ref the ref
     * @param alt the alt
     *
     * @return a map of individuals and respective variants.
     */
    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt) {
        return null;
    }

    /**
     * Get a list of all the individual IDs stored in the variant store.
     *
     * @return a list of individual IDs.
     */
    @Override
    public List<String> getIndividuals() {
        return this.variantStore.getIndividuals();
    }

    /**
     * Get a list of all genes with variants in the given individual.
     *
     * @param id the patient identifier
     *
     * @return an unmodifiable set of gene names with variants in the individual
     */
    @Override
    public Set<String> getAllGenesForIndividual(String id) {
        return this.variantStore.getAllGenesForIndividual(id);
    }

    @Override
    public Double getGeneScore(String id, String gene) {
        return this.variantStore.getGeneScore(id, gene);
    }

    /**
     * Get a list of at most k variants with a known gene of an individual, sorted by harmfulness.
     *
     * @param id   the individual
     * @param gene the gene
     * @param k    the max number of variants to return
     *
     * @return the list of variants
     */
    @Override
    public List<GAVariant> getTopHarmfullVariantsForGene(String id, String gene, Integer k) {
        return this.variantStore.getTopHarmfullVariantsForGene(id, gene, k);
    }

    /**
     * Get a list of top k harmfull genes for an individual, sorted by harmfulness.
     *
     * @param id the individual's id
     * @param k  the max number of genes to return
     *
     * @return the list of genes
     */
    @Override
    public List<String> getTopGenesForIndividual(String id, Integer k) {
        return this.variantStore.getTopGenesForIndividual(id, k);
    }

}
