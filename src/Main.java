import controllers.MainFormController;
import javafx.application.Application;
import javafx.stage.Stage;
import views.MainFormView;

public class Main extends Application {

    private MainFormView mainFormView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainFormView = new MainFormView(primaryStage);
        mainFormView.initializeForm();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
