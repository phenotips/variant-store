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
package org.phenotips.variantstore.input;

/**
 * The metadata associated with a collection of variants.
 *
 * @version $Id$
 */
public class VariantHeader
{
    private String individualId;
    private boolean isPublic;

    /**
     * Create a new header.
     * @param individualId the individual's id
     * @param isPublic can these variants be used in aggregate searches
     */
    public VariantHeader(String individualId, boolean isPublic) {
        this.individualId = individualId;
        this.isPublic = isPublic;
    }

    /**
     * Get the individual id.
     * @return the individuals id
     */
    public String getIndividualId() {
        return individualId;
    }

    /**
     * Set the individual's id.
     * @param individualId the id
     */
    public void setIndividualId(String individualId) {
        this.individualId = individualId;
    }

    /**
     * Return whether the variants can be used in aggregate queries.
     * @return whether the variants can be used in aggregate queries.
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Set whether the variants may be used in aggregate queries.
     * @param isPublic whether the variants may be used in aggregate queries
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
