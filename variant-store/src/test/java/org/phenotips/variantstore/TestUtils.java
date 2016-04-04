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

import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ga4gh.GACall;
import org.ga4gh.GAVariant;

import com.google.common.collect.Lists;

/**
 * Created by meatcar on 4/29/15.
 */
public class TestUtils
{
    private static final List<String> effects = Arrays.asList("MISSENSE", "FS_DELETION", "FS_INSERTION", "NON_FS_DELETION", "NON_FS_INSERTION", "STOPGAIN", "STOPLOSS",
            "FS_DUPLICATION", "SPLICING", "NON_FS_DUPLICATION", "FS_SUBSTITUTION", "NON_FS_SUBSTITUTION", "STARTLOSS",
            "ncRNA_EXONIC", "ncRNA_SPLICING", "UTR3", "UTR5", "SYNONYMOUS", "INTRONIC", "ncRNA_INTRONIC", "UPSTREAM",
            "DOWNSTREAM", "INTERGENIC");

    private static final List<String> chromosomes = Arrays.asList( "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22", "chrX", "chrY");

    public static GAVariant randomGAVariant() {
        String chrom = randomChromosome();
        long position = (long) Math.floor(Math.random());
        String ref = randomBases(10);
        List<String> alt = Arrays.asList(randomBases(10), randomBases(10));
        String qual = String.valueOf(Math.random());

        String filter;
        if (Math.random()*100 % 2 < 1) {
            filter = "PASS";
        } else {
            filter = "FAIL";
        }

        double exomiser_variant_score = Math.random();
        double exomiser_gene_pheno_score = Math.random();
        double exomiser_gene_variant_score = Math.random();
        double exomiser_gene_combined_score = Math.random();
        String gene = randomBases(5);
        String gene_effect = randomGeneEffect();
        double exac_af = Math.random();

        GAVariant variant = new GAVariant();
        variant.setReferenceName(chrom);
        variant.setStart(position);
        variant.setReferenceBases(ref);
        variant.setAlternateBases(alt);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE, gene);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE_EFFECT, gene_effect);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXAC_AF, exac_af);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE, exomiser_gene_pheno_score);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE, exomiser_gene_variant_score);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);

        GACall call = new GACall();

        call.setGenotype(Arrays.asList((int) (Math.random() * 100 % 2), (int) (Math.random() * 100 % 2)));
        VariantUtils.addInfo(call, GACallInfoFields.QUALITY, qual);
        VariantUtils.addInfo(call, GACallInfoFields.FILTER, filter);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        variant.setCalls(Collections.singletonList(call));

        return variant;
    }

    /**
     * Generate a string of random bases of length n.
     * @param n the length of the base string
     * @return the base string
     */
    public static String randomBases(int n) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            int pick = (int) Math.floor(Math.random());

            switch (pick % 4) {
                case 0:
                    sb.append("T");
                    break;
                case 1:
                    sb.append("C");
                    break;
                case 2:
                    sb.append("A");
                    break;
                case 3:
                    sb.append("G");
                    break;
            }
        }

        return sb.toString();
    }

    public static String randomGeneEffect() {
        return effects.get(((int) Math.floor(Math.random() * 100)) % effects.size());
    }

    public static String randomChromosome() {
        return chromosomes.get(((int) Math.floor(Math.random() * 100)) % effects.size());
    }
}
