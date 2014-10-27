package org.phenotips;

import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import javax.inject.Singleton;
import org.apache.drill.jdbc.Driver;

/**
 * Created by meatcar on 10/27/14.
 */
@Singleton
public class DrillManager {
    private Connection connection = null;

    public DrillManager() throws SQLException {

        try {
            Class.forName("org.apache.drill.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Driver d = new Driver();
        // TODO: this creates a new instance of drill, and complains if one is running. Figure out how to connect to a running one.
        connection = d.connect("jdbc:drill:zk=local", new Properties());
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
