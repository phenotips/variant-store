package org.phenotips.variantstore;

import de.charite.compbio.exomiser.cli.Main;
import de.charite.compbio.exomiser.core.Exomiser;
import de.charite.compbio.exomiser.core.ExomiserSettings;
import de.charite.compbio.exomiser.core.factories.SampleDataFactory;
import de.charite.compbio.exomiser.core.model.SampleData;
import java.nio.file.Path;
import org.phenotips.variantstore.shared.Service;
import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * Created by meatcar on 3/20/15.
 */
public class ExomiserController implements Service{
    private Exomiser exomiser;
    private SampleDataFactory sampleDataFactory;

    @Override
    public void init(Path path) throws VariantStoreException {
        // get Spring Context
        // fetch Exomiser and SampleDataFactory components.
    }

    @Override
    public void stop() {

    }

    public void annotate(Path vcf) {
        SampleData sampleData = sampleDataFactory.createSampleData(vcf, null);

    }
}
