package org.phenotips.variantstore.input;

import java.nio.file.Path;
import org.phenotips.variantstore.shared.Service;
import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * An input handler is responsible for orchistrating the transformation of variant data from one format to a
 * standard representation that then can be used by the rest of the Variant Store.
 */
public interface InputManager extends Service {
    public void addIndividual(String id, Path path) throws InputException;
    public Path getIndividual(String id);
    public void removeIndividual(String id) throws InputException;
    /**
     * Given an individual, get the VariantIterator
     * @param id
     * @param isPublic
     * @return
     */
    public AbstractVariantIterator getIteratorForIndividual(String id, boolean isPublic);
}
