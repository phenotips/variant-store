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
