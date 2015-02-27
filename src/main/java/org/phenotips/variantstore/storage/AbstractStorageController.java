package org.phenotips.variantstore.storage;

import java.nio.file.Path;
import java.util.concurrent.Future;
import org.phenotips.variantstore.input.AbstractVariantIterator;

/**
 * Created by meatcar on 2/20/15.
 */
public abstract class AbstractStorageController {
    protected Path storePath;
    public AbstractStorageController(Path rootPath) {
        this.storePath = rootPath.resolve(this.getStoragePathSuffix());
    }

    protected abstract Path getStoragePathSuffix();
    public abstract void stop();

    public abstract Future addIndividual(AbstractVariantIterator iterator) throws StorageException;

    public abstract Future removeIndividual(String id) throws StorageException;
}
