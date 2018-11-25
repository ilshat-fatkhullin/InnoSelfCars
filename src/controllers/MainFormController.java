package controllers;

import data.CommandResult;
import data.ConnectionResult;
import data.QueryResult;
import data.UpdateResult;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;

public class MainFormController implements RequestControllerListener {

    @FXML
    private Button discardChangesButton;

    @FXML
    private Button reconnectButton;

    @FXML
    private Button runButton;

    @FXML
    private Button showButton;

    @FXML
    private Button clearButton;

    @FXML
    private ChoiceBox<String> methodChoiceBox;

    @FXML
    private ChoiceBox<String> tableChoiceBox;

    @FXML
    private TextArea terminalTextArea;

    @FXML
    private TextField commandTextField;

    @FXML
    private TableView resultTableView;

    private ObservableList<String> methodChoiceBoxList;

    private ObservableList<String> tableChoiceBoxList;

    private String terminalText;

    private Boolean isProceeding;

    private RequestController requestController;

    public MainFormController() {
        methodChoiceBoxList = FXCollections.observableArrayList();
        methodChoiceBoxList.addAll("3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "3.10");

        tableChoiceBoxList = FXCollections.observableArrayList();
        tableChoiceBoxList.addAll("Customers", "Charging stations", "Workshops", "Providers");
    }

    @FXML
    private void initialize() {
        methodChoiceBox.setItems(methodChoiceBoxList);
        methodChoiceBox.getSelectionModel().selectFirst();
        tableChoiceBox.setItems(tableChoiceBoxList);
        tableChoiceBox.getSelectionModel().selectFirst();

        terminalText = "";
        isProceeding = false;

        commandTextField.textProperty().addListener((obs, oldText, newText) -> {
            onCommandTextFieldTextChanged();
        });

        connect();
    }

    @FXML
    private void handleDiscardChangesButtonAction(ActionEvent event) {
        if (isProceeding)
            return;
        System.out.println("Clicked 'Recover' button.");
    }

    @FXML
    private void handleReconnectButtonAction(ActionEvent event) {
        connect();
    }

    @FXML
    private void handleRunButtonAction(ActionEvent event) {
        if (isProceeding)
            return;
    }

    @FXML
    private void handleShowButtonAction(ActionEvent event) {
        if (isProceeding)
            return;

        switch (tableChoiceBox.getValue()) {
            case "Customers":
                makeRequest("select * from customers");
                break;
            case "Charging stations":
                makeRequest("select * from chargingStations");
                break;
            case "Workshops":
                makeRequest("select * from workshops");
                break;
            case "Providers":
                makeRequest("select * from providers");
                break;
        }
    }

    @FXML
    private void onCommandLineKeyPressed(javafx.scene.input.KeyEvent event) {
        if (isProceeding)
            return;

        if (event.getCode() != KeyCode.ENTER)
            return;

        String command = commandTextField.getText();

        commandTextField.setText("");

        makeRequest(command);
    }

    @FXML
    private void handleClearButtonAction(ActionEvent event) {
        terminalTextArea.setText("");
        terminalText = "";
    }

    private void appendIntoTerminal(String line) {
        if (terminalText.length() != 0)
            terminalText += "\n";
        terminalText += line;
        terminalTextArea.setText(terminalText);
    }

    private void onCommandTextFieldTextChanged() {
        if (isProceeding)
            commandTextField.setText("");
    }

    public void handleCommandResult(CommandResult result) {
        isProceeding = false;

        if (result.isSuccessful()) {
            appendIntoTerminal("Command is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during command execution.");
        }
    }

    public void handleQueryResult(QueryResult result) {
        isProceeding = false;

        if (result.isSuccessful()) {
            appendIntoTerminal("Query is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during query execution.");
            return;
        }

        Platform.runLater(() -> updateResultTableView(result));
    }

    public void handleUpdateResult(UpdateResult result) {
        isProceeding = false;

        if (result.isSuccessful()) {
            appendIntoTerminal("Update is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during update execution.");
            return;
        }

        appendIntoTerminal("Number of changed entries: " + result.getCount());
    }

    public void handleConnectionResult(ConnectionResult result) {
        isProceeding = false;

        if (result.isSuccessful()) {
            appendIntoTerminal("Connection established.");
        } else {
            appendIntoTerminal("Connection failed.");
        }
    }

    public void handleWrongRequest() {
        isProceeding = false;
        appendIntoTerminal("Syntax error.");
    }

    private void updateResultTableView(QueryResult result) {
        ObservableList<ObservableList> observableRows = FXCollections.observableArrayList();

        resultTableView.getColumns().clear();

        for (int i = 0; i < result.getData().get(0).size(); i++) {
            final int j = i;
            TableColumn col = new TableColumn(result.getData().get(0).get(i));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });

            resultTableView.getColumns().addAll(col);
        }

        for (int i = 1; i < result.getData().size(); i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.addAll(result.getData().get(i));
            observableRows.add(row);
        }

        resultTableView.getItems().clear();
        resultTableView.setItems(observableRows);
    }

    private void connect() {
        appendIntoTerminal("Connecting to database...");
        isProceeding = true;

        requestController = new RequestController(this);
    }

    private void makeRequest(String request) {
        isProceeding = true;

        appendIntoTerminal(request);
        appendIntoTerminal("Proceeding...");

        requestController.makeRequest(request);
    }
}
