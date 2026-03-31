package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
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

import javax.sound.SoundClip;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class GameController{
    @FXML
    private Label timerLbl;
    @FXML
    private HBox hboxTop;
    @FXML
    private HBox hboxBottom;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField inputTF;
    @FXML
    private ImageView xImg;
    @FXML
    private Label levelLbl;

    Random random = new Random();

    private Timeline timer;
    private int secondsLeft = 500;
    private ArrayList<Text> wordList = new ArrayList<>();
    private String currQuote;
    private int currLevel = 1;

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

        Image img = new Image(getClass().getResourceAsStream("/UI/images/X.png"));
        ImageView resetImg = new ImageView(img);
        resetImg.setFitWidth(85);
        resetImg.setFitHeight(45);
        xImg.setImage(img);
        xImg.setVisible(false);


        firstGame(randomWords, randomQuote);
    }

    // meant to populate the 2 hboxes - runs on startup
    // populateBox for the first part
    private void populateBox(List<String> randomWords, List<String> randomQuote){
        //System.out.println("POPULATE");
        // if elements are present in both
        if (!(hboxTop.getChildren().isEmpty() && hboxBottom.getChildren().isEmpty())){
            hboxTop.getChildren().clear();
            hboxBottom.getChildren().clear();
        }

        currQuote = randomQuote.getFirst();
        String quote = randomQuote.removeFirst();
        String[] quoteWords = quote.split(" ");
        double[] totalTextWidth = {0,0}; // top - bottom
        int[] counters = {0,0}; // word counter and quote counter
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
    // populateBox for the second part (only quote)
    private void populateBox(List<String> shuffledQuote){
        if (!(hboxTop.getChildren().isEmpty() && hboxBottom.getChildren().isEmpty())){
            hboxTop.getChildren().clear();
            hboxBottom.getChildren().clear();
        }

        double[] totalTextWidth = {0,0}; // top - bottom

        while(!shuffledQuote.isEmpty()){
            Text word = new Text(shuffledQuote.removeFirst());
            // doesn't fit top -> doesn't fit bottom -> break
            if (!addWord(hboxTop, word, totalTextWidth, 0)){ // can probably work without topfull because
                if (!addWord(hboxBottom, word, totalTextWidth, 1)) {
                    System.out.println("------ WORD TOO LONG ------");
                    break;
                }
            }
        }

    }
    // addWord for the first part
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
    // addword for the second part
    private boolean addWord(HBox hbox, Text word, double[] totalTextWidth, int index){
        double wordWidth = word.getLayoutBounds().getWidth();
        if (totalTextWidth[index] + wordWidth < 388){
            hbox.getChildren().add(word);
            totalTextWidth[index] += wordWidth;
            return true;
        }
        return false; // can't fit
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
                System.out.println("Timer stopped");
            }
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        //secondsLeft = 60;
        timerLbl.setText(String.valueOf(secondsLeft));
        timer.playFromStart();
    }

    private void firstGame(List<String> randomWords, List<String> randomQuote){
        startTimer();
        populateBox(randomWords, randomQuote);
        //System.out.println(randomWords.size() + " " + randomQuote.size());
        levelLbl.setText("Level: " + currLevel);
        currLevel++;


        int[] stats = {0,0}; // hit - miss
        int goal = wordList.size();
        inputTF.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.SPACE)){
                if (!wordList.isEmpty()){
                    String userText = inputTF.getText().trim(); // trim is used because of the "space" carryover
                    Text screenWord = wordList.getFirst();
                    String compareToText = screenWord.getText();
                    //System.out.println("CURRENT:" + compareToText);
                    if (userText.equals(compareToText)){
                        //System.out.println("GOOD!");
                        wordList.removeFirst();
                        screenWord.setFill(Color.GREEN);
                        stats[0]++;
                    }
                    else {
                        //System.out.println("BAD!");
                        screenWord.setFill(Color.RED);
                        stats[1]++;
                    }
                    inputTF.clear();
                }

                // when the last word is typed correctly
                if (wordList.isEmpty()){
                    //inputTF.clear();
                    System.out.println("Hit: " + (stats[0]-stats[1]) + "\nMiss: " + stats[1]);
                    secondGame(randomWords,randomQuote);
                }
            }
        });
    }
    // second game has parameters so that it can pass them on to the first game again
    private void secondGame(List<String> randomWords, List<String> randomQuote){
        inputTF.setOnKeyPressed(null); // removes the first handler
        System.out.println("==== SECOND PART ====");
        System.out.println(currQuote);
        List<String> shuffledQuote = new ArrayList<>();
        shuffledQuote.addAll(Arrays.asList(currQuote.split(" ")));
        Collections.shuffle(shuffledQuote);
        populateBox(shuffledQuote);

        inputTF.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)){
                StringBuilder userText = new StringBuilder(inputTF.getText()); // stringbuilder because of in place remove
                if (userText.charAt(0) == ' '){
                    //System.out.println("EMPTY SPACE DETECTED");
                    userText.deleteCharAt(0);
                }
                //System.out.println(userText);
                if (userText.toString().equals(currQuote)){
                    inputTF.setOnKeyPressed(null);
                    inputTF.clear();
                    firstGame(randomWords,randomQuote);
                }
                else{
                    xImg.setVisible(true);
                    PauseTransition showX = new PauseTransition(Duration.seconds(1.5));
                    showX.setOnFinished(eX -> xImg.setVisible(false));
                    showX.play();

                    SoundClip wrongBuzzer;
                    try {
                        // regular buzzer is extremely loud
                        wrongBuzzer = SoundClip.createSoundClip(new File("src/main/resources/UI/sound/buzzer(-70%).wav"));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    wrongBuzzer.play();
                }
            }
        });
    }
}