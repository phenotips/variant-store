package org.phenotips.variantstore.input.tsv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.shared.VariantStoreException;

import org.apache.log4j.Logger;

/**
 * Th
 */
public class ExomiserTSVManager implements InputManager {
    private Logger logger = Logger.getLogger(ExomiserTSVManager.class);
    private Path path;

    @Override
    public void init(Path path) throws VariantStoreException {
        this.path = path;

        if (!Files.exists(this.path)) {
            logger.info("No TSV directory found, Creating it.");
            try {
                Files.createDirectories(this.path);
            } catch (IOException e) {
                throw new InputException("Unable to create directory.");
            }
        }

    }

    @Override
    public void stop() {

    }

    @Override
    public void addIndividual(String id, Path path) throws InputException {
        try {
            Files.copy(path, this.getIndividual(id), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new InputException("Error copying TSV for storage.", e);
        }
    }

    @Override
    public Path getIndividual(String id) {
        return this.path.resolve(id + ".variants.tsv");
    }

    @Override
    public void removeIndividual(String id) throws InputException {
        try {
            Files.delete(this.getIndividual(id));
        } catch (IOException e) {
            throw new InputException("Error removing TSV", e);
        }
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
        return new ExomiserTSVIterator(this.getIndividual(id), new VariantHeader(id, isPublic));
    }
}
