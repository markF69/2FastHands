package controllers;

import com.sun.tools.javac.Main;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;

import java.beans.EventHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        resetBtn.setOnMouseClicked(e -> startTimer());


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
