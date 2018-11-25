package controllers;

import data.*;
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

import java.util.Hashtable;

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

    private Hashtable<String, String> tableChoiceBoxItemsToTables;

    private ObservableList<String> methodChoiceBoxList;

    private ObservableList<String> tableChoiceBoxList;

    private String terminalText;

    private boolean isProceeding;

    private boolean isSequentiallyRequesting;

    private RequestController requestController;

    private SequentialRequestRunner sequentialRequestRunner;

    public MainFormController() {
        methodChoiceBoxList = FXCollections.observableArrayList();
        methodChoiceBoxList.addAll("3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "3.10");

        tableChoiceBoxItemsToTables = new Hashtable<>();
        tableChoiceBoxItemsToTables.put("Addresses", "Addresses");
        tableChoiceBoxItemsToTables.put("Car parts", "CarParts");
        tableChoiceBoxItemsToTables.put("Cars", "Cars");
        tableChoiceBoxItemsToTables.put("Charging history", "ChargingHistory");
        tableChoiceBoxItemsToTables.put("Charging stations", "ChargingStations");
        tableChoiceBoxItemsToTables.put("Charging station to plugs", "CSToPlugs");
        tableChoiceBoxItemsToTables.put("Customers", "Customers");
        tableChoiceBoxItemsToTables.put("Locations", "Locations");
        tableChoiceBoxItemsToTables.put("Parking history", "ParkingHistory");
        tableChoiceBoxItemsToTables.put("Parking places", "ParkingPlaces");
        tableChoiceBoxItemsToTables.put("Parking stations", "ParkingStations");
        tableChoiceBoxItemsToTables.put("Payment history", "PaymentHistory");
        tableChoiceBoxItemsToTables.put("Plugs", "Plugs");
        tableChoiceBoxItemsToTables.put("Providers", "Providers");
        tableChoiceBoxItemsToTables.put("Provides", "Provides");
        tableChoiceBoxItemsToTables.put("Providing history", "ProvidingHistory");
        tableChoiceBoxItemsToTables.put("Rent history", "RentHistory");
        tableChoiceBoxItemsToTables.put("Repairing history", "RepairingHistory");
        tableChoiceBoxItemsToTables.put("Workshops", "Workshops");
        tableChoiceBoxItemsToTables.put("Workshops have parts", "WorkshopsHaveParts");

        tableChoiceBoxList = FXCollections.observableArrayList();
        tableChoiceBoxList.addAll(tableChoiceBoxItemsToTables.keySet());
    }

    @FXML
    private void initialize() {
        methodChoiceBox.setItems(methodChoiceBoxList);
        methodChoiceBox.getSelectionModel().selectFirst();
        tableChoiceBox.setItems(tableChoiceBoxList);
        tableChoiceBox.getSelectionModel().selectFirst();

        terminalText = "";
        isProceeding = false;
        isSequentiallyRequesting = false;

        commandTextField.textProperty().addListener((obs, oldText, newText) -> {
            onCommandTextFieldTextChanged();
        });

        connect();
    }

    @FXML
    private void handleDiscardChangesButtonAction(ActionEvent event) {
        if (isProceeding)
            return;

        sequentialRequestRunner = new SequentialRequestRunner(StringConstants.DISCARD_CHANGES_REQUEST_PATH);

        isSequentiallyRequesting = true;
        makeSequentialRequest();
    }

    @FXML
    private void handleReconnectButtonAction(ActionEvent event) {
        connect();
    }

    @FXML
    private void handleRunButtonAction(ActionEvent event) {
        if (isProceeding)
            return;

        sequentialRequestRunner = new SequentialRequestRunner(StringConstants.SQL_REQUESTS_FOLDER_PATH +
                methodChoiceBox.getValue() + ".txt");

        isSequentiallyRequesting = true;
        makeSequentialRequest();
    }

    @FXML
    private void handleShowButtonAction(ActionEvent event) {
        if (isProceeding)
            return;

        makeRequest("select * from " + tableChoiceBoxItemsToTables.get(tableChoiceBox.getValue()));
    }

    @FXML
    private void handleClearButtonAction(ActionEvent event) {
        terminalTextArea.setText("");
        terminalText = "";
    }

    @FXML
    private void onCommandLineKeyPressed(javafx.scene.input.KeyEvent event) {
        if (isProceeding)
            return;

        if (event.getCode() != KeyCode.ENTER)
            return;

        String command = commandTextField.getText();
        commandTextField.setText("");
        appendIntoTerminal(command);

        if (isSequentiallyRequesting) {
            sequentialRequestRunner.setInput(command);
            makeSequentialRequest();
        } else {
            makeRequest(command);
        }
    }

    private void onCommandTextFieldTextChanged() {
        if (isProceeding)
            commandTextField.setText("");
    }

    public void handleResult(Result result) {
        isProceeding = false;

        if (!isSequentiallyRequesting)
            return;

        if (!result.isSuccessful()) {
            appendIntoTerminal("An error occurred during sequential requesting.");
        } else if (sequentialRequestRunner.isFinished()) {
            appendIntoTerminal("Sequential requesting finished successfully.");
        }

        if (!result.isSuccessful() || sequentialRequestRunner.isFinished()) {
            isSequentiallyRequesting = false;
            return;
        }

        makeSequentialRequest();
    }

    public void handleCommandResult(CommandResult result) {
        if (result.isSuccessful()) {
            appendIntoTerminal("Command is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during command execution.");
        }
    }

    public void handleQueryResult(QueryResult result) {
        if (result.isSuccessful()) {
            appendIntoTerminal("Query is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during query execution.");
            return;
        }

        if (isSequentiallyRequesting) {
            StringBuilder line = new StringBuilder();
            line.append('\n');

            int[] columnSizes = new int[result.getData().get(0).size()];
            for (int i = 0; i < result.getData().size(); i++) {
                for (int j = 0; j < result.getData().get(i).size(); j++) {
                    int length = result.getData().get(i).get(j).length();
                    if (length > columnSizes[j])
                        columnSizes[j] = length;
                }
            }

            for (int i = 0; i < result.getData().size(); i++) {
                for (int j = 0; j < result.getData().get(i).size(); j++) {
                    StringBuilder part = new StringBuilder();
                    part.append(result.getData().get(i).get(j));
                    while (part.toString().length() < columnSizes[j])
                        part.append(' ');
                    line.append(part.toString()).append('|');
                }
                line.append('\n');
            }
            appendIntoTerminal(line.toString());
            return;
        }

        Platform.runLater(() -> updateResultTableView(result));
    }

    public void handleUpdateResult(UpdateResult result) {
        if (result.isSuccessful()) {
            appendIntoTerminal("Update is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during update execution.");
            return;
        }

        appendIntoTerminal("Number of changed entries: " + result.getCount());
    }

    public void handleConnectionResult(ConnectionResult result) {
        if (result.isSuccessful()) {
            appendIntoTerminal("Connection established.");
        } else {
            appendIntoTerminal("Connection failed.");
        }
    }

    public void handleWrongRequest() {
        appendIntoTerminal("Syntax error.");
    }

    private void appendIntoTerminal(String line) {
        Platform.runLater(() -> {
            if (terminalText.length() != 0)
                terminalText += "\n";
            terminalText += line;
            terminalTextArea.setText(terminalText);
            terminalTextArea.setScrollTop(Double.MAX_VALUE);
        });
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
        isSequentiallyRequesting = false;

        requestController = new RequestController(this);
    }

    private void makeRequest(String request) {
        isProceeding = true;
        appendIntoTerminal("Proceeding...");
        requestController.makeRequest(request);
    }

    private void makeSequentialRequest() {
        if (sequentialRequestRunner.isWaitingForInput()) {
            appendIntoTerminal(sequentialRequestRunner.getInputDescription());
            return;
        }

        String currentRequest = sequentialRequestRunner.nextRequest();
        requestController.makeRequest(currentRequest);
    }
}
