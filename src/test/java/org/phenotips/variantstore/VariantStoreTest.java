package org.phenotips.variantstore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.phenotips.variantstore.storage.InvalidFileFormatException;

import static org.junit.Assert.*;

public class VariantStoreTest {
    Logger logger = Logger.getLogger(VariantStoreTest.class);
    VariantStore store;


    @Before
    public void setUp() throws Exception {
        store = new VariantStore(Paths.get("/home/meatcar/dev/drill/parquet/"));
    }

    @After
    public void tearDown() throws Exception {
        store.stop();
    }

    @Ignore("unfinished")
    @Test
    public void testConnection() throws Exception {
        logger.debug("starting..");
        Connection connection = store.connection();
        PreparedStatement query = connection.prepareStatement(
                "SELECT flatten(tbl.info.`value`.`array`) AS EXOMISER_GENE_PHENO_SCORE " +
                        "FROM ( " +
                        "SELECT flatten(tbl.info.map) AS info " +
                        "FROM phenotips.root.`P0000084.ezr2.parquet` AS tbl " +
                        ") AS tbl " +
                        "WHERE tbl.info.key='EXOMISER_GENE_PHENO_SCORE'"
        );
        ResultSet rs = query.executeQuery();
        logger.warn(rs);
        rs.close();
        connection.close();
    }

    @Ignore("unfinished")
    @Test
    public void testStop() throws Exception {

    }

    @Ignore("unfinished")
    @Test
    public void testAddFile() throws Exception {

    }

    @Ignore("unfinished")
    @Test
    public void testAddFilesFromDirectory() throws InvalidFileFormatException, InterruptedException {
        Path vcfDir = Paths.get("/home/meatcar/dev/drill/vcf");

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
