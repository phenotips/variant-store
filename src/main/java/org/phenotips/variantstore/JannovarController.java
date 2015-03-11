package org.phenotips.variantstore;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.annotation.AnnotationException;
import de.charite.compbio.jannovar.cmd.annotate_vcf.AnnotatedVCFWriter;
import de.charite.compbio.jannovar.datasource.DataSourceFactory;
import de.charite.compbio.jannovar.datasource.FileDownloadException;
import de.charite.compbio.jannovar.datasource.InvalidDataSourceException;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.JannovarDataSerializer;
import de.charite.compbio.jannovar.io.SerializationException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.phenotips.variantstore.db.DatabaseException;

/**
 * Created by meatcar on 3/6/15.
 */
public class JannovarController {
    private Path path;
    private Path dataDir;
    private Path vcfDir;

    public void init(Path path) throws DatabaseException {
        this.path = path;
        this.dataDir = path.resolve("data");
        this.vcfDir = path.resolve("vcf");
        ensureDataExists();
    }

    public void stop() {

    }

    /**
     * Ensure the nescessary Jannovar data files are present. If not, download them them.
     * @throws DatabaseException
     */
    private void ensureDataExists() throws DatabaseException {
        if (Files.exists(this.path)) {
            return;
        }

        try {
            Files.createDirectories(this.dataDir);
            Files.createDirectories(this.vcfDir);
        } catch (IOException e) {
            throw new DatabaseException("Unable to create directory", e);
        }

        JannovarOptions options = new JannovarOptions();
        ImmutableList.Builder<String> dsfBuilder = new ImmutableList.Builder<String>();
        dsfBuilder.add("bundle:///default_sources.ini");
        options.dataSourceFiles = dsfBuilder.build();
        try {
            DataSourceFactory factory = new DataSourceFactory(options, options.dataSourceFiles);

            JannovarData data = factory.getDataSource("hg19/ucsc").getDataFactory()
                        .build(this.dataDir.toString(), false);

            JannovarDataSerializer serializer = new JannovarDataSerializer(this.getDataFile().toString());

            serializer.save(data);
        } catch (InvalidDataSourceException e) {
            e.printStackTrace();
        } catch (TranscriptParseException e) {
            e.printStackTrace();
        } catch (FileDownloadException e) {
            e.printStackTrace();
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    public Path getDataFile() {
        return this.dataDir.resolve("hg19_ucsc.ser");
    }

    public void annotate(Path vcf) {
        VCFFileReader reader = new VCFFileReader(vcf.toFile(), false);
        try {
            JannovarData data = new JannovarDataSerializer(this.getDataFile().toString()).load();

            JannovarOptions options = new JannovarOptions();
            options.writeJannovarInfoFields = true;
            options.writeVCFAnnotationStandardInfoFields = true;
            options.showAll = true;
            options.outVCFFolder = this.path.resolve("vcf").toString();

            AnnotatedVCFWriter writer = new AnnotatedVCFWriter(data.refDict, reader, data.chromosomes, vcf.getFileName().toString(), options);

            for (VariantContext vc : reader) {
                writer.put(vc);
            }

            // close parser writer again
            reader.close();
            writer.close();
        } catch (SerializationException e) {
            e.printStackTrace();
        } catch (AnnotationException e) {
            e.printStackTrace();
        }
    }

}
