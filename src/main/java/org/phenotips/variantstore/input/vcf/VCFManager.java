package org.phenotips.variantstore.input.vcf;

import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.input.VariantIterator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Manage the raw VCF files that we store.
 *
 * @version $Id$
 */
public class VCFManager implements InputManager
{
    private static String suffix = ".vcf";
    private static Logger logger = Logger.getLogger(VCFManager.class);
    private Path path;

    @Override
    public void init(Path path) throws InputException {
        this.path = path;

        if (!Files.exists(this.path)) {
            logger.info("No VCF directory found, Creating it.");
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
            throw new InputException("Error copying VCF for storage.", e);
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
            throw new InputException("Error removing VCF", e);
        }
    }

    @Override
    public VariantIterator getIteratorForIndividual(String id, boolean isPublic) {
        return new VCFIterator(this.getIndividual(id), new VariantHeader(id, true));
    }

    @Override
    public List<String> getAllIndividuals() {
        final List<String> list = new ArrayList<>();

        try {
            Files.walkFileTree(this.path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isDirectory()) {
                        return FileVisitResult.CONTINUE;
                    }
                    String id = file.getFileName().toString();
                    id = StringUtils.removeEnd(id, suffix);
                    list.add(id);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Error getting all individuals");
        }

        return list;
    }
}
