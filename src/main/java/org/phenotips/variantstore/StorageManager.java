package org.phenotips.variantstore;

import java.io.File;
import java.util.concurrent.*;
import org.apache.log4j.Logger;

/**
 * The StorageManager is responsible for managing the data files. It
 */
public class StorageManager {
    private Logger logger = Logger.getLogger(StorageManager.class);
    private Executor executor = Executors.newSingleThreadExecutor();

    private String outDir;

    public StorageManager(String outDir) {
        this.outDir = outDir;
    }

    public Future add(String fileName) {
        FutureTask<String> task = new FutureTask<String>(new ProcessVCFTask(fileName, outDir));
        executor.execute(task);
        return task;
    }

    public void addAllInDirectory(String vcfDir) throws InterruptedException {
        File dir = new File(vcfDir);
        File[] directoryListing = dir.listFiles();
        Future future;
        if (directoryListing != null) {
            for (File vcfFile : directoryListing) {
                if (vcfFile.getName().endsWith("ASM.vcf")) {
                    logger.info("Processing: " + vcfFile.getAbsolutePath());
                    future = this.add(vcfFile.getAbsolutePath() );

                    if (!future.isCancelled()) try {
                        future.get();
                    } catch (InterruptedException e) {
                        // shouldn't happen..
                        throw e;
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    logger.info("Done processing: " + vcfFile.getAbsolutePath());
                }
            }
        } else {
            logger.error("Directory " + vcfDir + "is empty!");
            return;
        }
    }

}
