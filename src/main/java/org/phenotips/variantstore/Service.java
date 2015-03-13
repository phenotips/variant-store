package org.phenotips.variantstore;

import java.nio.file.Path;

/**
 * Created by meatcar on 3/12/15.
 */
public interface Service {
    public void init(Path path) throws VariantStoreException;
    public void stop();
}
