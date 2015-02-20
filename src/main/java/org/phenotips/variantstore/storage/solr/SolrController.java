package org.phenotips.variantstore.storage.solr;

import java.nio.file.Path;
import java.util.concurrent.Future;
import org.phenotips.variantstore.input.VariantIterator;
import org.phenotips.variantstore.storage.StorageController;

/**
 * Created by meatcar on 2/20/15.
 */
public class SolrController extends StorageController {

    public SolrController(Path storePath) {
        super(storePath.resolve("/solr/"));
    }

    @Override
    public Future addIndividual(String id, VariantIterator iterator) {
        return null;
    }

    @Override
    public Future removeIndividual(String id) {
        return null;
    }
}
