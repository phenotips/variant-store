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
package org.phenotips.variantstore.db.solr;

import org.phenotips.variantstore.TestUtils;
import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import static org.phenotips.variantstore.db.solr.SolrVariantUtils.addVariantToDoc;
import static org.phenotips.variantstore.db.solr.SolrVariantUtils.getCallsetField;
import static org.phenotips.variantstore.db.solr.SolrVariantUtils.setCallsetField;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.solr.common.SolrDocument;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test the SolrVariantUtils Class
 */
public class SolrVariantUtilsTest
{

    @Test
    public void testDocToVariant() throws Exception {
        String callsetId = "callset1";
        String chrom = "chrX";
        long start = (long) 2000;
        String ref = "CTAG";
        String alt = "A";
        String qual = "10";
        String filter = "PASS";
        double exomiser_variant_score = 0.1;
        double exomiser_gene_pheno_score = 0.2;
        double exomiser_gene_variant_score = 0.3;
        double exomiser_gene_combined_score = 0.4;
        String gene = "CNST";
        String gene_effect = "MISSENSE";
        double exac_af = 0.5;

        SolrDocument doc = new SolrDocument();
        doc.setField(VariantsSchema.CHROM, chrom);
        doc.setField(VariantsSchema.REF, ref);
        doc.setField(VariantsSchema.START, start);
        doc.setField(VariantsSchema.END, start + 1);
        doc.setField(VariantsSchema.ALT, alt);
        doc.setField(VariantsSchema.GENE, gene);
        doc.setField(VariantsSchema.GENE_EFFECT, gene_effect);
        doc.setField(VariantsSchema.EXAC_AF, exac_af);

        setCallsetField(doc, callsetId, VariantsSchema.QUAL, qual);
        setCallsetField(doc, callsetId, VariantsSchema.FILTER, filter);
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_COMBINED_SCORE,
                exomiser_gene_combined_score);
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_PHENO_SCORE,
                exomiser_gene_pheno_score);
        setCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_VARIANT_SCORE,
                exomiser_gene_variant_score);
        setCallsetField(doc, callsetId, VariantsSchema.AC, 2);

        GAVariant variant = SolrVariantUtils.docToVariant(doc, callsetId);

        assertEquals(variant.getReferenceName(), chrom);
        assertEquals((long) variant.getStart(), start);
        assertEquals(variant.getReferenceBases(), ref);
        assertThat(variant.getAlternateBases().get(0), CoreMatchers.<Object>is(alt));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE), String.valueOf(gene));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE_EFFECT), String.valueOf(gene_effect));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.EXAC_AF), String.valueOf(exac_af));

        assertThat(variant.getCalls(), is(notNullValue()));
        GACall call = variant.getCalls().get(0);
        assertThat(call, is(notNullValue()));
        assertThat(call.getGenotype().get(0), is(1));
        assertThat(call.getGenotype().get(1), is(1));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.QUALITY), qual);
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.FILTER), filter);
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE), String.valueOf(exomiser_variant_score));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE), String.valueOf(exomiser_gene_pheno_score));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE), String.valueOf(exomiser_gene_variant_score));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE), String.valueOf(exomiser_gene_combined_score));

    }

    @Test
    public void testVariantToDoc() throws Exception {
        String callsetId = "callset1";
        String chrom = "chrX";
        long start = (long) 2000;
        String ref = "CTAG";
        List<String> alt = Arrays.asList("A", "T");
        String qual = "10";
        String filter = "PASS";
        double exomiser_variant_score = 0.1;
        double exomiser_gene_pheno_score = 0.2;
        double exomiser_gene_variant_score = 0.3;
        double exomiser_gene_combined_score = 0.4;
        String gene = "CNST";
        String gene_effect = "MISSENSE";
        double exac_af = 0.5;

        GAVariant variant = new GAVariant();
        variant.setReferenceName(chrom);
        variant.setStart(start);
        variant.setReferenceBases(ref);
        variant.setAlternateBases(alt);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE, gene);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE_EFFECT, gene_effect);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXAC_AF, exac_af);
        GACall call = new GACall();
        call.setGenotype(Arrays.asList(0, 1));
        VariantUtils.addInfo(call, GACallInfoFields.QUALITY, qual);
        VariantUtils.addInfo(call, GACallInfoFields.FILTER, filter);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE,
                exomiser_gene_pheno_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE,
                exomiser_gene_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields
                .EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);
        variant.setCalls(Collections.singletonList(call));

        SolrDocument doc = SolrVariantUtils.variantToDoc(variant);
        addVariantToDoc(doc, variant, callsetId, true);

        assertEquals(doc.get(VariantsSchema.CHROM), chrom);
        assertEquals(doc.get(VariantsSchema.START), start);
        assertEquals(doc.get(VariantsSchema.END), start + ref.length());
        assertEquals(doc.get(VariantsSchema.REF), ref);
        assertEquals(doc.get(VariantsSchema.ALT), alt.get(0));
        assertEquals(doc.get(VariantsSchema.GENE), gene);
        assertEquals(doc.get(VariantsSchema.GENE_EFFECT), gene_effect);
        assertEquals(doc.get(VariantsSchema.EXAC_AF), exac_af);

        assertEquals(getCallsetField(doc, callsetId, VariantsSchema.AC), 1);
        assertEquals(getCallsetField(doc, callsetId, VariantsSchema.QUAL), qual);
        assertEquals(getCallsetField(doc, callsetId, VariantsSchema.FILTER), filter);
        assertEquals(getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_VARIANT_SCORE), exomiser_variant_score);
        assertEquals(getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_PHENO_SCORE), exomiser_gene_pheno_score);
        assertEquals(getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_VARIANT_SCORE), exomiser_gene_variant_score);
        assertEquals(getCallsetField(doc, callsetId, VariantsSchema.EXOMISER_GENE_COMBINED_SCORE), exomiser_gene_combined_score);
    }

    @Test
    public void testVariantToDocIdempotence() throws Exception {
        String callsetId = "callset1";
        String chrom = "chrX";
        long position = (long) 2000;
        String ref = "CTAG";
        List<String> alts = Collections.singletonList(TestUtils.randomBases(10));
        String qual = "10";
        String filter = "PASS";
        double exomiser_variant_score = 0.1;
        double exomiser_gene_pheno_score = 0.2;
        double exomiser_gene_variant_score = 0.3;
        double exomiser_gene_combined_score = 0.4;
        String gene = "CNST";
        String gene_effect = "MISSENSE";
        double exac_af = 0.5;

        GAVariant variant = new GAVariant();
        variant.setReferenceName(chrom);
        variant.setStart(position);
        variant.setReferenceBases(ref);
        variant.setAlternateBases(alts);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE, gene);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE_EFFECT, gene_effect);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXAC_AF, exac_af);

        GACall call = new GACall();
        call.setGenotype(Arrays.asList(0, 1));
        VariantUtils.addInfo(call, GACallInfoFields.QUALITY, qual);
        VariantUtils.addInfo(call, GACallInfoFields.FILTER, filter);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE, exomiser_gene_pheno_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE, exomiser_gene_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);
        variant.setCalls(Collections.singletonList(call));

        SolrDocument doc = SolrVariantUtils.variantToDoc(variant);
        addVariantToDoc(doc, variant, callsetId, true);
        GAVariant variant2 = SolrVariantUtils.docToVariant(doc, callsetId);

        assertEquals(variant.getReferenceName(), variant2.getReferenceName());
        assertEquals((long) variant.getStart(), (long) variant2.getStart());
        assertEquals(variant.getReferenceBases(), variant2.getReferenceBases());
        assertEquals(variant.getAlternateBases(), variant2.getAlternateBases());

        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.EXAC_AF),
                VariantUtils.getInfo(variant2, GAVariantInfoFields.EXAC_AF));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE),
                VariantUtils.getInfo(variant2, GAVariantInfoFields.GENE));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE_EFFECT),
                VariantUtils.getInfo(variant2, GAVariantInfoFields.GENE_EFFECT));

        GACall call2 = variant2.getCalls().get(0);
        assertThat(call2.getGenotype().get(0), is(0));
        assertThat(call2.getGenotype().get(1), is(1));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.QUALITY),
                VariantUtils.getInfo(call2, GACallInfoFields.QUALITY));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.FILTER),
                VariantUtils.getInfo(call2, GACallInfoFields.FILTER));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE),
                VariantUtils.getInfo(call2, GACallInfoFields.EXOMISER_VARIANT_SCORE));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE),
                VariantUtils.getInfo(call2, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE),
                VariantUtils.getInfo(call2, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE),
                VariantUtils.getInfo(call2, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE));
    }
}
