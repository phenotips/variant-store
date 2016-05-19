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

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CursorMarkParams;

/**
 * A general utils class to help working with Solr.
 *
 * @version $Id$
 */
public final class SolrUtils
{
    private SolrUtils() {
        throw new AssertionError();
    }

    /**
     * Loop over all the documents returned by the query. This method queries the DB multiple times. Every time we get
     * data back, we pass it onto a processor, and stop processing data if the processor tells us it's had enough.
     *
     * @param server    the solr db
     * @param q         the query
     * @param uniqueKey the solr uniqueKey field to sort on. Required for solr's Cursor functionality.
     *@param processor the processor to handle the data. If the function returns true, we stop fetching more data.
     *  @throws IOException
     * @throws SolrServerException
     */
    static void processAllDocs(SolrClient server, SolrQuery q,
                               String uniqueKey, Function<Collection<SolrDocument>, Boolean> processor
    ) throws IOException, SolrServerException {
        boolean done = false;
        String oldCursorMark;
        String cursorMark = CursorMarkParams.CURSOR_MARK_START;
        QueryResponse resp;

        // Cursor functionality requires a sort containing a uniqueKey field tie breaker
        q.addSort(uniqueKey, SolrQuery.ORDER.desc);

        while (!done) {
            oldCursorMark = cursorMark;
            q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
            resp = server.query(q);
            done = processor.apply(resp.getResults());
            cursorMark = resp.getNextCursorMark();
            done = done || oldCursorMark.equals(cursorMark);
        }
    }
}
