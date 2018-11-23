import java.sql.*;
import java.util.Properties;

public class SqlController {

    public static void main(String[] args) throws Exception {

        // Inititalize connection variables
        String host = "carims.postgres.database.azure.com";
        String database = "postgres";
        String user = "carimsadmin@carims";
        String password =  "zvWiZcGNcTn6hy54";

        // Check that the driver is installed
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("PostgreSQL JDBC driver NOT detected in library path.", e);
        }

        System.out.println("PostgreSQL JDBC driver detected in library path.");

        Connection connection = null;

        // Initialize connection object
        try {
            String url = String.format("jdbc:postgresql://%s/%s", host, database);

            // set up the connection properties
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
//            properties.setProperty("ssl", "true");

            // get connection
            connection = DriverManager.getConnection(url, properties);
        }
        catch (SQLException e) {
            throw new SQLException("Failed to create connection to database.", e);
        }
        if (connection != null) {
            System.out.println("Successfully created connection to database.");

            // Perform some SQL queries over the connection.
            try {
                // Drop previous table of same name if one exists.
                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE IF EXISTS inventory;");
                System.out.println("Finished dropping table (if existed).");

                // Create table
                statement.execute("CREATE TABLE inventory (id serial PRIMARY KEY, name VARCHAR(50), quantity INTEGER);");
                System.out.println("Created table");
            }
            catch (SQLException e) {
                throw new SQLException("Encountered an error when executing given sql statement.", e);
            }
        }
        else {
            System.out.println("Failed to create connection to database.");
        }
        System.out.println("Execution finished.");
    }
}