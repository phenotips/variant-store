package org.phenotips.variantstore.jannovar;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.datasource.DataSourceFactory;
import de.charite.compbio.jannovar.datasource.FileDownloadException;
import de.charite.compbio.jannovar.datasource.InvalidDataSourceException;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.JannovarDataSerializer;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.SerializationException;
import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * Created by meatcar on 2/24/15.
 */
public class FetchDataTask implements Callable<Object> {

    private Path dataDir;
    private Path dataFile;

    public FetchDataTask(Path dataDir, Path dataFile) {
        this.dataDir = dataDir;
        this.dataFile = dataFile;
    }

    @Override
    public Object call() throws Exception {
        JannovarOptions options = new JannovarOptions();
        ImmutableList.Builder<String> dsfBuilder = new ImmutableList.Builder<String>();
        dsfBuilder.add("bundle:///default_sources.ini");
        options.dataSourceFiles = dsfBuilder.build();
        try {
            DataSourceFactory factory = new DataSourceFactory(options, options.dataSourceFiles);

            JannovarData data = factory.getDataSource("hg19/ucsc").getDataFactory()
                    .build(this.dataDir.toString(), false);

            JannovarDataSerializer serializer = new JannovarDataSerializer(this.dataFile.toString());

            serializer.save(data);
        } catch (InvalidDataSourceException | TranscriptParseException | SerializationException | FileDownloadException e) {
            throw new VariantStoreException("Error fetching Jannovar data", e);
        }
        return null;
    }
}
