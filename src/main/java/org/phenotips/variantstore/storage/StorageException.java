package org.phenotips.variantstore.storage;

import org.phenotips.variantstore.VariantStoreException;

/**
 * An exception thrown by the input handlers
 */
public class StorageException extends VariantStoreException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Exception e) {
        super(message, e);
    }
}
