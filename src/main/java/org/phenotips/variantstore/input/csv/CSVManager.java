package org.phenotips.variantstore.input.csv;

import java.nio.file.Path;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.input.AbstractVariantIterator;

/**
 * Th
 */
public class CSVManager implements InputManager {
    /**
     * Given a file, get the VariantIterator
     *
     * @param path the path to the file
     * @param individualId the id of the individual
     * @param isPublic
     * @return the VariantIterator
     */
    @Override
    public AbstractVariantIterator getIteratorForFile(Path path, String individualId, boolean isPublic) throws InputException {
        return new CSVIterator(path, new VariantHeader(individualId, isPublic));
    }
}
