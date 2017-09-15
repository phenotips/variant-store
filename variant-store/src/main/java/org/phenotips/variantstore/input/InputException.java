/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.variantstore.input;

import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * An exception thrown by the input handlers.
 * @version $Id$
 */
@SuppressWarnings("serial")
public class InputException extends VariantStoreException
{
    /**
     * Create a new exception with a message.
     * @param message the message
     */
    public InputException(String message)
    {
        super(message);
    }

    /**
     * Create a new exception with a message and a chained exception.
     * @param message a message
     * @param e the chained exception
     */
    public InputException(String message, Exception e)
    {
        super(message, e);
    }
}
