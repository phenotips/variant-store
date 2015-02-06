package org.phenotips.variantstore.storage;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.ga4gh.GAVariant;
import org.phenotips.variantstore.VariantStoreException;
import org.phenotips.variantstore.models.Info;
import org.phenotips.variantstore.writers.InfoWriter;
import org.phenotips.variantstore.writers.VariantWriter;

/**
 * Created by meatcar on 1/19/15.
 */
public class ProcessSingleVCFTask implements Callable {
    Logger logger = Logger.getLogger(ProcessSingleVCFTask.class);
    private Path outDir;
    private final File vcfFile;
    private final VCFFileReader vcfReader;

    public ProcessSingleVCFTask(Path filePath, Path outDir) {
        vcfFile = filePath.toFile();
        vcfReader = new VCFFileReader(vcfFile, false);
        this.outDir = outDir;
    }

    @Override
    public Object call() throws Exception {
        VCFHeader vcfHeader = vcfReader.getFileHeader();
        String id = null;

        logger.debug("Processing: " + vcfFile.getAbsolutePath());

        if (vcfHeader.getSampleNamesInOrder().size() > 1) {
            throw new VariantStoreException("Multi-sample VCF unsupported");
        } else if (vcfHeader.getSampleNamesInOrder().size() == 1) {
            id = vcfHeader.getSampleNamesInOrder().get(0);
            //TODO: pass this to getGaVariant
        } else {
            //TODO: get patient name
        }

        Iterator<VariantContext> it = vcfReader.iterator();

        VariantWriter variantWriter = new VariantWriter(outDir.resolve(vcfFile.getName() + ".parquet"));
        InfoWriter infoWriter = new InfoWriter(outDir.resolve(vcfFile.getName() + ".info.parquet"));

        GAVariant gaVariant;
        Info typedInfo;

        VariantContext vcfRow = null;
        while (it.hasNext()) {
            try {
                vcfRow = it.next(); //.fullyDecode(vcfHeader, true);
            } catch (Exception e) {
                logger.error("Error encountered while processing " + vcfFile.getAbsolutePath(), e);
                continue;
            }

            /**
             * Parse VCF row to ga4gh schema + our own metadata schema
             * Write Parquet file
             */

            variantWriter.write(vcfRow);
            infoWriter.write(vcfRow);
        }

        variantWriter.close();
        infoWriter.close();

        logger.debug("Done processing: " + vcfFile.getAbsolutePath());

        return null;
    }
}
