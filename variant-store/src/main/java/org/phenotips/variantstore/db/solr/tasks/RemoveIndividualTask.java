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
package org.phenotips.variantstore.db.solr.tasks;

import org.phenotips.variantstore.db.solr.SolrUtils;
import org.phenotips.variantstore.db.solr.SolrVariantUtils;
import org.phenotips.variantstore.db.solr.VariantsSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @version $Id$
 */
public class RemoveIndividualTask implements Callable<Object>
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private SolrClient server;
    private final String individualId;

    /**
     * Remove an individual from solr.
     *
     * @param server    the solr server to run the task on
     * @param id        the individual id
     */
    public RemoveIndividualTask(SolrClient server, String id) {
        this.server = server;
        this.individualId = id;
    }

    @Override
    public Object call() throws Exception {
        /*
         * 1. Find all documents that have our individualId in the callsetIds except metadata document
         * 2. For each doc, remove all traces of the individual:
         *   a. remove the doc (we can not edit solr documents)
         *   b. if this individual is the only individual, continue to the next iteration
         *   c. else remove callset from doc and save a copy
         */

        checkArgument(!this.individualId.isEmpty());
        String queryString = String.format("%s:%s AND NOT %s:%s ", VariantsSchema.CALLSET_IDS, this.individualId,
                VariantsSchema.ID, SolrVariantUtils.METADATA_DOC_ID);
        SolrQuery q = new SolrQuery().setQuery(queryString);

        SolrUtils.processAllDocs(server, q, VariantsSchema.ID, new Function<Collection<SolrDocument>, Boolean>()
        {
            @Override
            public Boolean apply(Collection<SolrDocument> solrDocuments) {
                SolrDocument doc;
                Iterator<SolrDocument> iterator = solrDocuments.iterator();
                try {
                    while (iterator.hasNext()) {
                        doc = iterator.next();

                        server.deleteById((String) doc.get("id"));

                        if (doc.getFieldValues(VariantsSchema.CALLSET_IDS) == null) {
                            continue;
                        }

                        List<Object> values = new ArrayList<>(doc.getFieldValues(VariantsSchema.CALLSET_IDS));
                        if (values.size() == 1) {
                            continue;
                        }

                        doc.remove("_version_");
                        SolrVariantUtils.removeCallsetFromDoc(doc, individualId);
                        SolrVariantUtils.addDoc(SolrVariantUtils.toSolrInputDocument(doc), server);
                    }
                } catch (Exception e) {
                    logger.error("Error while removing individual from solr document", e);
                }
                return false;
            }
        });

        // removing individual id from the metadata document
        SolrDocument metaDoc = SolrVariantUtils.getMetaDocument(server);
        SolrVariantUtils.removeMultiFieldValue(metaDoc, VariantsSchema.CALLSET_IDS, this.individualId);
        SolrVariantUtils.addDoc(SolrVariantUtils.toSolrInputDocument(metaDoc), server);

        // Solr should commit the fields at it's own optimal pace.
        // We want to commit once at the end to make sure any leftovers in solr buffers are available for querying.
        server.commit(true, true);
        return null;
    }
}
