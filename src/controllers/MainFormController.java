package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.util.List;

public class MainFormController {

    @FXML
    private Button recoverButton;

    @FXML
    private Button runButton;

    @FXML
    private Button showButton;

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
        methodChoiceBoxList.addAll("Select", "Insert", "Delete", "Drop");

        tableChoiceBoxList = FXCollections.observableArrayList();
        tableChoiceBoxList.addAll("First table", "Second table", "Third table");
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

        requestController = new RequestController();
    }

    @FXML
    private void handleRecoverButtonAction(ActionEvent event) {
        if (isProceeding)
            return;
        System.out.println("Clicked 'Recover' button.");
    }

    @FXML
    private void handleRunButtonAction(ActionEvent event) {
        if (isProceeding)
            return;
        System.out.println("Clicked 'Run' button.");
    }

    @FXML
    private void handleShowButtonAction(ActionEvent event) {
        if (isProceeding)
            return;
        System.out.println("Clicked 'Show' button.");
    }

    @FXML
    private void onCommandLineKeyPressed(javafx.scene.input.KeyEvent event) {
        if (isProceeding)
            return;

        if (event.getCode() != KeyCode.ENTER)
            return;

        appendIntoTerminal("Proceeding...");

        String command = commandTextField.getText();

        terminalTextArea.setText(terminalText);
        commandTextField.setText("");

        isProceeding = true;

        requestController.makeRequest(command);
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

    private void handleCommandResult(Boolean isSuccessful) {
        isProceeding = false;

        if (isSuccessful) {
            appendIntoTerminal("Command is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during command execution.");
        }
    }

    private void handleQueryResult(Boolean isSuccessful, List data) {
        isProceeding = false;

        if (isSuccessful) {
            appendIntoTerminal("Query is successfully done.");
        } else {
            appendIntoTerminal("An error occurred during query execution.");
            return;
        }

        ObservableList observableData = FXCollections.observableList(data);
        resultTableView.setItems(observableData);
    }
}
