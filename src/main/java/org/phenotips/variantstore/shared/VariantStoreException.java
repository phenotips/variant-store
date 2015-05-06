package org.phenotips.variantstore.shared;

/**
 * A general Exception thrown by the Variant Store.
 *
 * @version $Id$
 */
public class VariantStoreException extends Exception
{

    /**
     * Create a new Exception with a message.
     * @param message the message
     */
    public VariantStoreException(String message) {
        super(message);
    }

    /**
     * Create a new Exception with a message and a chained exception.
     * @param message the message
     * @param e the chained exception
     */
    public VariantStoreException(String message, Exception e) {
        super(message, e);
    }
}
