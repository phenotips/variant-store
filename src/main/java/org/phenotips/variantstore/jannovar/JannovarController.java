package org.phenotips.variantstore.jannovar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import org.phenotips.variantstore.shared.Service;
import org.phenotips.variantstore.shared.VariantStoreException;
import org.phenotips.variantstore.db.DatabaseException;

/**
 * Created by meatcar on 3/6/15.
 */
public class JannovarController implements Service {
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Path path;
    private Path dataDir;
    private Path vcfDir;

    public void init(Path path) throws VariantStoreException {
        this.path = path;
        this.dataDir = path.resolve("data");
        this.vcfDir = path.resolve("vcf");

        try {
            ensureDataExists().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new VariantStoreException("Error initializing Jannovar", e);
        }
    }

    public void stop() {

    }

    /**
     * Ensure the nescessary Jannovar data files are present. If not, download them them.
     * @throws DatabaseException
     */
    private Future ensureDataExists() throws VariantStoreException {
        if (Files.exists(this.path)) {
            return null;
        }

        try {
            Files.createDirectories(this.dataDir);
            Files.createDirectories(this.vcfDir);
        } catch (IOException e) {
            throw new VariantStoreException("Unable to create directory", e);
        }

        FutureTask task = new FutureTask<Object>(new FetchDataTask(this.dataDir, this.getDataFile()));
        executor.submit(task);
        return (Future) task;
    }

    public Path getDataFile() {
        return this.dataDir.resolve("hg19_ucsc.ser");
    }

    public Future<Path> annotate(Path vcf) throws VariantStoreException {
        FutureTask<Path> task = new FutureTask<Path>(new AnnotateTask(this.getDataFile(), this.path.resolve("vcf"), vcf));
        executor.submit(task);
        return task;
    }

}
