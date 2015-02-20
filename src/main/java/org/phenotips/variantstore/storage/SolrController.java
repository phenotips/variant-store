package org.phenotips.variantstore.storage;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.phenotips.variantstore.VariantStoreException;

/**
 * Created by meatcar on 2/10/15.
 */
public class SolrController {
    private static Logger logger = Logger.getLogger(SolrController.class);
    private CoreContainer cores;
    private SolrInputDocument doc;

    private EmbeddedSolrServer server;

    final List<String> csvSchema =  Arrays.asList(
            "chrom",
            "pos",
            "var_id",
            "ref",
            "alt",
            "qual",
            "filter",
            "ac",
            "an",
            "cga_bf",
            "cga_bndg",
            "cga_bndgo",
            "cga_fi",
            "cga_medel",
            "cga_mirb",
            "cga_pfam",
            "cga_rpt",
            "cga_sdo",
            "cga_winend",
            "cga_xr",
            "cipos",
            "endd",
            "mateid",
            "meinfo",
            "ns",
            "svlen",
            "svtype",
            "imprecise",
            "exomiser_variant_score",
            "exomiser_gene_pheno_score",
            "exomiser_gene",
            "exomiser_effect",
            "exomiser_gene_combined_score",
            "exomiser_gene_variant_score"
    );

    final List<String> solrSchema = Arrays.asList(
            "chrom",
            "pos",
            "ref",
            "alt",
            "qual",
            "filter",
            "exomiser_variant_score",
            "exomiser_gene_pheno_score",
            "exomiser_gene",
            "exomiser_effect",
            "exomiser_gene_combined_score",
            "exomiser_gene_variant_score"
    );

    public SolrController(Path solrHome) {
        cores = new CoreContainer(solrHome.toString());
        cores.load();
        server = new EmbeddedSolrServer(cores, "variants");
        doc = new SolrInputDocument();
    }

    public EmbeddedSolrServer getConnection() {
        return server;
    }

    public void stop() {
        cores.shutdown();
        server.shutdown();
    }

    public void add(Iterator<String> values, String patientName) throws VariantStoreException, IOException, SolrServerException {

        if (!doc.getField("patient").equals(patientName)) {
            doc.addField("patient", patientName);
        }

        for (String field: csvSchema) {
            String value = values.next();

            for (String solrField : solrSchema) {
                if (solrField.equals(field)) {
                    doc.addField(field, value);
                    break;
                }
            }
        }

        server.add(doc);
    }

    public QueryResponse query(String desc, SolrQuery query) throws SolrServerException {
        return query(desc, query, false);
    }

    public QueryResponse query(String desc, SolrQuery query, boolean printResults) throws SolrServerException {
        SolrDocumentList results;
        long startTime = System.nanoTime();
        QueryResponse rsp = server.query(query);
        long endTime   = System.nanoTime();

        results = rsp.getResults();

        logger.debug(String.format("Query: %s; %d of %d", desc, query.getStart(), results.getNumFound()));

        if (printResults) {
            for (SolrDocument doc : results) {
                logger.debug(doc);
            }
        }

        logger.debug("Time taken: " +
                new DecimalFormat("#.##########").format((double) (endTime - startTime) / 1000000000) +
                " s");


        return rsp;
    }
}
