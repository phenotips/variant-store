/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.variantstore.shared;

import org.phenotips.variantstore.db.DatabaseException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage the resources for the application. Set them up, clean them up, the whole 9 yards.
 *
 * @version $Id$
 */
public final class ResourceManager
{
    private static Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    private ResourceManager() {
        throw new AssertionError();
    }

    /**
     * Copy resources bundled with the application to a specified folder.
     *
     * @param source the path of the resources relative to the resource folder
     * @param dest   the destination
     * @throws DatabaseException if an error occurs
     */
    public static void copyResourcesToPath(String source, Path dest) throws DatabaseException {
        copyResourcesToPath(Paths.get(source), dest);
    }

    /**
     * Copy resources bundled with the application to a specified folder.
     *
     * @param source      the path of the resources relative to the resource folder
     * @param destination the destination
     * @throws DatabaseException if an error occurs
     */
    public static void copyResourcesToPath(final Path source, Path destination) throws DatabaseException {
        copyResourcesToPath(source, destination, ResourceManager.class);
    }

    /**
     * Copy resources bundled with the application to a specified folder.
     *
     * @param source      the path of the resources relative to the resource folder
     * @param destination the destination
     * @param clazz       the class that owns the resource we want
     * @throws DatabaseException if an error occurs
     */
    public static void copyResourcesToPath(final Path source, Path destination, Class<?> clazz)
        throws DatabaseException {
        Path dest = destination;
        // Check if storage dirs exists
        if (Files.isDirectory(dest)) {
            return;
        }

        // Make sure that we aren't double-nesting directories
        if (dest.endsWith(source)) {
            dest = dest.getParent();
        }

        if (clazz.getProtectionDomain().getCodeSource() == null) {
            throw new DatabaseException("This is running in a jar loaded from the system class loader. "
                    + "Don't know how to handle this.");
        }

        Path resourcesPath = Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());

        try {

            // make destination folder
            Files.createDirectories(dest);

            // if we are running in a jar, get the resources from the jar
            if ("jar".equals(FilenameUtils.getExtension(resourcesPath.toString()))) {

                copyResourcesFromJar(resourcesPath, source, dest);

                // if running from an IDE or the filesystem, get the resources from the folder
            } else {

                copyResourcesFromFilesystem(resourcesPath.resolve(source), dest.resolve(source));

            }
        } catch (IOException e) {
            throw new DatabaseException("Error setting up variant store, unable to install resources.", e);
        }

    }

    /**
     * Copy resources recursively from a path on the filesystem to the destination folder.
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void copyResourcesFromFilesystem(final Path source, final Path dest) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(file);
                Files.copy(file, dest.resolve(relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(dir);
                Files.createDirectory(dest.resolve(relative));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Copy resources recursively from a folder specified by source in a jar file specified by jarPath
     * to a destination folder on the filesystem dest.
     *
     * @param jarPath the jar file
     * @param source  the folder on the filesystem
     * @param dest    the destination
     * @throws IOException
     */
    private static void copyResourcesFromJar(Path jarPath, Path source, Path dest) throws IOException {
        JarFile jar = new JarFile(jarPath.toFile());

        for (JarEntry entry : Collections.list(jar.entries())) {

            if (entry.getName().startsWith(source.toString())) {
                if (entry.isDirectory()) {
                    Files.createDirectory(dest.resolve(entry.getName()));
                } else {
                    Files.copy(jar.getInputStream(entry), dest.resolve(entry.getName()));
                }
            }

        }
    }

    /**
     * Delete the directory and any files in the path provided.
     *
     * @param path the directory to delete
     * @throws DatabaseException if an error occurs
     */
    public static void clearResources(Path path) throws DatabaseException {
        try {
            FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            throw new DatabaseException("Error clearing resources", e);
        }
    }
}

