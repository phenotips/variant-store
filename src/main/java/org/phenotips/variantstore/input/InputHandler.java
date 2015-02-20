package org.phenotips.variantstore.input;

import java.nio.file.Path;

/**
 * An input handler is responsible for delegating the transformation of variant data from one format to a
 * standard representation that then can be used by the rest of the Variant Store.
 */
public interface InputHandler {
    /**
     * Given a file, get the VariantIterator
     * @param path
     * @param individualId
     * @return
     */
    public VariantIterator getIteratorForFile(Path path, String individualId);
}
