package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

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

    private ObservableList<String> methodChoiceBoxList;

    private ObservableList<String> tableChoiceBoxList;

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
    }

    @FXML
    private void handleRecoverButtonAction(ActionEvent event) {
        System.out.println("Clicked 'Recover' button.");
    }

    @FXML
    private void handleRunButtonAction(ActionEvent event) {
        System.out.println("Clicked 'Run' button.");
    }

    @FXML
    private void handleShowButtonAction(ActionEvent event) {
        System.out.println("Clicked 'Show' button.");
    }

    @FXML
    private void onCommandLineKeyPressed(javafx.scene.input.KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER)
            return;
        System.out.println("Pressed 'Enter' on command line.");
    }
}
