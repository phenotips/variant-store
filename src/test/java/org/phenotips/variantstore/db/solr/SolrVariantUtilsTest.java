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
package org.phenotips.variantstore.db.solr;

import org.phenotips.variantstore.TestUtils;
import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.GAVariantInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
        doc.setField(SolrSchema.ALT, alt);
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
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE), String.valueOf(gene));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE_EFFECT), String.valueOf(gene_effect));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.EXAC_AF), String.valueOf(exac_af));

        assertThat(variant.getCalls(), is(notNullValue()));
        GACall call = variant.getCalls().get(0);
        assertThat(call, is(notNullValue()));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.QUALITY), qual);
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.FILTER), filter);
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE), String.valueOf(exomiser_variant_score));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE), String.valueOf(exomiser_gene_pheno_score));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE), String.valueOf(exomiser_gene_variant_score));
        assertEquals(VariantUtils.getInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE), String.valueOf(exomiser_gene_combined_score));

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
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE, gene);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE_EFFECT, gene_effect);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXAC_AF, exac_af);
        GACall call = new GACall();
        VariantUtils.addInfo(call, GACallInfoFields.QUALITY, qual);
        VariantUtils.addInfo(call, GACallInfoFields.FILTER, filter);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE, exomiser_gene_pheno_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE, exomiser_gene_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);
        variant.setCalls(Collections.singletonList(call));

        SolrDocument doc = SolrVariantUtils.variantToDoc(variant);

        assertEquals(doc.get(SolrSchema.CHROM), chrom);
        assertEquals(doc.get(SolrSchema.POS), position);
        assertEquals(doc.get(SolrSchema.REF), ref);
        assertThat(doc.get(SolrSchema.ALT), is(nullValue()));
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
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE, gene);
        VariantUtils.addInfo(variant, GAVariantInfoFields.GENE_EFFECT, gene_effect);
        VariantUtils.addInfo(variant, GAVariantInfoFields.EXAC_AF, exac_af);

        GACall call = new GACall();
        VariantUtils.addInfo(call, GACallInfoFields.QUALITY, qual);
        VariantUtils.addInfo(call, GACallInfoFields.FILTER, filter);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_VARIANT_SCORE, exomiser_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_PHENO_SCORE, exomiser_gene_pheno_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_VARIANT_SCORE, exomiser_gene_variant_score);
        VariantUtils.addInfo(call, GACallInfoFields.EXOMISER_GENE_COMBINED_SCORE, exomiser_gene_combined_score);
        variant.setCalls(Collections.singletonList(call));

        SolrDocument doc = SolrVariantUtils.variantToDoc(variant);
        GAVariant variant2 = SolrVariantUtils.docToVariant(doc);

        assertEquals(variant.getReferenceName(), variant2.getReferenceName());
        assertEquals((long) variant.getStart(), (long) variant2.getStart());
        assertEquals(variant.getReferenceBases(), variant2.getReferenceBases());

        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.EXAC_AF),
                VariantUtils.getInfo(variant2, GAVariantInfoFields.EXAC_AF));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE),
                VariantUtils.getInfo(variant2, GAVariantInfoFields.GENE));
        assertEquals(VariantUtils.getInfo(variant, GAVariantInfoFields.GENE_EFFECT),
                VariantUtils.getInfo(variant2, GAVariantInfoFields.GENE_EFFECT));

        GACall call2 = variant2.getCalls().get(0);
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

    @Test
    public void testGroupResponseToMap() throws Exception {
        GAVariant var1 = TestUtils.randomGAVariant();
        GAVariant var2 = TestUtils.randomGAVariant();
        GAVariant var3 = TestUtils.randomGAVariant();

        String p1 = "patient1";
        SolrDocument doc1 = SolrVariantUtils.variantToDoc(var1);
        SolrDocument doc2 = SolrVariantUtils.variantToDoc(var2);
        SolrDocumentList list1 = new SolrDocumentList();
        list1.add(doc1);
        list1.add(doc2);

        String p2 = "patient2";
        SolrDocument doc3 = SolrVariantUtils.variantToDoc(var3);
        SolrDocumentList list2 = new SolrDocumentList();
        list2.add(doc3);

        GroupCommand command = new GroupCommand("testCommand", 2);
        command.add(new Group(p1, list1));
        command.add(new Group(p2, list2));

        GroupResponse response = new GroupResponse();
        response.add(command);

        Map<String, List<GAVariant>> map = SolrVariantUtils.groupResponseToMap(response);
        assertThat(map.size(), is(2));
        assertThat(map.get(p1).size(), is(2));
        assertThat(map.get(p2).size(), is(1));

        assertThat(map.get(p1).get(0).getReferenceBases(), is(var1.getReferenceBases()));
        assertThat(map.get(p1).get(1).getReferenceBases(), is(var2.getReferenceBases()));
        assertThat(map.get(p2).get(0).getReferenceBases(), is(var3.getReferenceBases()));
    }

    @Test
    public void testDocumentListToList() throws Exception {
        GAVariant var1 = TestUtils.randomGAVariant();
        GAVariant var2 = TestUtils.randomGAVariant();

        SolrDocumentList doclist = new SolrDocumentList();
        SolrDocument doc1 = SolrVariantUtils.variantToDoc(var1);
        SolrDocument doc2 = SolrVariantUtils.variantToDoc(var2);

        doclist.add(doc1);
        doclist.add(doc2);

        List<GAVariant> list = SolrVariantUtils.documentListToList(doclist);

        assertThat(list.size(), is(2));
        assertThat(list.get(0), is(notNullValue()));
        assertThat(list.get(1), is(notNullValue()));
        /** TODO: stop relying on Math.random to generate pseudo-unique keys **/
        assertThat(list.get(0).getReferenceBases(), is(var1.getReferenceBases()));
        assertThat(list.get(1).getReferenceBases(), is(var2.getReferenceBases()));
    }

    @Test
    public void testVariantToDocs() throws Exception {
        GAVariant variant = new GAVariant();
        GACall call = new GACall();

        /** Test multiple alts */
        variant.setAlternateBases(Arrays.asList("A", "T"));
        call.setGenotype(Arrays.asList(1, 2));
        variant.setCalls(Collections.singletonList(call));

        List<SolrDocument> solrDocuments = SolrVariantUtils.variantToDocs(variant);

        assertThat(solrDocuments.size(), is(2));
        assertThat((String) solrDocuments.get(0).getFieldValue(SolrSchema.ALT), is(equalTo("A")));
        assertThat((int) solrDocuments.get(0).getFieldValue(SolrSchema.COPIES), is(equalTo(1)));
        assertThat((String) solrDocuments.get(1).getFieldValue(SolrSchema.ALT), is(equalTo("T")));
        assertThat((int) solrDocuments.get(1).getFieldValue(SolrSchema.COPIES), is(equalTo(1)));

        /** Test single alt */
        variant.setAlternateBases(Arrays.asList("A"));
        call.setGenotype(Arrays.asList(1, 1));
        variant.setCalls(Collections.singletonList(call));

        solrDocuments = SolrVariantUtils.variantToDocs(variant);

        assertThat(solrDocuments.size(), is(1));
        assertThat((String) solrDocuments.get(0).getFieldValue(SolrSchema.ALT), is(equalTo("A")));
        assertThat((int) solrDocuments.get(0).getFieldValue(SolrSchema.COPIES), is(equalTo(2)));
    }
}
