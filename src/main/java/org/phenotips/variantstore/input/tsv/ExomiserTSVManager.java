package org.phenotips.variantstore.input.tsv;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.phenotips.variantstore.input.AbstractVariantIterator;
import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.shared.VariantStoreException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Th
 */
public class ExomiserTSVManager implements InputManager {
    private Logger logger = Logger.getLogger(ExomiserTSVManager.class);
    private Path path;

    private static String suffix = ".variants.tsv";

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
        return this.path.resolve(id + suffix);
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

    @Override
    public List<String> getAllIndividuals() {
        final List<String> list = new ArrayList<>();

        try {
            Files.walkFileTree(this.path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isDirectory()) {
                        return FileVisitResult.CONTINUE;
                    }
                    String filename = file.getFileName().toString();
                    String id = StringUtils.removeEnd(filename, suffix);
                    list.add(id);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Error getting all individuals", e);
        }

        return list;
    }
}
