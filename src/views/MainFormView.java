package views;

import data.StringConstants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFormView {

    private Stage primaryStage;

    public MainFormView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initializeForm() {
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource(StringConstants.MAIN_FORM_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (root == null)
            throw new NullPointerException("Root is null");

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(StringConstants.MAIN_FORM_TITLE);
        primaryStage.show();
        primaryStage.setMinWidth(725);
        primaryStage.setMinHeight(350);
    }
}
