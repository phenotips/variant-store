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
package org.phenotips.variantStoreIntegration;

import org.phenotips.variantstore.shared.VariantStoreException;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.ga4gh.GAVariant;

/**
 * Service that exposes Phenotip's Variant Store.
 *
 * @version $Id: 47fbeef7d4aac08639f2dc9016b2e6c6d3923293 $
 * @since 1.1M1
 */
@Unstable
@Role
public interface VariantStoreService
{
    /**
     * Safely stop the VariantStore.
     */
    void stop();

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
     * @throws VariantStoreException If the variant store encountered a problem processing the file
     */
    Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException;

    /**
     * Remove any information associated with the specified individual from the variant store.
     *
     * @param id the individual's ID
     * @return a Future that completes when the individual is fully removed from the variant store.
     * @throws VariantStoreException If the variant store encountered a problem processing the file
     */
    Future removeIndividual(String id) throws VariantStoreException;

    /**
     * Get the top n most harmful variants for a specified individual.
     *
     * @param id the individuals ID
     * @param n the number of variants to return
     * @return a List of harmful variants for the specified individual
     */
    List<GAVariant> getTopHarmfullVariants(String id, int n);

    /**
     * Get the individuals that have variants with the given gene symbol, exhibiting the given variant effects, and with
     * the given allele frequencies. Sort the list of patients by descending variant harmfulness
     *
     * @param geneSymbol The gene symbol to be included
     * @param variantEffects A list of effects to be filtered by.
     * @param alleleFrequencies Only variants with allele frequencies less than this will be considered.
     * @return A Map of individual IDs to lists of GAVariants passing the filter params.
     */
    Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol,
        List<String> variantEffects,
        Map<String, Double> alleleFrequencies);

    /**
     * Get a list of all the individual IDs stored in the variant store.
     *
     * @return a list of individual IDs.
     */
    List<String> getIndividuals();
}
