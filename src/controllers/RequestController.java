package controllers;

import data.*;

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
        return doesMatches(request, "(alter|drop|create).*");
    }

    private boolean isQueryRequest(String request) {
        return doesMatches(request, "select.*");
    }

    private boolean isUpdateRequest(String request) {
        return doesMatches(request, "(insert|delete|update).*");
    }

    private boolean doesMatches(String inputLine, String patternLine) {
        Pattern pattern = Pattern.compile(patternLine);
        Matcher matcher = pattern.matcher(inputLine.toLowerCase());
        return matcher.matches();
    }

    private void handleCommandResult(CommandResult result) {
        for (RequestControllerListener listener : listeners) {
            listener.handleCommandResult(result);
            listener.handleResult(result);
        }
    }

    private void handleQueryResult(QueryResult result) {
        for (RequestControllerListener listener : listeners) {
            listener.handleQueryResult(result);
            listener.handleResult(result);
        }
    }

    private void handleUpdateResult(UpdateResult result) {
        for (RequestControllerListener listener : listeners) {
            listener.handleUpdateResult(result);
            listener.handleResult(result);
        }
    }

    private void handleConnectionResult(ConnectionResult result) {
        sqlController = result.getSqlController();
        for (RequestControllerListener listener : listeners) {
            listener.handleConnectionResult(result);
            listener.handleResult(result);
        }
    }

    private void handleWrongRequest() {
        for (RequestControllerListener listener : listeners) {
            listener.handleWrongRequest();
            listener.handleResult(new Result(false));
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

    void handleResult(Result result);

    void handleCommandResult(CommandResult result);

    void handleQueryResult(QueryResult result);

    void handleUpdateResult(UpdateResult result);

    void handleWrongRequest();

    void handleConnectionResult(ConnectionResult result);
}
