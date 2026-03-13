package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class IntroController {
    @FXML
    private Button startBtn;

    @FXML
    private void initialize(){
        startBtn.setOnMouseClicked(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/UI/GameUI.fxml"));
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex){
                ex.printStackTrace();
            }
        });
    }
}
