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

import org.phenotips.data.Patient;
import org.phenotips.data.similarity.Exome;
import org.phenotips.data.similarity.Variant;
import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.variantStoreIntegration.VariantStoreVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ga4gh.GAVariant;
import org.json.JSONArray;

/**
 * An exome implementation that's backed by {{@link VariantStore}}.
 *
 * @version $Id$
 */
public class VariantstoreExome implements Exome
{
    private final Patient patient;

    private VariantStoreService vs;

    /**
     * Initialize the exome with the variant store and the patient.
     *
     * @param vs the Variant Store instance
     * @param p the patient
     */
    public VariantstoreExome(VariantStoreService vs, Patient p)
    {
        this.vs = vs;
        this.patient = p;
    }

    /**
     * Get the names of all genes with variants in the patient.
     *
     * @return an unmodifiable set of gene names with variants in the patient
     */
    @Override
    public Set<String> getGenes()
    {
        return this.vs.getAllGenesForIndividual(this.patient.getId());
    }

    /**
     * Return the score for a gene.
     *
     * @param gene the gene in question
     *
     * @return the score of the gene, between 0 and 1, where 1 is better (or
     * {@code null} if no variants for gene)
     */
    @Override
    public Double getGeneScore(String gene)
    {
        return this.vs.getGeneScore(this.patient.getId(), gene);
    }

    /**
     * Get {@link Variant}s for a gene.
     *
     * @param gene the gene to get {@link Variant}s for.
     * @param k the max number of variants to return
     *
     * @return an unmodifiable (potentially-empty) list of top {@link Variant}s
     *     for the gene, by decreasing score
     */
    @Override
    public List<Variant> getTopVariants(String gene, int k)
    {
        List<GAVariant> gaVariants = this.vs.getTopHarmfullVariantsForGene(this.patient.getId(), gene, k);
        List<Variant> variants = new ArrayList<>();
        for (GAVariant gaVariant : gaVariants) {
            variants.add(new VariantStoreVariant(gaVariant, this.vs.getAllIndividuals().size()));
        }
        return variants;
    }

    /**
     * Get the n highest genes, in descending order of score. If there are fewer
     * than n genes, all will be returned.
     *
     * @param n the number of genes to return (specify 0 for all)
     *
     * @return an unmodifiable (potentially-empty) list of gene names
     */
    @Override
    public List<String> getTopGenes(int n)
    {
        return this.vs.getTopGenesForIndividual(this.patient.getId(), n);
    }

    /**
     * Retrieve all variant information in a JSON format. For example:
     * <p/>
     * <pre>
     *   [
     *     {
     *       "gene": "SRCAP",
     *       "score": 0.7, // phenotype score
     *       "variants": [ // variants sorted by decreasing score
     *         {
     *           "score": 0.8, // genotype score
     *           "chrom": "1",
     *           "position": 2014819,
     *           "type": "SPLICING",
     *           "ref": "A",
     *           "alt": "T",
     *         },
     *         {...},
     *        ]
     *     }
     *   ]
     * </pre>
     *
     * @return the data about this value, using the org.json classes
     */
    @Override
    public JSONArray toJSON()
    {
        return null;
    }
}
