package org.phenotips.variantstore.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.apache.drill.jdbc.Driver;
import org.apache.log4j.Logger;
import org.phenotips.variantstore.VariantStoreException;

/**
 * Created by meatcar on 10/27/14.
 */
@Singleton
public class DrillManager {
    private Logger logger = Logger.getLogger(DrillManager.class);
    private Connection connection = null;

    public DrillManager(String drillPath) throws VariantStoreException, IOException {

        try {
            Class.forName("org.apache.drill.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        initConfiguration();

        Driver d = new Driver();
        // TODO: this creates a new instance of drill, and complains if one is running. Figure out how to connect to a running one.
        try {
            connection = DriverManager.getConnection(drillPath);
        } catch (SQLException e) {
            throw new VariantStoreException("Unable to connect to Apache Drill at " + drillPath + "." +
                                            " If starting a single-node instance ensure one isn't already running," +
                                            " otherwise make sure the Drill cluster is available");
        }
    }

    public DrillManager() throws Exception {
        this("jdbc:drill:zk=local");
    }

    /**
     * Ensure sure that drill configuration is in `/tmp/init`, add it if it's not.
     */
    private void initConfiguration() throws IOException, VariantStoreException {
        final String configName = "phenotips.sys.drill";
        File configDir = new File("/tmp/drill/sys.storage_plugins"); //TODO: see if it varies per platform

        if (configDir .exists()) {
            if (configDir.isDirectory()) {
                logger.info(configDir.getAbsolutePath() + " already exists, using it now.");
                return;
            } else {
                throw new VariantStoreException("/tmp/drill is not a directory");
            }
        }

        logger.info(configDir.getAbsolutePath() + " doesn't exist, initializing it.");

        URL config = DrillManager.class.getResource("/" + configName);
        try {
            // strip initial / in configPath
            FileUtils.copyURLToFile(config, new File(configDir.getAbsolutePath(), configName));
        } catch (IOException e) {
            logger.error("Copying drill config failed.", e);
            throw e;
        }
    }

    public Connection connection() {
        return connection;
    }

    public void stop() throws SQLException {
        connection.close();
    }

    public static void main(String[] args) {
        try {
            DrillManager m = new DrillManager();
            Statement s = m.connection().createStatement();
            m.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
