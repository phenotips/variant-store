package org.phenotips.variantstore.db;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.phenotips.variantstore.input.AbstractVariantIterator;

import org.ga4gh.GAVariant;

/**
 * Created by meatcar on 2/20/15.
 */
public abstract class AbstractDatabaseController {
    protected Path path;

    protected abstract Path getStoragePathSuffix();

    public abstract void stop();

    public void init(Path path) throws DatabaseException {
        this.path = path.resolve(this.getStoragePathSuffix());
    }

    public abstract Future addIndividual(AbstractVariantIterator iterator) throws DatabaseException;

    public abstract Future removeIndividual(String id) throws DatabaseException;

    public abstract Map<String, List<GAVariant>> getIndividualsWithGene(String gene,
                                                                        List<String> variantEffects,
                                                                        Map<String, Double> alleleFrequencies,
                                                                        int n);

    public abstract Map<String,List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt);

    public abstract List<GAVariant> getTopHarmfulWithGene(String id, int n, String gene, List<String> variantEffects, Map<String, Double> alleleFrequencies);

    public abstract List<GAVariant> getTopHarmfullVariants(String id, int n);
}
