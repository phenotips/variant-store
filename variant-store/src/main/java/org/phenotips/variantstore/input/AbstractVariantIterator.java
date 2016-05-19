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

import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.ga4gh.GAVariant;

/**
 * An Iterator that wraps around a file, and enables the access of the variants stored in the file. The variants are
 * exposed as GA4GH GAVariant objects.
 *
 * @version $Id$
 */
public abstract class AbstractVariantIterator implements VariantIterator
{
    protected Path path;
    protected VariantHeader header;

    /**
     * Initialize the variant iterator.
     * @param path the file to iterate over
     * @param header information associated with the file
     */
    public AbstractVariantIterator(Path path, VariantHeader header) {
        this.path = path;
        this.header = header;
    }

    @Override
    public VariantHeader getHeader() {
        return header;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public GAVariant next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
