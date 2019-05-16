import javafx.application.Application;
import javafx.stage.Stage;

public class Cannon extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My First JavaFX App");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}