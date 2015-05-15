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
package org.phenotips.variantstore.db.solr.tasks;

import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.db.solr.SolrSchema;
import org.phenotips.variantstore.db.solr.SolrVariantUtils;
import org.phenotips.variantstore.input.VariantIterator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.ga4gh.GACall;
import org.ga4gh.GAVariant;

/**
 * @version $Id$
 */
public class AddIndividualTask implements Callable<Object>
{

    private final SolrClient server;
    private final VariantIterator iterator;

    /**
     * Initialize the task.
     *
     * @param server   the SolrServer to run the task on
     * @param iterator the variants to add
     */
    public AddIndividualTask(SolrClient server, VariantIterator iterator) {
        this.server = server;
        this.iterator = iterator;
    }

    @Override
    public Object call() throws Exception {
        GAVariant variant;
        Map<String, List<String>> info;

        while (iterator.hasNext()) {
            variant = iterator.next();
            SolrInputDocument doc;

            for (SolrDocument plainDoc : SolrVariantUtils.variantToDocs(variant)) {
                doc = ClientUtils.toSolrInputDocument(plainDoc);

                doc.setField("individual", iterator.getHeader().getIndividualId());

                if (iterator.getHeader().isPublic()) {
                    doc.setField("is_public", true);
                }

                addDoc(doc);

            }



        }

        // Solr should commit the fields at it's own optimal pace.
        // We want to commit once at the end to make sure any leftovers in solr buffers are available for querying.
        server.commit();
        return null;
    }

    private void addDoc(SolrInputDocument doc) throws DatabaseException {
        try {
            server.add(doc);
            doc.clear();
        } catch (SolrServerException | IOException e) {
            throw new DatabaseException(String.format("Error adding variants to Solr"), e);
        }
    }
}
