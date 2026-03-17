package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
    @FXML
    private TextField inputTF;



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


        int[] stats = {0,0}; // hit - miss
        int goal = wordList.size();
        inputTF.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.SPACE)){
                if (!wordList.isEmpty()){
                    String userText = inputTF.getText().trim(); // trim is used because of the "space" carryover
                    Text screenWord = wordList.getFirst();
                    String compareToText = screenWord.getText();
                    System.out.println("CURRENT:" + compareToText);
                    if (userText.equals(compareToText)){
                        System.out.println("GOOD!");
                        wordList.removeFirst();
                        screenWord.setFill(Color.GREEN);
                        stats[0]++;
                    }
                    else {
                        System.out.println("BAD!");
                        screenWord.setFill(Color.RED);
                        stats[1]++;
                    }
                    inputTF.clear();
                } else {
                    System.out.println("empty");
                    inputTF.clear();
                    System.out.println("Hit: " + (stats[0]-stats[1]) + "\nMiss: " + stats[1]);
                }
            }
        });

        if (stats[0] == goal){
            System.out.println("YAY!");
        }
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
        boolean topFull = false;

        while (true){
            Text word = new Text(randomWords.removeFirst());
            if (!topFull && addWord(hboxTop, word, totalTextWidth, 0, counters, quoteWords)){
                counters[0]++;
            }
            else if (addWord(hboxBottom, word, totalTextWidth, 1, counters, quoteWords)) {
                if (!topFull) topFull=true;
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

    // will be used when you start typing
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
            //System.out.println(secondsLeft);

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
