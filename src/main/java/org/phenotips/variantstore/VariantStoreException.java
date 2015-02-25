package org.phenotips.variantstore;

/**
 * A general Exception thrown by the Variant Store
 */
public class VariantStoreException extends Exception {
    public VariantStoreException(String message) {
        super(message);
    }

    public VariantStoreException(String message, Exception e) {
        super(message, e);
    }
}
