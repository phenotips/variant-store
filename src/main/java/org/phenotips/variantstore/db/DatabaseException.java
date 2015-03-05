package org.phenotips.variantstore.db;

import org.phenotips.variantstore.VariantStoreException;

/**
 * An exception thrown by the input handlers
 */
public class DatabaseException extends VariantStoreException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Exception e) {
        super(message, e);
    }
}
