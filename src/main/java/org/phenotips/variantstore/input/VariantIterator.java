package org.phenotips.variantstore.input;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.ga4gh.GAVariant;

/**
 * An Iterator that extracts and exposes ga4gh-standard Variants from some other representation.
 */
public abstract class VariantIterator implements Iterator<GAVariant> {
    private Path path;
    private String individualId;

    public VariantIterator(Path path, String individualId) {
        this.path = path;
        this.individualId = individualId;
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
