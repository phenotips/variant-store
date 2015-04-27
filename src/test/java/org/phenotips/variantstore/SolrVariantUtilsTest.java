package org.phenotips.variantstore;

import org.phenotips.variantstore.db.solr.SolrSchema;
import org.phenotips.variantstore.db.solr.SolrVariantUtils;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.ga4gh.GAVariant;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test the SolrVariantUtils Class
 */
public class SolrVariantUtilsTest
{

    @Test
    public void testDocToVariant() throws Exception {
        String chrom = "chrX";
        long position = (long) 2000;
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

        SolrDocument doc = new SolrDocument();
        doc.setField(SolrSchema.CHROM, chrom);
        doc.setField(SolrSchema.POS, position);
        doc.setField(SolrSchema.REF, ref);
        doc.setField(SolrSchema.ALTS, alt);
        doc.setField(SolrSchema.QUAL, qual);
        doc.setField(SolrSchema.FILTER, filter);
        doc.setField(SolrSchema.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        doc.setField(SolrSchema.EXOMISER_GENE_PHENO_SCORE, exomiser_gene_pheno_score);
        doc.setField(SolrSchema.EXOMISER_GENE_VARIANT_SCORE, exomiser_gene_variant_score);
        doc.setField(SolrSchema.EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);
        doc.setField(SolrSchema.GENE, gene);
        doc.setField(SolrSchema.GENE_EFFECT, gene_effect);
        doc.setField(SolrSchema.EXAC_AF, exac_af);

        GAVariant variant = SolrVariantUtils.docToVariant(doc);

        assertEquals(variant.getReferenceName(), chrom);
        assertEquals((long) variant.getStart(), position);
        assertEquals(variant.getReferenceBases(), ref);
        assertThat(variant.getAlternateBases(), CoreMatchers.<Object>is(alt));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.QUALITY), qual);
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.FILTER), filter);
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_VARIANT_SCORE), String.valueOf(exomiser_variant_score));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE), String.valueOf(exomiser_gene_pheno_score));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE), String.valueOf(exomiser_gene_variant_score));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE), String.valueOf(exomiser_gene_combined_score));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXAC_AF), String.valueOf(exac_af));

    }

    @Test
    public void testVariantToDoc() throws Exception {
        String chrom = "chrX";
        long position = (long) 2000;
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
        variant.setStart(position);
        variant.setReferenceBases(ref);
        variant.setAlternateBases(alt);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.QUALITY, qual);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.FILTER, filter);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE, exomiser_gene_pheno_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE, exomiser_gene_variant_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.GENE, gene);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.GENE_EFFECT, gene_effect);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXAC_AF, exac_af);

        SolrDocument doc = SolrVariantUtils.variantToDoc(variant);

        assertEquals(doc.get(SolrSchema.CHROM), chrom);
        assertEquals(doc.get(SolrSchema.POS), position);
        assertEquals(doc.get(SolrSchema.REF), ref);
        assertThat(doc.get(SolrSchema.ALTS), CoreMatchers.<Object>is(alt));
        assertEquals(doc.get(SolrSchema.QUAL), qual);
        assertEquals(doc.get(SolrSchema.FILTER), filter);
        assertEquals(doc.get(SolrSchema.EXOMISER_VARIANT_SCORE), exomiser_variant_score);
        assertEquals(doc.get(SolrSchema.EXOMISER_GENE_PHENO_SCORE), exomiser_gene_pheno_score);
        assertEquals(doc.get(SolrSchema.EXOMISER_GENE_VARIANT_SCORE), exomiser_gene_variant_score);
        assertEquals(doc.get(SolrSchema.EXOMISER_GENE_COMBINED_SCORE), exomiser_gene_combined_score);
        assertEquals(doc.get(SolrSchema.GENE), gene);
        assertEquals(doc.get(SolrSchema.GENE_EFFECT), gene_effect);
        assertEquals(doc.get(SolrSchema.EXAC_AF), exac_af);
    }

    @Test
    public void testVariantToDocIdempotence() throws Exception {
        String chrom = "chrX";
        long position = (long) 2000;
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
        variant.setStart(position);
        variant.setReferenceBases(ref);
        variant.setAlternateBases(alt);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.QUALITY, qual);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.FILTER, filter);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE, exomiser_gene_pheno_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE, exomiser_gene_variant_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.GENE, gene);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.GENE_EFFECT, gene_effect);
        VariantUtils.addInfoToVariant(variant, GAVariantInfoFields.EXAC_AF, exac_af);

        SolrDocument doc = SolrVariantUtils.variantToDoc(variant);
        GAVariant variant2 = SolrVariantUtils.docToVariant(doc);

        assertEquals(variant.getReferenceName(), variant2.getReferenceName());
        assertEquals((long) variant.getStart(), (long) variant2.getStart());
        assertEquals(variant.getReferenceBases(), variant2.getReferenceBases());
        assertThat(variant.getAlternateBases(), CoreMatchers.<Object>is(variant2.getAlternateBases()));

        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.QUALITY),
                VariantUtils.getInfoFromVariant(variant2, GAVariantInfoFields.QUALITY));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.FILTER),
                VariantUtils.getInfoFromVariant(variant2, GAVariantInfoFields.FILTER));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_VARIANT_SCORE),
                VariantUtils.getInfoFromVariant(variant2, GAVariantInfoFields.EXOMISER_VARIANT_SCORE));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE),
                VariantUtils.getInfoFromVariant(variant2, GAVariantInfoFields.EXOMISER_GENE_PHENO_SCORE));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE),
                VariantUtils.getInfoFromVariant(variant2, GAVariantInfoFields.EXOMISER_GENE_VARIANT_SCORE));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE),
                VariantUtils.getInfoFromVariant(variant2, GAVariantInfoFields.EXOMISER_GENE_COMBINED_SCORE));
        assertEquals(VariantUtils.getInfoFromVariant(variant, GAVariantInfoFields.EXAC_AF),
                VariantUtils.getInfoFromVariant(variant2, GAVariantInfoFields.EXAC_AF));
    }
}
