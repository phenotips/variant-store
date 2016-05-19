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

import org.phenotips.variantstore.db.DatabaseException;
import org.phenotips.variantstore.db.solr.SolrVariantUtils;
import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.shared.GACallInfoFields;
import org.phenotips.variantstore.shared.VariantUtils;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.ga4gh.GAVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Id$
 */
public class RemoveIndividualTask implements Callable<Object>
{

    private final VariantIterator iterator;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private SolrClient server;

    /**
     * Remove an individual from solr.
     *
     * @param server   the solr server to run the task on
     * @param iterator an iterator of the individual's variants
     */
    public RemoveIndividualTask(SolrClient server, VariantIterator iterator) {
        this.server = server;
        this.iterator = iterator;
    }

    @Override
    public Object call() throws Exception {
        GAVariant variant;
        SolrQuery q;
        SolrDocument resp;
        SolrDocument doc;

        int hashCollisions = 0;
        int hashMisses = 0;

        while (iterator.hasNext()) {
            variant = iterator.next();

            // skip filter!= PASS
            if (!"PASS".equals(VariantUtils.getInfo(variant.getCalls().get(0), GACallInfoFields.FILTER))) {
                continue;
            }

            /**
             * Query Solr for existing variant
             */
            String hash = SolrVariantUtils.getHash(variant);

            resp = null;
            try {
                resp = server.getById(hash);
            } catch (SolrServerException | IOException e) {
                logger.error("Failed to check for an existing variant", e);
                continue;
            }

            if (resp == null) {
                // our variant is totally new. how did this happen?
                logger.debug("variant not found");
                hashMisses += 1;
                continue;
            }

            // found a doc, use it.
            doc = resp;
            doc.remove("_version_");
            server.deleteById(hash);
            hashCollisions++;

            SolrVariantUtils.removeVariantFromDoc(
                    doc,
                    variant,
                    iterator.getHeader().getIndividualId(),
                    iterator.getHeader().isPublic());
        }
        logger.debug("csv: Hash Collisions: " + hashCollisions);
        logger.debug("csv: Hash Misses: " + hashMisses);

        // Solr should commit the fields at it's own optimal pace.
        // We want to commit once at the end to make sure any leftovers in solr buffers are available for querying.
        server.commit(true, true);
        return null;
    }

    private void addDoc(SolrInputDocument doc) throws DatabaseException {
        try {
            server.add(doc);
            doc.clear();
        } catch (SolrServerException | IOException e) {
            throw new DatabaseException("Error adding variants to Solr", e);
        }
    }

}
