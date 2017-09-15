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

import org.phenotips.variantstore.db.AbstractDatabaseController;
import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.db.solr.tasks.AddIndividualTask;
import org.phenotips.variantstore.db.solr.tasks.RemoveIndividualTask;
import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.shared.ResourceManager;
import org.phenotips.variantstore.shared.VariantUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.core.CoreContainer;
import org.ga4gh.GAVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages an embedded instance of solr.
 *
 * @version $Id$
 */
public class SolrController extends AbstractDatabaseController
{
    /**
     * the field name of the EXAC allele frequency value in the allele frequency query map.
     **/
    public static final String EXAC_FREQUENCY_FIELD = "EXAC";
    /**
     * the field name of the internal (db) allele frequency value in the allele frequency query map.
     */
    public static final String DB_FREQUENCY_FIELD = "PhenomeCentral";
    /** The error message for input parameters validation. **/
    public static final String ID_ERROR_MESSAGE = "id cannot be empty";
    /** The error message for input parameters validation. **/
    public static final String ENSEMBL_ID_ERROR_MESSAGE = "gene ensembl id cannot be empty";
    /** The error message for input parameters validation. **/
    public static final String NUMBER_ERROR_MESSAGE = "k cannot be equal or less than zero";
    /** The error message for input parameters validation. **/
    private static Logger logger = LoggerFactory.getLogger(SolrController.class);

    // ensure that insertion and deletions are done synchronously, one task at a time
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    private CoreContainer cores;
    private SolrClient server;

    /**
     * Create a SolrController, that will store it's files and configuration in a directory inside of rootPath.
     */
    public SolrController()
    {
        super();
    }

    @Override
    protected Path getStoragePathSuffix()
    {
        return Paths.get("solr/");
    }

    @Override
    public void init(Path path) throws DatabaseException
    {
        super.init(path);

        ResourceManager.copyResourcesToPath(this.getStoragePathSuffix(), this.path);

        // Spin Solr up
        logger.debug(String.valueOf(this.path));
        this.cores = new CoreContainer(this.path.toString());
        this.cores.load();
        this.server = new EmbeddedSolrServer(this.cores, "variants");
    }

    @Override
    public void stop()
    {
        this.executor.shutdownNow();
        this.cores.shutdown();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Future addIndividual(final VariantIterator iterator)
    {
        FutureTask task = new FutureTask<>(new AddIndividualTask(this.server, iterator));
        this.executor.submit(task);
        return task;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Future removeIndividual(String id) throws DatabaseException
    {
        FutureTask task = new FutureTask<>(new RemoveIndividualTask(this.server, id));
        this.executor.submit(task);
        return task;
    }

    @Override
    public List<GAVariant> getTopHarmfullVariants(String id, int n)
    {
        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);
        checkArgument(n > 0, "n cannot be equal or less than zero");

        List<GAVariant> list = new ArrayList<>();

        logger.debug(String.format("Searching for id:%s n:%s", id, n));

        String queryString = String.format("%s:PASS",
                VariantsSchema.getCallsetsFieldName(id, VariantsSchema.FILTER));

        logger.debug("Query string: " + queryString);

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(n)
                .setSort(VariantsSchema.getCallsetsFieldName(id, VariantsSchema.EXOMISER_VARIANT_SCORE),
                        SolrQuery.ORDER.desc);

        QueryResponse resp;

        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("Error getting individuals ", e);
            return list;
        }
        // Filter the variants further, to pull out each individual's variant.
        List<Map<String, GAVariant>> mapList = SolrVariantUtils.documentListToMapList(resp.getResults());
        for (Map<String, GAVariant> map : mapList) {
            if (map.containsKey(id)) {
                list.add(map.get(id));
            }
        }

        return list;
    }

    /**
     * GA4GH Beacon implementation. Return the allele count for this specific variant in the database.
     *
     * @param chr    chr
     * @param pos    pos
     * @param allele allele
     *
     * @return the allele count for this specific variant in the db.
     */
    @Override
    public int beacon(String chr, long pos, String allele)
    {
        checkNotNull(chr);
        checkArgument(!"".equals(chr));
        checkArgument(pos > 0);
        checkNotNull(allele);

        String queryString = String.format("%s:%s AND %s:%s AND %s:%S",
                VariantsSchema.CHROM, chr,
                VariantsSchema.START, pos - 1,
                VariantsSchema.ALT, allele);

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(1);

        QueryResponse resp;
        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("Beacon Solr Exception", e);
            return 0;
        }

        SolrDocumentList results = resp.getResults();
        if (results.size() > 0) {
            return (int) results.get(0).get(VariantsSchema.AC_TOT);
        }
        return 0;
    }

    @Override
    public long getTotNumVariants()
    {
        String queryString = "*:*";

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(0);

        QueryResponse resp;
        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("TotNumVariants Solr Exception", e);
            return 0;
        }

        return resp.getResults().getNumFound();
    }

    /**
     * Given an individual id, return all the genes stored for that individual.
     *
     * @param id the individual's id
     *
     * @return the set of genes.
     */
    @Override
    public Set<String> getAllGenesForIndividual(String id)
    {
        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);
        logger.debug("getAllGenesForIndividual(" + id + ")");

        final Set<String> set = new HashSet<>();

        String queryString = String.format("%s:%s AND NOT %s:%s ", VariantsSchema.CALLSET_IDS,
            ClientUtils.escapeQueryChars(id), VariantsSchema.ID, SolrVariantUtils.METADATA_DOC_ID);

        SolrQuery q = new SolrQuery().setQuery(queryString);
        // sort on unique

        try {
            SolrUtils.processAllDocs(this.server, q, VariantsSchema.ID, new Function<Collection<SolrDocument>,
                Boolean>()
            {
                @Override
                public Boolean apply(Collection<SolrDocument> solrDocuments)
                {
                    for (SolrDocument doc : solrDocuments) {
                        String gene = (String) doc.get(VariantsSchema.GENE);
                        if (StringUtils.isNotBlank(gene)) {
                            set.add((String) doc.get(VariantsSchema.GENE));
                        }
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            logger.error("AllGenesForIndividual Solr Exception", e);
            return set;
        }

        return set;
    }

    @Override
    public Double getGeneScore(String id, String gene)
    {
        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);
        checkArgument(!StringUtils.isBlank(gene), "gene cannot be empty");
        logger.debug(String.format("getGeneScore(%s, %s)", id, gene));

        String ensemblId = VariantUtils.getEnsemblId(gene);
        String queryString = String.format("%s:%s AND %s:%s",
                VariantsSchema.CALLSET_IDS, ClientUtils.escapeQueryChars(id),
                VariantsSchema.GENE, ClientUtils.escapeQueryChars(ensemblId));

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(1);

        QueryResponse resp;
        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("GeneScore Solr Exception", e);
            return null;
        }

        SolrDocumentList results = resp.getResults();
        if (results.size() != 1) {
            return null;
        }

        Float result = (Float) results.get(0).get(VariantsSchema.getCallsetsFieldName(
                id, VariantsSchema.EXOMISER_GENE_COMBINED_SCORE));

        if (result != null) {
            return result.doubleValue();
        } else {
            return null;
        }
    }

    @Override
    public List<String> getTopGenesForIndividual(String id, Integer k)
    {
        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);
        checkArgument(k > 0, NUMBER_ERROR_MESSAGE);
        logger.debug(String.format("getTopGenesForIndividual(%s, %d)", id, k));

        final List<String> list = new LinkedList<>();

        String queryString = String.format("%s:%s", VariantsSchema.CALLSET_IDS, ClientUtils.escapeQueryChars(id));

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(k)
                .setSort(
                        VariantsSchema.getCallsetsFieldName(id, VariantsSchema.EXOMISER_GENE_COMBINED_SCORE),
                        SolrQuery.ORDER.desc)
                .setParam(GroupParams.GROUP, true)
                .setParam(GroupParams.GROUP_FIELD, VariantsSchema.GENE);

        QueryResponse resp;
        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("Solr Exception", e);
            return list;
        }

        for (GroupCommand command : resp.getGroupResponse().getValues()) {
            for (Group group : command.getValues()) {
                list.add(group.getGroupValue());
            }
        }

        return list;
    }

    @Override
    public List<GAVariant> getTopHarmfullVariantsForGene(String id, String gene, Integer k)
    {
        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);
        checkArgument(!StringUtils.isBlank(gene), "gene cannot be empty");
        checkArgument(k > 0, NUMBER_ERROR_MESSAGE);
        logger.debug(String.format("getTopHarmfullVariantsForGene(%s, %s, %d)", id, gene, k));

        String ensemblId = VariantUtils.getEnsemblId(gene);
        final List<GAVariant> list = new LinkedList<>();

        String queryString = String.format("%s:%s AND  %s:%s",
                VariantsSchema.CALLSET_IDS, ClientUtils.escapeQueryChars(id),
                VariantsSchema.GENE, ClientUtils.escapeQueryChars(ensemblId));

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setRows(k)
                .setSort(
                        VariantsSchema.getCallsetsFieldName(id, VariantsSchema.EXOMISER_VARIANT_SCORE),
                        SolrQuery.ORDER.desc);

        QueryResponse resp;
        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("Caught Solr Exception", e);
            return list;
        }

        for (SolrDocument doc : resp.getResults()) {
            list.add(SolrVariantUtils.docToVariant(doc, id));
        }

        return list;
    }

    @Override
    public List<GAVariant> getTopHarmfulWithGene(String id,
                                                 int n,
                                                 String gene,
                                                 List<String> variantEffects,
                                                 Map<String, Double> alleleFrequencies)
    {

        List<GAVariant> list = new ArrayList<>();

        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);
        checkArgument(!StringUtils.isBlank(gene), "gene cannot be empty");
        checkArgument(n > 0, NUMBER_ERROR_MESSAGE);

        String ensemblId = VariantUtils.getEnsemblId(gene);
        checkArgument(!"".equals(ensemblId), ENSEMBL_ID_ERROR_MESSAGE);

        checkNotNull(variantEffects);
        checkArgument(variantEffects.size() != 0);

        checkNotNull(alleleFrequencies);
        checkArgument(alleleFrequencies.size() != 0);

        /** Build Query String **/

        String effectQuery = "";
        for (String effect : variantEffects) {
            effectQuery += String.format("%s:%s OR ",
                    VariantsSchema.GENE_EFFECT, ClientUtils.escapeQueryChars(effect));
        }
        // Strip final ' OR '
        effectQuery = effectQuery.substring(0, effectQuery.length() - 4);

        // Find ExAC AF under the specified frequency, or where ExAC is null.
        String exacQuery = String.format("(-%s:[* TO *] AND *:*) OR %s:[0 TO %s] ",
                VariantsSchema.EXAC_AF, VariantsSchema.EXAC_AF,
                ClientUtils.escapeQueryChars(String.valueOf(alleleFrequencies.get(EXAC_FREQUENCY_FIELD)))
        );

        String queryString = String.format("s:%s AND %s:%s AND (%s) AND (%s)",
                VariantsSchema.CALLSET_IDS, ClientUtils.escapeQueryChars(id),
                VariantsSchema.GENE, ClientUtils.escapeQueryChars(ensemblId),
                effectQuery,
                exacQuery
        );

        SolrQuery q = new SolrQuery()
                .setRows(n)
                .setQuery(queryString);

        QueryResponse resp = null;

        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("Error getting individuals with variants", e);
            return list;
        }
        Map<String, List<GAVariant>> map = SolrVariantUtils.variantListToCallsetMap(
                SolrVariantUtils.documentListToMapList(resp.getResults()));

        if (map.containsKey(id)) {
            list = map.get(id);
        }
        return list;
    }

    /**
     * beacon.
     *
     * @return the allele count
     */
    public int beacon()
    {
        return 0;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String gene,
                                                               List<String> variantEffects,
                                                               Map<String, Double> alleleFrequencies,
                                                               int n, int totIndividuals)
    {
        Map<String, List<GAVariant>> map = new HashMap<>();

        checkArgument(!StringUtils.isBlank(gene), "gene cannot be empty");
        checkArgument(n > 0, "n cannot be equal or less than zero");

        String ensemblId = VariantUtils.getEnsemblId(gene);
        checkArgument(!"".equals(ensemblId), ENSEMBL_ID_ERROR_MESSAGE);

        checkNotNull(variantEffects, "effects cannot be null");
        checkArgument(variantEffects.size() != 0, "effects cannot be empty");

        checkNotNull(alleleFrequencies, "allele frequencies cannot be null");
        checkArgument(alleleFrequencies.size() != 0, "allele frequencies cannot be empty");

        /** Build Query String **/

        // alleleFreq = copiesSum / 2*totIndividuals
        int copiesSum = (int) (alleleFrequencies.get(DB_FREQUENCY_FIELD) * totIndividuals * 2);

        StringBuilder builder = new StringBuilder();
        for (String effect : variantEffects) {
            builder.append(VariantsSchema.GENE_EFFECT)
                    .append(":")
                    .append(ClientUtils.escapeQueryChars(effect))
                    .append(" OR ");
        }
        // Strip final ' OR '
        String effectQuery = builder.toString();
        effectQuery = effectQuery.substring(0, effectQuery.length() - 4);

        // Find ExAC AF under the specified frequency, or where ExAC is null.
        String exacQuery = String.format("(-%s:[* TO *] AND *:*) OR %s:[0 TO %s]",
                VariantsSchema.EXAC_AF, VariantsSchema.EXAC_AF,
                ClientUtils.escapeQueryChars(String.valueOf(alleleFrequencies.get(EXAC_FREQUENCY_FIELD)))
        );

        String queryString = String.format("%s:[* TO %s] AND %s:%s AND (%s) AND (%s)",
                VariantsSchema.AC_TOT, copiesSum,
                VariantsSchema.GENE, ClientUtils.escapeQueryChars(ensemblId),
                effectQuery,
                exacQuery
        );

        SolrQuery q = new SolrQuery()
                .setQuery(queryString)
                .setFilterQueries(queryString);

        q.setRows(300);

        QueryResponse resp;

        try {
            resp = this.server.query(q);
            map = SolrVariantUtils.variantListToCallsetMap(SolrVariantUtils.documentListToMapList(resp.getResults()));
        } catch (SolrServerException | IOException e) {
            logger.error("Error getting individals with variant", e);
        }

        return map;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt)
    {
        //TODO: NEWINIESE
        Map<String, List<GAVariant>> map = new HashMap<>();
        return map;
    }

    @Override
    public List<String> getAllIndividuals()
    {
        List<String> ids = new ArrayList<String>();
        try {
            SolrDocument metaDoc = SolrVariantUtils.getMetaDocument(this.server);
            if (metaDoc.getFieldValues(VariantsSchema.CALLSET_IDS) != null) {
                List<Object> values = new ArrayList<>(metaDoc.getFieldValues(VariantsSchema.CALLSET_IDS));
                for (Object item : values) {
                    ids.add((String) item);
                }
            }
        } catch (SolrServerException | IOException e) {
            logger.error("Error getting all individals stored in the variant store", e);
        }
        return ids;
    }

    @Override
    public List<GAVariant> getAllVariantsForIndividual(String id)
    {
        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);

        List<GAVariant> variants = new ArrayList<GAVariant>();
        SolrDocumentList list = getAllVariantsDocumentsForIndividual(id);
        if (list != null) {
            variants = SolrVariantUtils.documentListToGAVarintList(list, id);
        }
        return variants;
    }

    private SolrDocumentList getAllVariantsDocumentsForIndividual(String id)
    {
        checkArgument(!StringUtils.isBlank(id), ID_ERROR_MESSAGE);

        String queryString = String.format("%s:%s ", VariantsSchema.CALLSET_IDS, ClientUtils.escapeQueryChars(id));
        SolrQuery q = new SolrQuery().setQuery(queryString);

        QueryResponse resp = null;
        try {
            resp = this.server.query(q);
        } catch (SolrServerException | IOException e) {
            logger.error("Error getting variants for individual with id " + id, e);
            return null;
        }

        return resp.getResults();
    }
}
