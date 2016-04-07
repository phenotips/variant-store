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
import org.phenotips.variantstore.VariantStoreInterface;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.ga4gh.GAVariant;

/**
 * Proxy to {{@link org.phenotips.variantstore.VariantStore}}.
 *
 * @version $Id$
 */
public abstract class AbstractVariantStoreProxy implements VariantStoreService
{
    protected VariantStoreInterface variantStore;

    @Override
    public void init(Path path) throws VariantStoreException {
    }

    @Override
    public void stop() {
        this.variantStore.stop();
    }

    @Override
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException {
        return this.variantStore.addIndividual(id, isPublic, file);
    }

    @Override
    public Future removeIndividual(String id) throws VariantStoreException {
        return this.variantStore.removeIndividual(id);
    }

    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        return this.variantStore.getTopHarmfullVariants(id, n);
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol, List<String> variantEffects,
                                                               Map<String, Double> alleleFrequencies) {
        return this.variantStore.getIndividualsWithGene(geneSymbol, variantEffects, alleleFrequencies);
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt) {
        return null;
    }

    @Override
    public List<String> getIndividuals() {
        return this.variantStore.getIndividuals();
    }

    @Override
    public Set<String> getAllGenesForIndividual(String id) {
        return this.variantStore.getAllGenesForIndividual(id);
    }

    @Override
    public Double getGeneScore(String id, String gene) {
        return this.variantStore.getGeneScore(id, gene);
    }

    @Override
    public List<GAVariant> getTopHarmfullVariantsForGene(String id, String gene, Integer k) {
        return this.variantStore.getTopHarmfullVariantsForGene(id, gene, k);
    }

    @Override
    public List<String> getTopGenesForIndividual(String id, Integer k) {
        return this.variantStore.getTopGenesForIndividual(id, k);
    }

}
