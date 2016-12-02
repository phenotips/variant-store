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

import org.phenotips.variantstore.shared.Service;

import java.nio.file.Path;

/**
 * An input handler is responsible for orchestrating the transformation of variant data from one format to a
 * standard representation that then can be used by the rest of the Variant Store.
 *
 * @version $Id$
 */
public interface InputManager extends Service
{
    /**
     * Add an individual's data file for safekeeping.
     * @param id the id of the individual
     * @param path the path to the file
     * @throws InputException if an error is encountered.
     */
    void addIndividual(String id, Path path) throws InputException;

    /**
     * Get the path to the individual's data file.
     * @param id the id of the individual
     * @return a path to the file.
     */
    Path getIndividual(String id);

    /**
     * Remove an individual's file from the file store.
     * @param id the id of the individual
     * @throws InputException if an error is encountered.
     */
    void removeIndividual(String id) throws InputException;

    /**
     * Given an individual, get the VariantIterator.
     *
     * @param id the id of the individual
     * @param isPublic can the variants be used for aggregate data.
     * @return the variant iterator
     */
    VariantIterator getIteratorForIndividual(String id, boolean isPublic);

    /**
     * Given an individual, get the Variant Iterator. The individual is assumed to be private.
     * @param id the id of the individual
     * @return the variant iterator
     */
    VariantIterator getIteratorForIndividual(String id);
}
