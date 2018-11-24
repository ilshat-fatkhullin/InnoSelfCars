package controllers;

import data.CommandResult;
import data.ConnectionResult;
import data.QueryResult;
import data.UpdateResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestController {

    private SqlController sqlController;

    private List<RequestControllerListener> listeners;

    public RequestController(RequestControllerListener listener) {
        listeners = new ArrayList<>();
        addListener(listener);

        CompletableFuture<ConnectionResult> commandFuture = CompletableFuture.supplyAsync(this::connectToDatabase);
        commandFuture.thenAccept(this::handleConnectionResult);
    }

    public void makeRequest(String request) {
        if (isCommandRequest(request)) {
            CompletableFuture<CommandResult> commandFuture = CompletableFuture.supplyAsync(() -> sqlController.executeCommand(request));
            commandFuture.thenAccept(this::handleCommandResult);
        } else if (isQueryRequest(request)) {
            CompletableFuture<QueryResult> queryFuture = CompletableFuture.supplyAsync(() -> sqlController.executeQuery(request));
            queryFuture.thenAccept(this::handleQueryResult);
        } else if (isUpdateRequest(request)) {
            CompletableFuture<UpdateResult> updateFuture = CompletableFuture.supplyAsync(() -> sqlController.executeUpdate(request));
            updateFuture.thenAccept(this::handleUpdateResult);
        }
        else {
            handleWrongRequest();
        }
    }

    public void addListener(RequestControllerListener listener) {
        listeners.add(listener);
    }

    private boolean isCommandRequest(String request) {
        return doesMatches(request, "(alter|drop|create)[a-zA-Z0-9_ *]*");
    }

    private boolean isQueryRequest(String request) {
        return doesMatches(request, "select[a-zA-Z0-9_ *]*");
    }

    private boolean isUpdateRequest(String request) {
        return doesMatches(request, "(insert|delete|update)[a-zA-Z0-9_ *]*");
    }

    private boolean doesMatches(String inputLine, String patternLine) {
        Pattern pattern = Pattern.compile(patternLine);
        Matcher matcher = pattern.matcher(inputLine.toLowerCase());
        return matcher.matches();
    }

    private void handleCommandResult(CommandResult result) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handleCommandResult(result);
        }
    }

    private void handleQueryResult(QueryResult result) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handleQueryResult(result);
        }
    }

    private void handleUpdateResult(UpdateResult result) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handleUpdateResult(result);
        }
    }

    private void handleConnectionResult(ConnectionResult result) {
        sqlController = result.getSqlController();
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handleConnectionResult(result);
        }
    }

    private void handleWrongRequest() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handleWrongRequest();
        }
    }

    private ConnectionResult connectToDatabase() {
        boolean isSuccessful;
        SqlController sqlController = null;
        try {
            sqlController = new SqlController();
            isSuccessful = true;
        } catch (ClassNotFoundException | SQLException e) {
            isSuccessful = false;
        }

        return new ConnectionResult(isSuccessful, sqlController);
    }
}

interface RequestControllerListener {

    void handleCommandResult(CommandResult result);

    void handleQueryResult(QueryResult result);

    void handleUpdateResult(UpdateResult result);

    void handleWrongRequest();

    void handleConnectionResult(ConnectionResult result);
}
