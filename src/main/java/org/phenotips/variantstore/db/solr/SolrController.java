package org.phenotips.variantstore.db.solr;

import org.phenotips.variantstore.db.AbstractDatabaseController;
import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.db.solr.tasks.AddIndividualTask;
import org.phenotips.variantstore.db.solr.tasks.RemoveIndividualTask;
import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.shared.ResourceManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages an embedded instance of solr.
 *
 * @version $Id$
 */
public class SolrController extends AbstractDatabaseController
{
    /** the field name of the EXAC allele frequency value in the allele frequency query map. **/
    public static final String EXAC_FREQUENCY_FIELD = "EXAC";

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
    public Future addIndividual(final VariantIterator iterator) {
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

        String queryString = String.format("%s:PASS AND %s:%s",
                SolrSchema.FILTER,
                SolrSchema.INDIVIDUAL, ClientUtils.escapeQueryChars(id));

        logger.debug("Query string: " + queryString);

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(n)
                .setSort(SolrSchema.EXOMISER_VARIANT_SCORE, SolrQuery.ORDER.desc);

        QueryResponse resp = null;

        try {
            resp = server.query(q);
            list = SolrVariantUtils.documentListToList(resp.getResults());
        } catch (SolrServerException e) {
            logger.error("Error getting individuals ", e);
        }

        return list;
    }

    @Override
    public List<GAVariant> getTopHarmfulWithGene(String id,
                                                 int n,
                                                 String gene,
                                                 List<String> variantEffects,
                                                 Map<String, Double> alleleFrequencies) {

        List<GAVariant> list = new ArrayList<>();

        checkNotNull(id);
        checkArgument("".equals(id));

        checkArgument(n == 0);

        checkNotNull(gene);
        checkArgument("".equals(gene));

        checkNotNull(variantEffects);
        checkArgument(variantEffects.size() == 0);

        checkNotNull(alleleFrequencies);
        checkArgument(alleleFrequencies.size() == 0);

        logger.debug(String.format("Searchig for gene:%s effects:%s af:%s", gene, variantEffects, alleleFrequencies));

        /** Build Query String **/

        String effectQuery = "";
        for (String effect : variantEffects) {
            effectQuery += String.format("%s:%s OR ", SolrSchema.GENE_EFFECT, ClientUtils.escapeQueryChars(effect));
        }
        // Strip final ' OR '
        effectQuery = effectQuery.substring(0, effectQuery.length() - 4);

        // Find ExAC AF under the specified frequency, or where ExAC is null.
        String exacQuery = String.format("(-%s:[* TO *] AND *:*) OR %s:[0 TO %s] ",
                SolrSchema.EXAC_AF, SolrSchema.EXAC_AF,
                ClientUtils.escapeQueryChars(String.valueOf(alleleFrequencies.get(EXAC_FREQUENCY_FIELD)))
        );

        String queryString = String.format("%s:PASS AND %s:%s AND %s:%s AND (%s) AND (%s)",
                SolrSchema.FILTER,
                SolrSchema.INDIVIDUAL, ClientUtils.escapeQueryChars(id),
                SolrSchema.GENE, ClientUtils.escapeQueryChars(gene),
                effectQuery,
                exacQuery
        );

        logger.debug("Query : " + queryString);

        SolrQuery q = new SolrQuery()
                .setRows(n)
                .setQuery(queryString)
                .addSort(SolrSchema.EXOMISER_VARIANT_SCORE, SolrQuery.ORDER.desc);

        QueryResponse resp = null;

        try {
            resp = server.query(q);
            list = SolrVariantUtils.documentListToList(resp.getResults());
        } catch (SolrServerException e) {
            logger.error("Error getting individuals with variants", e);
        }

        return list;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String gene,
                                                               List<String> variantEffects,
                                                               Map<String, Double> alleleFrequencies,
                                                               int n) {
        Map<String, List<GAVariant>> map = new HashMap<>();

        checkArgument(n == 0, "n cannot be zero");

        checkNotNull(gene, "gene cannot be null");
        checkArgument("".equals(gene), "gene cannot be empty");

        checkNotNull(variantEffects, "effects cannot be null");
        checkArgument(variantEffects.size() == 0, "effects cannot be empty");

        checkNotNull(alleleFrequencies, "allele frequencies cannot be null");
        checkArgument(alleleFrequencies.size() == 0, "allele frequencies cannot be empty");

        logger.debug(String.format("Searching for gene:%s effects:%s af:%s", gene, variantEffects, alleleFrequencies));

        /** Build Query String **/

        StringBuilder builder = new StringBuilder();
        for (String effect : variantEffects) {
            builder.append(SolrSchema.GENE_EFFECT)
                   .append(":")
                   .append(ClientUtils.escapeQueryChars(effect))
                   .append(" OR ");
        }
        // Strip final ' OR '
        String effectQuery = builder.toString();
        effectQuery = effectQuery.substring(0, effectQuery.length() - 4);

        // Find ExAC AF under the specified frequency, or where ExAC is null.
        String exacQuery = String.format("(-%s:[* TO *] AND *:*) OR %s:[0 TO %s]",
                SolrSchema.EXAC_AF, SolrSchema.EXAC_AF,
                ClientUtils.escapeQueryChars(String.valueOf(alleleFrequencies.get(EXAC_FREQUENCY_FIELD)))
        );

        String queryString = String.format("%s:PASS AND %s:%s AND (%s) AND (%s)",
                SolrSchema.FILTER,
                SolrSchema.GENE,
                ClientUtils.escapeQueryChars(gene),
                effectQuery,
                exacQuery
        );

        logger.debug("Qeury: " + queryString);

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .addSort(SolrSchema.EXOMISER_VARIANT_SCORE, SolrQuery.ORDER.desc);

        q.setRows(300);
        q.set(GroupParams.GROUP, true);
        q.set(GroupParams.GROUP_FIELD, SolrSchema.INDIVIDUAL);
        q.set(GroupParams.GROUP_LIMIT, n);

        QueryResponse resp = null;

        try {
            resp = server.query(q);
            map = SolrVariantUtils.groupResponseToMap(resp.getGroupResponse());
        } catch (SolrServerException e) {
            logger.error("Error getting individals with variant", e);
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
        String queryString = String.format("%s:%s AND %s:%s AND %s:%s AND %s:%s",
                SolrSchema.CHROM, ClientUtils.escapeQueryChars(chr),
                SolrSchema.REF, ClientUtils.escapeQueryChars(ref),
                SolrSchema.POS, ClientUtils.escapeQueryChars(String.valueOf(pos)),
                SolrSchema.ALTS, ClientUtils.escapeQueryChars(alt)
        );

        logger.debug("Query: " + queryString);

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .addSort(SolrSchema.ID, SolrQuery.ORDER.desc)
                // required for cursor
                .setTimeAllowed(0);


        QueryResponse resp = null;

        q.setRows(10);
        q.set(GroupParams.GROUP, true);
        q.set(GroupParams.GROUP_FIELD, SolrSchema.INDIVIDUAL);
        q.set(GroupParams.GROUP_LIMIT, 1);

        String cursor = CursorMarkParams.CURSOR_MARK_START;
        String oldCursor = null;

        while (!cursor.equals(oldCursor)) {
            try {
                q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursor);

                resp = server.query(q);
                map.putAll(SolrVariantUtils.groupResponseToMap(resp.getGroupResponse()));

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
