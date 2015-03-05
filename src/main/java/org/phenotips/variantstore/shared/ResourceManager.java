package org.phenotips.variantstore.shared;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.phenotips.variantstore.db.DatabaseException;

/**
 * Created by meatcar on 2/26/15.
 */
public class ResourceManager {
    private static Logger logger = Logger.getLogger(ResourceManager.class);

    public static void copyResourcesToPath(String source, Path dest) throws DatabaseException {
        copyResourcesToPath(Paths.get(source), dest);
    }

    public static void copyResourcesToPath(final Path source, Path dest) throws DatabaseException {
        // Check if storage dirs exists
        if (Files.isDirectory(dest)) {
            return;
        }

        // Make sure that we aren't double-nesting directories
        if (dest.endsWith(source)) {
            dest = dest.getParent();
        }

        // Get path to where the resources are stored
        Path resourceContainerPath = null;
        Class clazz = ResourceManager.class;
        if (clazz.getProtectionDomain().getCodeSource() != null) {
            resourceContainerPath = Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        } else {
            throw new DatabaseException("This is running in a jar loaded from the system class loader. Don't know how to handle this.");
        }

        try {

            logger.debug("Copying resources from " + source + " to " + dest);
            logger.debug("Making dir " + dest);
            Files.createDirectories(dest);

            // The resources can be in a jar that we are running in or on the filesystem.
            if ("jar".equals(FilenameUtils.getExtension(resourceContainerPath.toString()))) {

                // run from a jar
                JarFile jar = new JarFile(resourceContainerPath.toFile());

                for (JarEntry entry : Collections.list(jar.entries())) {

                    if (entry.getName().startsWith(source.toString())) {
                        if (entry.isDirectory()) {
                            logger.debug("Making dir " + entry.getName());
                            Files.createDirectory(dest.resolve(entry.getName()));
                        } else {
                            logger.debug("Copying " + entry.getName());
                            Files.copy(jar.getInputStream(entry), dest.resolve(entry.getName()));
                        }
                    }

                }

            } else {

                // run from an IDE / filesystem
                final Path resourcePath = resourceContainerPath.resolve(source);

                final Path finalDest = dest;
                Files.walkFileTree(resourcePath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path relFile = source.resolve(resourcePath.relativize(file));
                        logger.debug("Copying " + file + " to " + finalDest.resolve(relFile));
                        Files.copy(file, finalDest.resolve(relFile));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path relDir = source.resolve(resourcePath.relativize(dir));
                        logger.debug("Making dir " + finalDest.resolve(relDir));
                        Files.createDirectory(finalDest.resolve(relDir));
                        return FileVisitResult.CONTINUE;
                    }
                });

            }
        } catch (IOException e) {
            throw new DatabaseException("Error setting up variant store, unable to install resources.", e);
        }

    }

    public static void clearResources(Path path) throws DatabaseException {
        try {
            FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            throw new DatabaseException("Error clearing resources", e);
        }
    }
}

