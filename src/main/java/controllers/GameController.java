package controllers;

import com.sun.tools.javac.Main;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Duration;


import java.time.LocalTime;
import java.util.List;


public class GameController{

    private Timeline timer;
    private int secondsLeft = 60;


    @FXML
    private Label timerLbl;
    @FXML
    private Button resetBtn;
    @FXML
    private HBox hboxTop;
    @FXML
    private HBox hboxBottom;
    @FXML
    private AnchorPane mainPane;

    @FXML
    private void initialize(){
        try{
            List<String> words = Files.readAllLines(Paths.get("src/main/resources/dictionary/english1k.txt"));
            //Collections.shuffle(words); // random order
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Failed to load english 1k");
        }

        Image img = new Image(getClass().getResourceAsStream("/UI/images/reset.png"));
        ImageView resetImg = new ImageView(img);
        resetImg.setFitWidth(85);
        resetImg.setFitHeight(45);
        resetBtn.setGraphic(resetImg);

        double topBoxWidth = hboxTop.getPrefWidth();
        System.out.println(topBoxWidth);
        double[] totalTextWidth = {0,0}; // top - bottom
        resetBtn.setOnMouseClicked(e -> {
            Text test = new Text("Click!");
            double textWidth = test.getLayoutBounds().getWidth();
            System.out.println("TW " + textWidth);
            if (totalTextWidth[0] + textWidth < 388){ // 388 because the width of the hbox isn't changing
                hboxTop.getChildren().add(test);
                totalTextWidth[0] += textWidth;
                System.out.println("TTTW " + totalTextWidth[0]);
            } else if (totalTextWidth[1] + textWidth < 388){
                hboxBottom.getChildren().add(test);
                totalTextWidth[1] += textWidth;
                System.out.println("BTTW " + totalTextWidth[1]);
            }
        });




    }

    // function meant to populate the 2 hboxes
    private void populateBox(){

    }

    // will be used when you start typing - currently bound to button for testing
    private void startTimer(){
        if (timer != null && timer.getStatus() == Animation.Status.RUNNING){
            timer.stop();
            System.out.println("MANUAL STOP");
        }

        // timer
        timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            timerLbl.setText(String.valueOf(secondsLeft));
            System.out.println(secondsLeft);

            if (secondsLeft == 0){
                //timerLbl.setText("0");
                timer.stop();
                System.out.printf("Timer stopped");
            }
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        secondsLeft = 60;
        timerLbl.setText("60");
        timer.playFromStart();
    }
}
