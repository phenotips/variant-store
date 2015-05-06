package org.phenotips.variantstore.shared;

import java.nio.file.Path;

/**
 * A generic service that can be started and stopped.
 * @version $Id$
 */
public interface Service
{
    /**
     * Initialize the Service.
     * @param path a path to store resources specific to this service
     * @throws VariantStoreException if an error occurs
     */
    void init(Path path) throws VariantStoreException;

    /**
     * Stop the service.
     */
    void stop();
}
