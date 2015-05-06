package org.phenotips.variantstore.input;

import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * An exception thrown by the input handlers.
 * @version $Id$
 */
public class InputException extends VariantStoreException
{
    /**
     * Create a new exception with a message.
     * @param message the message
     */
    public InputException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and a chained exception.
     * @param message a message
     * @param e the chained exception
     */
    public InputException(String message, Exception e) {
        super(message, e);
    }
}
