package org.phenotips.variantstore.storage;

import org.phenotips.variantstore.VariantStoreException;

/**
 * An exception thrown when an invalid file format is encountered.
 */
public class InvalidFileFormatException extends VariantStoreException{
    public InvalidFileFormatException(String message) {
        super(message);
    }

    public InvalidFileFormatException(String message, Exception e) {
        super(message, e);
    }
}
