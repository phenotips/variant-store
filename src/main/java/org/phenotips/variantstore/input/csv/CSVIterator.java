package org.phenotips.variantstore.input.csv;

import java.nio.file.Path;
import org.ga4gh.GAVariant;
import org.phenotips.variantstore.input.VariantIterator;

/**
 * Created by meatcar on 2/20/15.
 */
public class CSVIterator extends VariantIterator {
    public CSVIterator(Path path, String individualId) {
        super(path, individualId);
    }

    @Override
    public boolean hasNext() {
        return super.hasNext();
    }

    @Override
    public GAVariant next() {
        return super.next();
    }
}
