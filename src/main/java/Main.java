import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {
    public static void main(String[] args) {
        // English 1k
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("UI/GameUI.fxml"));
        stage.setTitle("2FastHands");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
