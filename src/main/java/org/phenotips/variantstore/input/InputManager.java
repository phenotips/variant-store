package org.phenotips.variantstore.input;

import org.phenotips.variantstore.shared.Service;

import java.nio.file.Path;
import java.util.List;

/**
 * An input handler is responsible for orchestrating the transformation of variant data from one format to a
 * standard representation that then can be used by the rest of the Variant Store.
 *
 * @version $Id$
 */
public interface InputManager extends Service
{
    /**
     * Add an individual's data file for safekeeping.
     * @param id the id of the individual
     * @param path the path to the file
     * @throws InputException if an error is encountered.
     */
    void addIndividual(String id, Path path) throws InputException;

    /**
     * Get the path to the individual's data file.
     * @param id the id of the individual
     * @return a path to the file.
     */
    Path getIndividual(String id);

    /**
     * Remove an individual's file from the file store.
     * @param id the id of the individual
     * @throws InputException if an error is encountered.
     */
    void removeIndividual(String id) throws InputException;

    /**
     * Given an individual, get the VariantIterator.
     *
     * @param id the id of the individual
     * @param isPublic can the variants be used for aggregate data.
     * @return the variant iterator
     */
    VariantIterator getIteratorForIndividual(String id, boolean isPublic);

    /**
     * Get All individuals stored in the variant store.
     *
     * @return a list of individual IDs
     */
    List<String> getAllIndividuals();
}
