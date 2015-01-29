package org.phenotips;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.junit.*;

import static org.junit.Assert.*;
import org.phenotips.variantstore.VariantStore;
import org.phenotips.variantstore.VariantStoreException;

/**
 * Created by meatcar on 1/23/15.
 */
public class VariantStoreTest {
    Logger logger = Logger.getLogger(VariantStoreTest.class);
    VariantStore store;

    @Before
    public void before() throws VariantStoreException {
        store = new VariantStore("/home/meatcar/dev/drill/vcf/",
                "/home/meatcar/dev/drill/parquet/"
        );
    }

    @After
    public void after() throws VariantStoreException {
        store.stop();
    }

    @Ignore
    @Test
    public void testLoadAllVCFs() throws VariantStoreException, SQLException, InterruptedException {
        String vcfDir = "/home/meatcar/dev/drill/vcf";
        File dir = new File(vcfDir);
        File[] directoryListing = dir.listFiles();
        List<Future> futures = new ArrayList<>();
        if (directoryListing != null) {
            for (File vcfFile : directoryListing) {

                logger.debug("Queueing " + vcfFile.getAbsolutePath());
                futures.add(store.addFile(vcfFile.getAbsolutePath()));
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
    }

    @Ignore
    @Test
    public void testSampleQuery() throws SQLException {
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
}
