package org.phenotips.variantstore.db.solr;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.CursorMarkParams;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.core.CoreContainer;
import org.ga4gh.GAVariant;

import org.phenotips.variantstore.db.solr.tasks.AddIndividualTask;
import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.db.AbstractDatabaseController;
import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.shared.ResourceManager;
import org.phenotips.variantstore.db.solr.tasks.RemoveIndividualTask;

/**
 * Manages an embedded instance of solr.
 */
public class SolrController extends AbstractDatabaseController {
    private Logger logger = Logger.getLogger(getClass());

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private CoreContainer cores;
    private SolrServer server;

    /**
     * Create a SolrController, that will store it's files and configuration in a directory inside of rootPath.
     */
    public SolrController() {
        super();
    }

    @Override
    protected Path getStoragePathSuffix() {
        return Paths.get("solr/");
    }

    @Override
    public void init(Path path) throws DatabaseException {
        super.init(path);

        ResourceManager.copyResourcesToPath(this.getStoragePathSuffix(), this.path);

        // Spin Solr up
        logger.debug(this.path);
        cores = new CoreContainer(this.path.toString());
        cores.load();
        server = new EmbeddedSolrServer(cores, "variants");
    }

    @Override
    public void stop() {
        executor.shutdownNow();
        server.shutdown();
        cores.shutdown();
    }

    @Override
    public Future addIndividual(final AbstractVariantIterator iterator) {
        FutureTask task = new FutureTask<Object>(new AddIndividualTask(server, iterator));

        executor.submit(task);

        return task;
    }

    @Override
    public Future removeIndividual(String id) throws DatabaseException {
        FutureTask task = new FutureTask<Object>(new RemoveIndividualTask(server, id));

        executor.submit(task);

        return task;
    }

    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n) {
        List<GAVariant> list = new ArrayList<GAVariant>();

        if (id == null || "".equals(id) || n == 0) {
            return list;
        }

        logger.debug(String.format("Searching for id:%s n:%s", id, n));

        String queryString = String.format("filter:PASS AND individual:%s", ClientUtils.escapeQueryChars(id));

        logger.debug("Query: " + queryString);

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(n)
                .setSort("exomiser_variant_score", SolrQuery.ORDER.desc);

        QueryResponse resp = null;

        try {
            resp = server.query(q);
            list = SolrVariantUtils.appendDocumentListToList(resp.getResults(), list);
        } catch (SolrServerException e) {
            logger.error("Error getting individuals ", e);
        }

        return list;
    }

    @Override
    public List<GAVariant> getTopHarmfulWithGene(String id, int n, String gene, List<String> variantEffects, Map<String, Double> alleleFrequencies) {
        List<GAVariant> list = new ArrayList<>();

        if (id == null || "".equals(id)
                || n == 0
                || gene == null || "".equals(gene)
                || variantEffects == null || variantEffects.size() == 0
                || alleleFrequencies == null || alleleFrequencies.size() == 0) {
            return list;
        }

        logger.debug(String.format("Searching for gene:%s effects:%s af:%s", gene, variantEffects, alleleFrequencies));

        /** Build Query String **/

        String effectQuery = "";
        for (String effect: variantEffects) {
            effectQuery += "gene_effect:" + ClientUtils.escapeQueryChars(effect) + " OR ";
        }
        // Strip final ' OR '
        effectQuery = effectQuery.substring(0, effectQuery.length() - 4);

        // Find ExAC AF under the specified frequency, or where ExAC is null.
        String exacQuery = String.format("(-exac_af:[* TO *] AND *:*) OR exac_af:[0 TO %s]",
                ClientUtils.escapeQueryChars(String.valueOf(alleleFrequencies.get("EXAC")))
        );

        String queryString = String.format("filter:PASS AND individual:%s AND gene:%s AND (%s) AND (%s)",
                ClientUtils.escapeQueryChars(id),
                ClientUtils.escapeQueryChars(gene),
                effectQuery,
                exacQuery
        );

        logger.debug("Query: " + queryString);

        SolrQuery q = new SolrQuery()
                .setRows(n)
                .setQuery(queryString)
                .addSort("exomiser_variant_score", SolrQuery.ORDER.desc);

        QueryResponse resp = null;

        try {
            resp = server.query(q);
            list = SolrVariantUtils.appendDocumentListToList(resp.getResults(), list);
        } catch (SolrServerException e) {
            logger.error("Error getting individuals with variant", e);
        }

        return list;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String gene,
                                                               List<String> variantEffects,
                                                               Map<String, Double> alleleFrequencies,
                                                               int n) {
        Map<String, List<GAVariant>> map = new HashMap<>();

        if (gene == null || "".equals(gene)
                || variantEffects == null || variantEffects.size() == 0
                || alleleFrequencies == null || alleleFrequencies.size() == 0) {
            return map;
        }

        logger.debug(String.format("Searching for gene:%s effects:%s af:%s", gene, variantEffects, alleleFrequencies));

        /** Build Query String **/

        String effectQuery = "";
        for (String effect: variantEffects) {
            effectQuery += "gene_effect:" + ClientUtils.escapeQueryChars(effect) + " OR ";
        }
        // Strip final ' OR '
        effectQuery = effectQuery.substring(0, effectQuery.length() - 4);

        // Find ExAC AF under the specified frequency, or where ExAC is null.
        String exacQuery = String.format("(-exac_af:[* TO *] AND *:*) OR exac_af:[0 TO %s]",
                ClientUtils.escapeQueryChars(String.valueOf(alleleFrequencies.get("EXAC")))
        );

        String queryString = String.format("filter:PASS AND gene:%s AND (%s) AND (%s)",
                ClientUtils.escapeQueryChars(gene),
                effectQuery,
                exacQuery
        );

        logger.debug("Query: " + queryString);

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .addSort("exomiser_variant_score", SolrQuery.ORDER.desc);

        q.setRows(10);
        q.set(GroupParams.GROUP, true);
        q.set(GroupParams.GROUP_FIELD, "individual");
        q.set(GroupParams.GROUP_LIMIT, n);

        QueryResponse resp = null;

        try {
            resp = server.query(q);
            map = SolrVariantUtils.appendGroupResponseToMap(resp.getGroupResponse(), map);
        } catch (SolrServerException e) {
            logger.error("Error getting individuals with variant", e);
        }

        return map;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt) {
        Map<String, List<GAVariant>> map = new HashMap<>();

        if (chr == null || pos == 0 || ref == null || alt == null) {
            return map;
        }


        logger.debug(String.format("Searching for %s:%s %s->%s", chr, pos, ref, alt));
        String queryString = String.format("chrom:%s AND ref:%s AND pos:%s AND alts:%s",
                ClientUtils.escapeQueryChars(chr),
                ClientUtils.escapeQueryChars(ref),
                ClientUtils.escapeQueryChars(String.valueOf(pos)),
                ClientUtils.escapeQueryChars(alt)
        );

        logger.debug("Query: " + queryString);

        SolrQuery q = new SolrQuery()
                .setTimeAllowed(0) // for cursor
                .setQuery(queryString)
                .addSort("id", SolrQuery.ORDER.desc);


        QueryResponse resp = null;

        q.setRows(10);
        q.set(GroupParams.GROUP, true);
        q.set(GroupParams.GROUP_FIELD, "individual");
        q.set(GroupParams.GROUP_LIMIT, 1);

        String cursor = CursorMarkParams.CURSOR_MARK_START;
        String oldCursor = null;

        while (!cursor.equals(oldCursor)) {
            try {
                q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursor);

                resp = server.query(q);
                map = SolrVariantUtils.appendGroupResponseToMap(resp.getGroupResponse(), map);

                oldCursor = cursor;
                cursor = resp.getNextCursorMark();
            } catch (SolrServerException e) {
                logger.error("Error getting individuals with variant", e);
                break;
            }
        }

        return map;
    }



}
