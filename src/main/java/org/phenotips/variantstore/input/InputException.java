package org.phenotips.variantstore.input;

import org.phenotips.variantstore.VariantStoreException;

/**
 * An exception thrown by the input handlers
 */
public class InputException extends VariantStoreException {
    public InputException(String message) {
        super(message);
    }

    public InputException(String message, Exception e) {
        super(message, e);
    }
}
