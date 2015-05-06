package org.phenotips.variantstore.db;

import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * An exception thrown by the input handlers.
 * @version $Id$
 */
public class DatabaseException extends VariantStoreException
{
    /**
     * Create a new exception with a message.
     * @param message the exception message
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and a chained exception.
     * @param message the message
     * @param e the chained exception
     */
    public DatabaseException(String message, Exception e) {
        super(message, e);
    }
}
