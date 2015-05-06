package org.phenotips.variantstore.input;

import java.util.Iterator;

import org.ga4gh.GAVariant;

/**
 * An iterator that returns standard GAVariant objects.
 * @version $Id$
 */
public interface VariantIterator extends Iterator<GAVariant>
{
    /**
     * Get the meta information associated with the variants.
     * @return the meta info
     */
    VariantHeader getHeader();

    @Override
    boolean hasNext();

    @Override
    GAVariant next();

    @Override
    void remove();
}
