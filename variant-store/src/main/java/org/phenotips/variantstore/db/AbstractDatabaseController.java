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
package org.phenotips.variantstore.db;

import java.nio.file.Path;

/**
 * @version $Id$
 */
public abstract class AbstractDatabaseController implements DatabaseController
{
    protected Path path;

    protected abstract Path getStoragePathSuffix();

    /**
     * Stop the DB.
     */
    public abstract void stop();

    /**
     * Initialize the db.
     *
     * @param path the path for the db to store resources in
     * @throws DatabaseException thrown if an error is encountered in initialization
     */
    public void init(Path path) throws DatabaseException {
        this.path = path.resolve(this.getStoragePathSuffix());
    }

}
