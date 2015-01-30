package org.phenotips.variantstore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by meatcar on 1/30/15.
 */
public class VariantStoreBenchmarkTest {
    Logger logger = Logger.getLogger(VariantStoreBenchmarkTest.class);
    VariantStore store;
    Path benchmarkDir = Paths.get("/home/meatcar/dev/drill/benchmark/");

    @Before
    public void before() throws VariantStoreException {
        store = new VariantStore(benchmarkDir.resolve("parquet/"));
    }

    @After
    public void after() throws VariantStoreException {
        store.stop();
    }

    @Test
    public void testParseBzippedVCFsToParquet() throws InterruptedException {
        Path vcfDir = benchmarkDir.resolve("vcf/");

        List<Future> futures = store.addFilesFromDirectory(vcfDir);

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
    }
}
