/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.ga4gh.GAVariant;

/**
 * @version $Id: 1f3bc36ff53b79ba95f90d7f1eaa24fa48d6bf4a $
 */
@Component
public class DefaultVariantStoreService implements Initializable, VariantStoreService
{
    @Inject
    private Environment env;

    private VariantStore variantStore;

    @Override
    public void initialize() throws InitializationException
    {
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

    @Override
    public void stop()
    {
        this.variantStore.stop();
    }

    /**
     * Add an individual to the variant store. This is an asynchronous operation. In case of application failure, the
     * individual would have to be remove and re-inserted.
     *
     * @param id a unique ID that represents the individual.
     * @param isPublic whether to include this individual's data in aggregate queries. This does not prevent the data to
     *            be queried by the individual's ID.
     * @param file the path to the file on the local filesystem where the data is stored.
     * @return a Future that completes when the individual is fully inserted into the variant store, and is ready to be
     *         queried.
     */
    @Override
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException
    {
        return this.variantStore.addIndividual(id, isPublic, file);
    }

    /**
     * Remove any information associated with the specified individual from the variant store
     *
     * @param id the individual's ID
     * @return a Future that completes when the individual is fully removed from the variant store.
     */
    @Override
    public Future removeIndividual(String id) throws VariantStoreException
    {
        return this.variantStore.removeIndividual(id);
    }

    /**
     * Get the top n most harmful variants for a specified individual.
     *
     * @param id the individuals ID
     * @param n the number of variants to return
     * @return a List of harmful variants for the specified individual
     */
    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n)
    {
        List<GAVariant> rawVs = this.variantStore.getTopHarmfullVariants(id, n);

        return rawVs;
    }

    /**
     * Get the individuals that have variants with the given gene symbol, exhibiting the given variant effects, and with
     * the given allele frequencies. Sort the list of patients by descending variant harmfulness
     *
     * @param geneSymbol
     * @param variantEffects
     * @param alleleFrequencies
     * @return
     */
    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol, List<String> variantEffects,
        Map<String, Double> alleleFrequencies)
    {
        Map<String, List<GAVariant>> raw =
            this.variantStore.getIndividualsWithGene(geneSymbol, variantEffects, alleleFrequencies);

        return raw;
    }

    /**
     * Get all the individuals that exhibit the given variant, as well as the variant itself.
     *
     * @param chr
     * @param pos
     * @param ref
     * @param alt
     * @return
     */
    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt)
    {
        Map<String, List<GAVariant>> raw = this.variantStore.getIndividualsWithVariant(chr, pos, ref, alt);

        return raw;
    }

    /**
     * Get a list of all the individual IDs stored in the variant store.
     *
     * @return a list of individual IDs.
     */
    @Override
    public List<String> getIndividuals()
    {
        return this.variantStore.getIndividuals();
    }

}
