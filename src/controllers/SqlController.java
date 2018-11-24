package controllers;

import java.sql.*;
import java.util.LinkedList;
import java.util.Properties;

import data.StringConstants;
import data.CommandResult;
import data.UpdateResult;
import data.QueryResult;

public class SqlController {

    Connection connection = null;

    public SqlController() throws ClassNotFoundException, SQLException {
        // Check that the driver is installed
        Class.forName("org.postgresql.Driver");

        // Initialize connection object
        String url = String.format("jdbc:postgresql://%s/%s", StringConstants.HOST, StringConstants.DATABASE);

        // set up the connection properties
        Properties properties = new Properties();
        properties.setProperty("user", StringConstants.USER);
        properties.setProperty("password", StringConstants.PASSWORD);

        // get connection
        connection = DriverManager.getConnection(url, properties);

        if (connection == null) {
            throw new NullPointerException("Failed to create connection to database.");
        }
    }

    public CommandResult executeCommand(String query) throws SQLException {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(query);
            CommandResult result = new CommandResult(true);
            return result;
        } catch (SQLException e) {
            CommandResult result = new CommandResult(false);
            return result;
        }
    }


    public UpdateResult executeUpdate(String query) throws SQLException {
        Statement stmt = null;
        try {
            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int n = stmt.executeUpdate(query);
            UpdateResult result = new UpdateResult(true, n);
            return result;
        } catch (SQLException e) {
            UpdateResult result = new UpdateResult(false, -1);
            return result;
        }
    }

    public QueryResult executeQuery(String query) throws SQLException {
        Statement stmt = null;
        try {
            stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);
            Object[] queryArray = proceedResultSet(rs);
            rs.close();
            QueryResult result = new QueryResult(true, queryArray);
            return result;
        } catch (SQLException e) {
            QueryResult result = new QueryResult(false, null);
            return result;
        }
    }

    private Object[] proceedResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int ncolumns = rsmd.getColumnCount();
        LinkedList<Object[]> list = new LinkedList<Object[]>();
        Object[] tempObj = new Object[ncolumns];
        for (int i = 0; i < ncolumns; i++) {
            tempObj[i] = rsmd.getColumnName(i+1);
        }
        list.add(tempObj);
        while (rs.next()) {
            for (int i = 0; i < ncolumns; i++) {
                tempObj[i] = rs.getObject(i+1);
            }
            list.add(tempObj);
        }
        return list.toArray();
    }
}