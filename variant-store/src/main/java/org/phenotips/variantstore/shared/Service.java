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
package org.phenotips.variantstore.shared;

import java.nio.file.Path;

/**
 * A generic service that can be started and stopped.
 * @version $Id$
 */
public interface Service
{
    /**
     * Initialize the Service.
     * @param path a path to store resources specific to this service
     * @throws VariantStoreException if an error occurs
     */
    void init(Path path) throws VariantStoreException;

    /**
     * Stop the service.
     */
    void stop();
}
