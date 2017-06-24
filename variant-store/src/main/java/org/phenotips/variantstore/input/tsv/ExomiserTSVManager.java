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
package org.phenotips.variantstore.input.tsv;

import org.phenotips.variantstore.input.InputException;
import org.phenotips.variantstore.input.InputManager;
import org.phenotips.variantstore.input.VariantHeader;
import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.shared.VariantStoreException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Id$
 */
public class ExomiserTSVManager implements InputManager
{
    private static String suffix = ".variants.tsv";
    private static Logger logger = LoggerFactory.getLogger(ExomiserTSVManager.class);
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
        return this.path.resolve(id + suffix);
    }

    @Override
    public void removeIndividual(String id) throws InputException {
        try {
            Files.delete(this.getIndividual(id));
        } catch (NoSuchFileException x) {
            logger.warn("No TSV file found for patient record with the id: {}", id);
        } catch (IOException e) {
            logger.error("Error removing TSV", e);
        }
    }

    /**
     * Given an individual, get the {@link VariantIterator}.
     *
     * @param id       individual id
     * @param isPublic permission to use variants in aggregate results
     *
     * @return a variant iterator
     */
    @Override
    public VariantIterator getIteratorForIndividual(String id, boolean isPublic) {
        return new ExomiserTSVIterator(this.getIndividual(id), new VariantHeader(id, isPublic));
    }

    /**
     * Given an individual, get the Variant Iterator. The individual is assumed to be private.
     *
     * @param id the id of the individual
     *
     * @return the variant iterator
     */
    @Override
    public VariantIterator getIteratorForIndividual(String id) {
        return getIteratorForIndividual(id, false);
    }

    @Override
    public String getTSVTimeStamp(String id) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            BasicFileAttributes attr = Files.readAttributes(this.getIndividual(id), BasicFileAttributes.class);
            return sdfDate.format(attr.creationTime().toMillis());
        } catch (NoSuchFileException x) {
            logger.warn("No TSF file found for patient record with the id: {}", id);
        } catch (IOException e) {
            logger.error("Error getting time for TSF file.", e);
        }
        return null;
    }
}
