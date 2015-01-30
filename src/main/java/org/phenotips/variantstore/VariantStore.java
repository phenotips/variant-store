package org.phenotips.variantstore;

import java.io.File;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.phenotips.variantstore.storage.DrillManager;
import org.phenotips.variantstore.storage.InvalidFileFormatException;
import org.phenotips.variantstore.storage.StorageManager;

/**
 * The Variant Store enables the storage of many many variants.
 */
public class VariantStore {
    private Path outDir;
    private static Logger logger = Logger.getLogger(VariantStore.class);

    private DrillManager drillManager;
    private StorageManager storageManager;

    /**
     *
     * @param drillPath Drill's configuration string, same as you would pass to sqlline
     * @param outDir
     */
    public VariantStore(String drillPath, Path outDir) throws VariantStoreException {
        this.outDir = outDir;

        storageManager = new StorageManager(this.outDir);

        try {
            drillManager = new DrillManager(drillPath);
        } catch (Exception e) {
            throw new VariantStoreException(e.getMessage(), e);
        }
    }

    public VariantStore(Path vcfDir) throws VariantStoreException {
        this("jdbc:drill:zk=local", vcfDir);
    }

    public Connection connection() {
        return drillManager.connection();
    }

    public void stop() throws VariantStoreException {
        try {
            drillManager.stop();
        } catch (SQLException e) {
            throw new VariantStoreException(e.getMessage(), e);
        }
    }

    public Future addFile(Path filePath) throws InvalidFileFormatException {
        return storageManager.add(filePath);
    }

    /**
     * Add all the VCF files found in the given directory to the store.
     * @param dirPath
     * @return a List of Futures, each one
     */
    public List<Future> addFilesFromDirectory(Path dirPath) throws InvalidFileFormatException {
        File dir = new File(dirPath.toString());
        File[] directoryListing = dir.listFiles();
        List<Future> futures = new ArrayList<>();
        if (directoryListing != null) {
            for (File vcfFile : directoryListing) {

                logger.debug("Queueing " + dirPath.resolve(vcfFile.getName()));
                futures.add(this.addFile(dirPath.resolve(vcfFile.getName())));
            }
        } else {
            return null;
        }

        return futures;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /** DRILL JDBC headache **/
//        Class.forName("org.apache.drill.jdbc.Driver");
//        Connection connection = DriverManager.getConnection("jdbc:drill:zk=local", null);
//        String query = "select N_NAME from dfs.`/home/meatcar/dev/drill/apache-drill-0.7.0/sample-data/nation.parquet`";
//
//        Statement statement = connection.createStatement();
//
//        // hangs here
//        System.out.println("About to hang..");
//        ResultSet rs = statement.executeQuery(query);
//        System.out.println("Didn't hang!!!");
        /**/
    }
}
