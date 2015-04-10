package org.phenotips.variantstore.input.tsv;

import java.nio.file.Path;
import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * Th
 */
public class ExomiserTSVManager implements InputManager {
    /**
     * Given a file, get the VariantIterator
     *
     * @param path the path to the file
     * @param individualId the id of the individual
     * @param isPublic
     * @return the VariantIterator
     */
    public AbstractVariantIterator getIteratorForFile(Path path, String individualId, boolean isPublic) throws InputException {
        return new ExomiserTSVIterator(path, new VariantHeader(individualId, isPublic));
    }

    @Override
    public void addIndividual(String id, Path path) throws InputException {

    }

    @Override
    public Path getIndividual(String id) {
        return null;
    }

    @Override
    public void removeIndividual(String id) throws InputException {

    }

    /**
     * Given an individual, get the VariantIterator
     *
     * @param id
     * @param isPublic
     * @return
     */
    @Override
    public AbstractVariantIterator getIteratorForIndividual(String id, boolean isPublic) {
        return null;
    }

    @Override
    public void init(Path path) throws VariantStoreException {

    }

    @Override
    public void stop() {

    }
}
