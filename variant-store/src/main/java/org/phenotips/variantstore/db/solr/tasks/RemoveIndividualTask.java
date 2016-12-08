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

import org.phenotips.variantstore.db.solr.SolrVariantUtils;
import org.phenotips.variantstore.db.solr.VariantsSchema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Id$
 */
public class RemoveIndividualTask implements Callable<Object>
{

    private final Iterator<SolrDocument> iterator;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private SolrClient server;
    private final String individualId;

    /**
     * Remove an individual from solr.
     *
     * @param server    the solr server to run the task on
     * @param iterator  an iterator of the individual's variants
     * @param id the individual id
     */
    public RemoveIndividualTask(SolrClient server, Iterator<SolrDocument> iterator, String id) {
        this.server = server;
        this.iterator = iterator;
        this.individualId = id;
    }

    @Override
    public Object call() throws Exception {

        SolrDocument doc;

        /*
         * 1. Find all docs that have our individualId in the callsetIds
         * 2. For each doc, remove all traces of the individual:
         *   a. if this individual is the only individual, remove the doc
         *   b. remove callset from doc
         */

        while (iterator.hasNext()) {
            doc = iterator.next();

            server.deleteById((String) doc.get("id"));

            List<Object> values = new ArrayList<>(doc.getFieldValues(VariantsSchema.CALLSET_IDS));
            if (values.size() == 1) {
                continue;
            }

            doc.remove("_version_");
            SolrVariantUtils.removeCallsetFromDoc(doc, this.individualId);
            SolrVariantUtils.addDoc(ClientUtils.toSolrInputDocument(doc), server);
        }

        // removing individual id from the metadata document
        SolrDocument metaDoc = SolrVariantUtils.getMetaDocument(server);
        SolrVariantUtils.removeMultiFieldValue(metaDoc, VariantsSchema.CALLSET_IDS, this.individualId);
        SolrVariantUtils.addDoc(ClientUtils.toSolrInputDocument(metaDoc), server);

        // Solr should commit the fields at it's own optimal pace.
        // We want to commit once at the end to make sure any leftovers in solr buffers are available for querying.
        server.commit(true, true);
        return null;
    }
}
