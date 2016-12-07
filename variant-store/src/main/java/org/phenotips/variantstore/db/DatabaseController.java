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
package org.phenotips.variantstore.db;

import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.shared.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
    Future removeIndividual(VariantIterator id) throws DatabaseException;

    /**
     * Fetch all individuals that exhibit the given genes. Filter on variant effects, variant allele frequencies.
     * Return at most n variants per individual.
     *
     * @param gene              the gene to filter individuals on.
     * @param variantEffects    the effect that the variant has on the gene
     * @param alleleFrequencies the allele frequencies of the variant. Allowed keys are "EXAC"
     * @param n                 the number of variants to return per individual
     * @param totIndividuals    the total number of individuals in the db.
     * @return a map, where the keys are individual ids, and the values are a list of variants
     * for each individual that match the filter, sorted by variant harmfulness.
     */
    Map<String, List<GAVariant>> getIndividualsWithGene(String gene,
                                                        List<String> variantEffects,
                                                        Map<String, Double> alleleFrequencies,
                                                        int n, int totIndividuals);

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

    /**
     * GA4GH Beacon implementation. Return the allele count for this specific variant in the database.
     * @param chr chr
     * @param pos pos
     * @param allele allele
     * @return the allele count for this specific variant in the db.
     */
    int beacon(String chr, long pos, String allele);

    /**
     * Get the total number of variants in the database.
     * @return the total number of variants.
     */
    long getTotNumVariants();

    /**
     * Given an individual id, return all the genes stored for that individual.
     * @param id the individual's id
     * @return the set of genes.
     */
    Set<String> getAllGenesForIndividual(String id);

    /**
     * Get the exomiser harmfulness score for a gene for an individual.
     *
     * @param id   the individual's id
     * @param gene the gene
     *
     * @return the exomiser harmfulness score for the gene for the individual.
     */
    Double getGeneScore(String id, String gene);

    /**
     * Get a list of top k harmfull genes for an individual, sorted by harmfulness.
     *
     * @param id the individual's id
     * @param k  the max number of genes to return
     *
     * @return the list of genes
     */
    List<String> getTopGenesForIndividual(String id, Integer k);

     /**
     * Get a list of at most k variants with a known gene of an individual, sorted by
     * harmfulness.
     *
     * @param id   the individual
     * @param gene the gene
     * @param k    the max number of variants to return
     *
     * @return the list of variants
     */
    List<GAVariant> getTopHarmfullVariantsForGene(String id, String gene, Integer k);

    /**
     * Get a list of all the individual IDs stored in the variant store.
     *
     * @return a list of individual IDs
     */
    List<String> getAllIndividuals();

    /**
     * Get a list of all variants for the individual ID stored in the variant store.
     *
     * @param id   the individual
     *
     * @return the list of variants
     */
    List<GAVariant> getAllVariantsForIndividual(String id);
}
