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
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.phenotips.variantstore.VariantStoreException;

/**
 * Created by meatcar on 2/10/15.
 */
public class SolrController {
    private static Logger logger = Logger.getLogger(SolrController.class);
    private CoreContainer cores;

    private EmbeddedSolrServer server;

    final List<String> schema =  Arrays.asList(
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

    public SolrController(Path solrHome) {
        cores = new CoreContainer(solrHome.toString());
        cores.load();
        server = new EmbeddedSolrServer(cores, "variants");
    }

    public EmbeddedSolrServer getConnection() {
        return server;
    }

    public void stop() {
        cores.shutdown();
        server.shutdown();
    }

    public void add(Iterator<String> values) throws VariantStoreException, IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();

        for (String field: schema) {
            if (!values.hasNext()) {
                throw new VariantStoreException("Wrong number of fields provided!");
            }
            doc.addField(field, values.next());
        }

        server.add(doc);
    }
    public QueryResponse query(String desc, SolrQuery query) throws SolrServerException {
        return query(desc, query, false);
    }

    public QueryResponse query(String desc, SolrQuery query, boolean printResults) throws SolrServerException {
        long startTime = System.nanoTime();

        QueryResponse rsp = server.query(query);

        long endTime   = System.nanoTime();

        System.out.println("Query: " + desc + ";");

        if (printResults) {
            System.out.println(rsp);
        }

        System.out.println("Number of results found: " + rsp.getResults().getNumFound());
        System.out.println("Time taken: " +
                new DecimalFormat("#.##########").format((double)(endTime - startTime)/1000000000) +
                " s");
        return rsp;
    }
}
