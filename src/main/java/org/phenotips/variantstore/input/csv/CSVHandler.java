package org.phenotips.variantstore.input.csv;

import java.nio.file.Path;
import org.phenotips.variantstore.input.InputHandler;
import org.phenotips.variantstore.input.VariantIterator;

/**
 * Created by meatcar on 2/20/15.
 */
public class CSVHandler implements InputHandler {
    /**
     * Given a file, get the VariantIterator
     *
     * @param path
     * @param individualId
     * @return
     */
    @Override
    public VariantIterator getIteratorForFile(Path path, String individualId) {
        return null;
    }
}
