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
package org.phenotips.variantStoreIntegration.mocks;

import org.phenotips.variantstore.VariantStoreInterface;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.ga4gh.GAVariant;

/**
 * A mock variant store.
 *
 * @version $Id$
 */
public class MockVariantStore implements VariantStoreInterface
{
    /**
     * Start up the variant store.
     */
    public void init()
    {
    }

    @Override
    public void init(Path path) throws VariantStoreException
    {

    }

    /**
     * Not used yet.
     */
    @Override
    public void stop()
    {
    };

    /**
     * @param id adsf
     * @param isPublic asdfas
     * @param file asdf
     * @return Future sdf
     */
    @Override
    public Future<Boolean> addIndividual(String id, boolean isPublic, Path file)
    {
        Callable<Boolean> task = new MockProcessingTask();
        return new FutureTask<Boolean>(task);
    }

    /**
     * @param id w/e
     * @return w/e
     */
    @Override
    public Future<Boolean> removeIndividual(String id)
    {
        Callable<Boolean> task = new MockProcessingTask();
        return new FutureTask<Boolean>(task);
    }

    @Override
    public List<GAVariant> getTopHarmfullVariants(String s, int i)
    {
        return null;
    }

    @Override
    public List<String> getAllIndividuals()
    {
        return null;
    }

    /**
     * Get a set of all known genes for an individual.
     *
     * @param id the individual's id
     *
     * @return the set of genes
     */
    @Override
    public Set<String> getAllGenesForIndividual(String id) {
        return null;
    }

    /**
     * Get the exomiser harmfulness score for a gene for an individual.
     *
     * @param id   the individual's id
     * @param gene the gene
     *
     * @return the exomiser harmfulness score for the gene for the individual.
     */
    @Override
    public Double getGeneScore(String id, String gene) {
        return null;
    }

    /**
     * Get a list of at most k variants with a known gene of an individual,
     * sorted by harmfulness.
     *
     * @param id   the individual
     * @param gene the gene
     * @param k    the max number of variants to return
     *
     * @return the list of variants
     */
    @Override
    public List<GAVariant> getTopHarmfullVariantsForGene(String id, String gene, Integer k) {
        return null;
    }

    /**
     * Get a list of top k harmfull genes for an individual, sorted by
     * harmfulness.
     *
     * @param id the individual's id
     * @param k  the max number of genes to return
     *
     * @return the list of genes
     */
    @Override
    public List<String> getTopGenesForIndividual(String id, Integer k) {
        return null;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol, List<String> variantEffects,
        Map<String, Double> alleleFrequencies)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
