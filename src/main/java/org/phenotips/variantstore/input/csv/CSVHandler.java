package org.phenotips.variantstore.input.csv;

import java.nio.file.Path;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputHandler;
import org.phenotips.variantstore.input.VariantIterator;

/**
 * Th
 */
public class CSVHandler implements InputHandler {
    /**
     * Given a file, get the VariantIterator
     *
     * @param path the path to the file
     * @param individualId the id of the individual
     * @return the VariantIterator
     */
    @Override
    public VariantIterator getIteratorForFile(Path path, String individualId) throws InputException {
        return new CSVIterator(path, individualId);
    }
}
