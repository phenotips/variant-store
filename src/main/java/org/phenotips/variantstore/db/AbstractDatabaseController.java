package org.phenotips.variantstore.db;

import java.nio.file.Path;
import java.util.concurrent.Future;
import org.phenotips.variantstore.input.AbstractVariantIterator;

/**
 * Created by meatcar on 2/20/15.
 */
public abstract class AbstractDatabaseController {
    protected Path path;

    protected abstract Path getStoragePathSuffix();

    public abstract void stop();

    public void init(Path path) throws DatabaseException {
        this.path = path.resolve(this.getStoragePathSuffix());
    }

    public abstract Future addIndividual(AbstractVariantIterator iterator) throws DatabaseException;

    public abstract Future removeIndividual(String id) throws DatabaseException;
}
