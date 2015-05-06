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

import org.phenotips.variantstore.shared.Service;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.ga4gh.GAVariant;

/**
 * @version $Id$
 */
public interface VariantStoreInterface extends Service
{
    /**
     * Add an individual to the variant store. This is an asynchronous operation.
     * In case of application failure, the individual would have to be remove and re-inserted.
     *
     * @param id       a unique ID that represents the individual.
     * @param isPublic whether to include this individual's data in aggregate queries.
     *                 This does not prevent the data to be queried by the individual's ID.
     * @param file     the path to the file on the local filesystem where the data is stored.
     * @throws VariantStoreException when an error occurs.
     * @return a Future that completes when the individual is fully inserted into the variant store,
     * and is ready to be queried.
     */
    Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException;

    /**
     * Remove any information associated with the specified individual from the variant store.
     *
     * @param id the individual's ID
     * @throws VariantStoreException when an error occurs.
     * @return a Future that completes when the individual is fully removed from the variant store.
     */
    Future removeIndividual(String id) throws VariantStoreException;

    /**
     * Get the top n most harmful variants for a specified individual.
     *
     * @param id the individuals ID
     * @param n  the number of variants to return
     * @return a List of harmful variants for the specified individual
     */
    List<GAVariant> getTopHarmfullVariants(String id, int n);

    /**
     * Get the individuals that have variants with the given gene symbol, exhibiting the given variant effects,
     * and with the given allele frequencies. Sort the list of patients by descending variant harmfulness
     * @param geneSymbol the gene symbol
     * @param variantEffects the variant effects
     * @param alleleFrequencies the allele frequenceis
     * @return a map of individuals and respective variants
     */
    Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol,
                                                     List<String> variantEffects,
                                                     Map<String, Double> alleleFrequencies);

    /**
     * Get all the individuals that exhibit the given variant, as well as the variant itself.
     * @param chr the chromosome
     * @param pos the position
     * @param ref the ref
     * @param alt the alt
     * @return a map of individuals and respective variants.
     */
    Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt);

    /**
     * Get a list of all the individual IDs stored in the variant store.
     * @return a list of individual IDs.
     */
    List<String> getIndividuals();
}
