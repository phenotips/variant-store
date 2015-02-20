package org.phenotips.variantstore.storage;

import java.nio.file.Path;
import java.util.concurrent.Future;
import org.phenotips.variantstore.input.VariantIterator;

/**
 * Created by meatcar on 2/20/15.
 */
public abstract class StorageController {
    private Path storePath;
    public StorageController(Path storePath) {
        this.storePath = storePath;
    }

    public abstract Future addIndividual(String id, VariantIterator iterator);
    public abstract Future removeIndividual(String id);
}
