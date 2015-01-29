package org.phenotips.variantstore;

import java.sql.*;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.phenotips.variantstore.storage.DrillManager;
import org.phenotips.variantstore.storage.StorageManager;

/**
 * The Variant Store enables the storage of many many variants.
 */
public class VariantStore {
    private String vcfDir;
    private String outDir;
    private static Logger logger = Logger.getLogger(VariantStore.class);

    private DrillManager drillManager;
    private StorageManager storageManager;

    /**
     *
     * @param drillPath Drill's configuration string, same as you would pass to sqlline
     */
    public VariantStore(String drillPath, String vcfDir, String outDir) throws VariantStoreException {
        this.vcfDir = vcfDir;
        this.outDir = outDir;

        storageManager = new StorageManager(this.outDir);

        try {
            drillManager = new DrillManager(drillPath);
        } catch (Exception e) {
            throw new VariantStoreException(e.getMessage(), e);
        }
    }

    public VariantStore(String vcfDir, String outDir) throws VariantStoreException {
        this("jdbc:drill:zk=local", vcfDir, outDir);
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

    public Future addFile(String filePath) {
        return storageManager.add(filePath);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("org.apache.drill.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:drill:zk=local", null);
        String query = "select N_NAME from dfs.`/home/meatcar/dev/drill/apache-drill-0.7.0/sample-data/nation.parquet`";

        Statement statement = connection.createStatement();

        // hangs here
        System.out.println("About to hang..");
        ResultSet rs = statement.executeQuery(query);
        System.out.println("Didn't hang!!!");
    }
}
