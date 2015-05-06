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
