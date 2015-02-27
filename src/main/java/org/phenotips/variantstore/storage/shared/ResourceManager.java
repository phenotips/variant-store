package org.phenotips.variantstore.storage.shared;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.phenotips.variantstore.storage.StorageException;

/**
 * Created by meatcar on 2/26/15.
 */
public class ResourceManager {
    private static Logger logger = Logger.getLogger(ResourceManager.class);

    public static void copyResourcesToPath(String source, Path dest) throws StorageException {
        copyResourcesToPath(Paths.get(source), dest);
    }

    public static void copyResourcesToPath(final Path source, final Path dest) throws StorageException {
        // Check if storage dirs exist
        if (!Files.isDirectory(dest.resolve(source))) {
            // Get path to where the resources are stored
            Path resourceContainerPath = null;
            Class clazz = ResourceManager.class;
            if (clazz.getProtectionDomain().getCodeSource() != null) {
                resourceContainerPath = Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
            } else {
                throw new StorageException("This is running in a jar loaded from the system class loader. Don't know how to handle this.");
            }

            try {
                // The resources can be in a jar that we are running in or on the filesystem.
                if ("jar".equals(FilenameUtils.getExtension(resourceContainerPath.toString()))) {
                    // run from a jar
                    JarFile jar = new JarFile(resourceContainerPath.toFile());

                    for (JarEntry entry : Collections.list(jar.entries())) {
                        if (entry.getName().startsWith("solr")) {
                            if (entry.isDirectory()) {
                                Files.createDirectory(dest.resolve(entry.getName()));
                            } else {
                                Files.copy(jar.getInputStream(entry), dest.resolve(entry.getName()));
                            }
                        }
                    }

                } else {
                    // run from an IDE / filesystem
                    final Path resourcePath = resourceContainerPath.resolve(source);

                    Files.walkFileTree(resourcePath, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Path relFile = source.resolve(resourcePath.relativize(file));
                            Files.copy(file, dest.resolve(relFile));
                            return FileVisitResult.CONTINUE;
                        }
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            Path relDir = source.resolve(resourcePath.relativize(dir));
                            Files.createDirectory(dest.resolve(relDir));
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            } catch (IOException e) {
                throw new StorageException("Error setting up variant store, unable to install resources.", e);
            }
        }

    }

    public static void clearResources(Path path) throws StorageException {
        try {
            FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            throw new StorageException("Error clearing resources", e);
        }
    }
}

