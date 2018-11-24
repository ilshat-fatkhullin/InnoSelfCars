package controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RequestController {

    public RequestController() {

    }

    public void makeRequest(String request) {
        CompletableFuture<Boolean> commandFuture = CompletableFuture.supplyAsync(() -> getRequest(request));
        commandFuture.thenAccept(this::handleCommandResult);
    }

    private Boolean getRequest(String request) {
        return true;
    }

    private void handleCommandResult(Boolean isSuccessful) {

    }
}

interface AListener {

    void handleCommandResult(Boolean isSuccessful);

    void handleQueryResult(Boolean isSuccessful, List data);

    void handleUpdateResult(Boolean isSuccessful, Integer count);
}
