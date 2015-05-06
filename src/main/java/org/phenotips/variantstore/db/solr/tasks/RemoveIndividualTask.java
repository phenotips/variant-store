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

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

/**
 * @version $Id$
 */
public class RemoveIndividualTask implements Callable<Object>
{

    private SolrServer server;
    private String id;

    /**
     * Remove an individual from solr.
     * @param server the solr server to run the task on
     * @param id the id of the individual
     */
    public RemoveIndividualTask(SolrServer server, String id) {
        this.server = server;
        this.id = id;
    }

    @Override
    public Object call() throws Exception {
        try {
            server.deleteByQuery(String.format("individual:%s", id));
            server.commit();
        } catch (SolrServerException | IOException e) {
            throw new DatabaseException("Error removing individual from solr", e);
        }
        return null;
    }
}
