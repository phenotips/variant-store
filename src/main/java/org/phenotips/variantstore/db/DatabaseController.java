package org.phenotips.variantstore.db;

import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.shared.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.ga4gh.GAVariant;

/**
 * @version $Id$
 */
public interface DatabaseController extends Service
{
    /**
     * Add an individual to the database.
     *
     * @param iterator the individual's VariantIterator
     * @return a Future that completes when the individual's variants are ready to be queried
     * @throws DatabaseException if an error is encountered immediately.
     *                           The Future throws exceptions if an error is encountered during insert.
     */
    Future addIndividual(VariantIterator iterator) throws DatabaseException;

    /**
     * Remove an individual from the db.
     *
     * @param id the id of the individual to remove.
     * @return a Future that completes when the individual's variants are no longer in the db.
     * @throws DatabaseException if an error is encountered immediately.
     *                           The Future throws exceptions if an error is encountred during insert.
     */
    Future removeIndividual(String id) throws DatabaseException;

    /**
     * Fetch all individuals that exhibit the given genes. Filter on variant effects, variant allele frequencies.
     * Return at most n variants per individual.
     *
     * @param gene              the gene to filter individuals on.
     * @param variantEffects    the effect that the variant has on the gene
     * @param alleleFrequencies the allele frequencies of the variant. Allowed keys are "EXAC"
     * @param n                 the number of variants to return per individual
     * @return a map, where the keys are individual ids, and the values are a list of variants
     * for each individual that match the filter, sorted by variant harmfulness.
     */
    Map<String, List<GAVariant>> getIndividualsWithGene(String gene,
                                                        List<String> variantEffects,
                                                        Map<String, Double> alleleFrequencies,
                                                        int n);

    /**
     * Fetch all individuals that have variants that match the pattern.
     *
     * @param chr the chromosome
     * @param pos the position
     * @param ref the reference bases
     * @param alt the alternate bases
     * @return a map, where the keys are individual ids, and the values are a list of variants
     * for each individual that match the filter.
     */
    Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt);

    /**
     * Fetch a list of top harmful variants for an individual, filtered by gene.
     *
     * @param id                the individual id
     * @param n                 the number of variants to return
     * @param gene              the gene associated with the variant
     * @param variantEffects    the effect that the variant has on the gene
     * @param alleleFrequencies the allele frequencies of the variant.
     * @return the list of variants that pass the filter, sorted by harmfulness
     */
    List<GAVariant> getTopHarmfulWithGene(String id,
                                          int n,
                                          String gene,
                                          List<String> variantEffects,
                                          Map<String, Double> alleleFrequencies);

    /**
     * Fetch a list of top harmful variants for an individual.
     * @param id the individual's id
     * @param n the number of variants to fetch
     * @return a list of n variants, sorted by harmfulness
     */
    List<GAVariant> getTopHarmfullVariants(String id, int n);
}
