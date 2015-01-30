package org.phenotips.variantstore.storage;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.apache.log4j.Logger;

/**
 * The StorageManager is responsible for managing the data files. It
 */
public class StorageManager {
    private Logger logger = Logger.getLogger(StorageManager.class);
    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Path outDir;

    public StorageManager(Path outDir) {
        this.outDir = outDir;
    }

    public Future add(Path filePath) {
        FutureTask<String> task = new FutureTask<String>(new ProcessSingleVCFTask(filePath, outDir));
        executor.execute(task);
        return task;
    }
}
