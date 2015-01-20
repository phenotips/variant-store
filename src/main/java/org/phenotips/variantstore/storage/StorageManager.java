package org.phenotips.variantstore.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.apache.log4j.Logger;
import org.phenotips.variantstore.ProcessSingleVCFTask;

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
        FutureTask<String> task = new FutureTask<String>(new ProcessSingleVCFTask(fileName, outDir));
        executor.execute(task);
        return task;
    }

    public void addAllInDirectory(String vcfDir) throws InterruptedException {
        File dir = new File(vcfDir);
        File[] directoryListing = dir.listFiles();
        List<Future> futures = new ArrayList<>();
        if (directoryListing != null) {
            for (File vcfFile : directoryListing) {

                logger.debug("Queueing " + vcfFile.getAbsolutePath());
                futures.add(this.add(vcfFile.getAbsolutePath()));
            }
        } else {
            logger.error("Directory " + vcfDir + "is empty!");
            return;
        }

        // Wait for all the tasks to finish to make this method sync.
        // If a task finished before another, `get()` will return immideately
        for (Future f : futures) {
            if (!f.isCancelled()) try {
                f.get();
            } catch (InterruptedException e) {
                // shouldn't happen..
                throw e;
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        logger.debug("Done processing all VCF files in " + vcfDir);
    }

}
