package org.phenotips.variantstore;

import org.phenotips.variantstore.shared.Service;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

import org.ga4gh.GAVariant;

/**
 * Created by meatcar on 4/14/15.
 */
public interface VariantStoreInterface extends Service
{
    /**
     * Add an individual to the variant store. This is an asynchronous operation.
     * In case of application failure, the individual would have to be remove and re-inserted.
     *
     * @param id       a unique id that represents the individual.
     * @param isPublic whether to include this individual's data in aggregate queries.
     *                 This does not prevent the data to be queried by the individual's id.
     * @param file     the path to the file on the local filesystem where the data is stored.
     * @return a Future that completes when the individual is fully inserted into the variant store,
     * and is ready to be queried.
     */
    Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException;

    /**
     * Remove any information associated with the specified individual from the variant store
     *
     * @param id the individual's id
     * @return a Future that completes when the individual is fully removed from the variant store.
     */
    Future removeIndividual(String id) throws VariantStoreException;

    /**
     * Get the top n most harmful variants for a specified individual.
     *
     * @param id the individuals id
     * @param n  the number of variants to return
     * @return a List of harmful variants for the specified individual
     */
    List<GAVariant> getTopHarmfullVariants(String id, int n);
}
