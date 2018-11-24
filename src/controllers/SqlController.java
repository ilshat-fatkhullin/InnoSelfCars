package controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import data.StringConstants;
import data.CommandResult;
import data.UpdateResult;
import data.QueryResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public CommandResult executeCommand(String query) {
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


    public UpdateResult executeUpdate(String query) {
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

    public QueryResult executeQuery(String query) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);
            ArrayList<ArrayList<String>> queryArray = proceedResultSet(rs);
            rs.close();
            QueryResult result = new QueryResult(true, queryArray);
            return result;
        } catch (SQLException e) {
            QueryResult result = new QueryResult(false, null);
            return result;
        }
    }

    private ArrayList<ArrayList<String>> proceedResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int numberOfColumns = resultSetMetaData.getColumnCount();

        ArrayList<ArrayList<String>> resultList = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();

        for (int i = 0; i < numberOfColumns; i++) {
            row.add(resultSetMetaData.getColumnName(i + 1));
        }

        resultList.add(row);
        while (rs.next()) {
            row = new ArrayList<>();
            for (int i = 0; i < numberOfColumns; i++) {
                row.add(rs.getString(i + 1));
            }
            resultList.add(row);
        }
        return resultList;
    }
}