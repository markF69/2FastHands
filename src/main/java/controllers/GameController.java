package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GameController{
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



    private Timeline timer;
    private int secondsLeft = 60;
    Random random = new Random();
    private ArrayList<Text> wordList = new ArrayList<>();

    @FXML
    private void initialize(){
        List<String> randomWords = new ArrayList<>();
        List<String> randomQuote = new ArrayList<>();
        try{
            randomWords.addAll(Files.readAllLines(Paths.get("src/main/resources/dictionary/english1k.txt"))); // addAll because of lambda
            randomQuote.addAll(Files.readAllLines(Paths.get("src/main/resources/dictionary/quotes.txt")));
            Collections.shuffle(randomWords);
            Collections.shuffle(randomQuote);
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Failed to load dictionary files");
        }

        Image img = new Image(getClass().getResourceAsStream("/UI/images/reset.png"));
        ImageView resetImg = new ImageView(img);
        resetImg.setFitWidth(85);
        resetImg.setFitHeight(45);
        resetBtn.setGraphic(resetImg);

        startTimer();
        populateBox(randomWords, randomQuote);
//        for (Text t : wordList){
//            System.out.println(t.getText());
//        }
        //resetBtn.setOnMouseClicked(e -> populateBox(randomWords, randomQuote));
    }

    // meant to populate the 2 hboxes - runs on startup
    // first part of the game
    private void populateBox(List<String> randomWords, List<String> randomQuote){
        System.out.println("POPULATE");
        // if elements are present in both
        if (!(hboxTop.getChildren().isEmpty() && hboxBottom.getChildren().isEmpty())){
            hboxTop.getChildren().clear();
            hboxBottom.getChildren().clear();
        }

        String quote = randomQuote.removeFirst();
        String[] quoteWords = quote.split(" ");
        double[] totalTextWidth = {0,0}; // top - bottom
        int[] counters = {0,0};

        while (true){
            Text word = new Text(randomWords.removeFirst());
            if (addWord(hboxTop, word, totalTextWidth, 0, counters, quoteWords)){
                counters[0]++;
            } else if (addWord(hboxBottom, word, totalTextWidth, 1, counters, quoteWords)) {
                counters[0]++;
            } else {
                break;
            }
        }
        //System.out.println(quote);
    }

    private boolean addWord(HBox hbox, Text word, double[] totalTextWidth, int index, int[] counters, String[] quoteWords){
        double wordWidth = word.getLayoutBounds().getWidth();
        if (totalTextWidth[index] + wordWidth < 388){
            if (counters[0] > 0 && counters[0] % 4 == 0 && counters[1] < quoteWords.length){
                Text txt = new Text(quoteWords[counters[1]]); //counterW
                if (totalTextWidth[index] + txt.getLayoutBounds().getWidth() < 388){
                    hbox.getChildren().add(txt);
                    wordList.add(txt);
                    totalTextWidth[index] += txt.getLayoutBounds().getWidth();
                    counters[1]++;
                }
            } else{
                hbox.getChildren().add(word);
                wordList.add(word);
                totalTextWidth[index] += wordWidth;
            }
            return true;
        }
        return false;
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
