package org.phenotips.variantstore.jannovar;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.cmd.annotate_vcf.AnnotatedVCFWriter;
import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.JannovarDataSerializer;
import de.charite.compbio.jannovar.io.SerializationException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import net.sf.picard.annotation.AnnotationException;
import org.phenotips.variantstore.shared.VariantStoreException;

/**
 * Created by meatcar on 2/24/15.
 */
public class AnnotateTask implements Callable<Path> {

    private final Path vcf;
    private final Path outFolder;
    private Path dataFile;

    public AnnotateTask(Path dataFile, Path outFolder, Path vcf) {
        this.dataFile = dataFile;
        this.outFolder = outFolder;
        this.vcf = vcf;
    }

    @Override
    public Path call() throws Exception {
        VCFFileReader reader = new VCFFileReader(vcf.toFile(), false);

        JannovarData data;
        try {
            data = new JannovarDataSerializer(this.dataFile.toString()).load();
        } catch (SerializationException e) {
            throw new VariantStoreException("Error loading Jannovar data", e);
        }

        Path outFile = null;
        AnnotatedVCFWriter writer = null;
        try {
            JannovarOptions options = new JannovarOptions();
            options.writeJannovarInfoFields = true;
            options.writeVCFAnnotationStandardInfoFields = true;
            options.showAll = true;
            options.outVCFFolder = outFolder.toString();

            writer = new AnnotatedVCFWriter(data.refDict, reader, data.chromosomes, vcf.getFileName().toString(), options, ImmutableList.<String>of(null));

            for (VariantContext vc : reader) {
                if (!Thread.interrupted()) {
                    Thread.yield();
                    writer.put(vc);
                }
            }

            outFile = Paths.get(writer.getOutFileName());

            // close parser writer again
        } catch (AnnotationException e) {
            throw new VariantStoreException("Error annotating VCF with Jannovar", e);
        } finally {
            reader.close();
            if (writer != null) {
                writer.close();
            }
        }
        return outFile;
    }
}
