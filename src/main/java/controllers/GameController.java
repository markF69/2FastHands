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
    //double topBoxWidth = hboxTop.getPrefWidth();

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


        //System.out.println(topBoxWidth);

//        resetBtn.setOnMouseClicked(e -> {
//            Text test = new Text("Click!");
//            double textWidth = test.getLayoutBounds().getWidth();
//            System.out.println("TW " + textWidth);
//            if (totalTextWidth[0] + textWidth < 388){ // 388 because the width of the hbox isn't changing
//                hboxTop.getChildren().add(test);
//                totalTextWidth[0] += textWidth;
//                System.out.println("TTTW " + totalTextWidth[0]);
//            } else if (totalTextWidth[1] + textWidth < 388){
//                hboxBottom.getChildren().add(test);
//                totalTextWidth[1] += textWidth;
//                System.out.println("BTTW " + totalTextWidth[1]);
//            }
//        });

        resetBtn.setOnMouseClicked(e -> populateBox(randomWords, randomQuote));




    }

    // meant to populate the 2 hboxes - runs on startup
    private void populateBox(List<String> randomWords, List<String> randomQuote){
        System.out.println("POPULATE");
        // if elements are present in both
        if (!(hboxTop.getChildren().isEmpty() && hboxBottom.getChildren().isEmpty())){
            hboxTop.getChildren().clear();
            hboxBottom.getChildren().clear();
        }

        //int quoteRandParts = random.nextInt(2, 6); // gets 3-5 words from a quote that will be "sprinkled" along with other words
        String quote = randomQuote.removeFirst();
        String[] quoteWords = quote.split(" ");
        double[] totalTextWidth = {0,0}; // top - bottom
        //int[] counters = {0,0};


        int counter=0;
        int counterW=0;
        while (true){
            Text word = new Text(randomWords.removeFirst());
            double wordWidth = word.getLayoutBounds().getWidth();
            // if there is space in the top box add the element
            if (totalTextWidth[0] + wordWidth < 388){ // 388 is the edge
                if (counter > 0 && counter % 4 == 0){ // every 4th word will be a part of the quote
                    Text txt = new Text(quoteWords[counterW++]);
                    if (totalTextWidth[0] + txt.getLayoutBounds().getWidth() < 388){
                        hboxTop.getChildren().add(txt);
                        totalTextWidth[0] += txt.getLayoutBounds().getWidth();
                    }
                } else{
                    hboxTop.getChildren().add(word);
                    totalTextWidth[0] += wordWidth;
                }
                counter++;
            } else if (totalTextWidth[1] + wordWidth < 388){
                if (counter % 4 == 0){ // every 4th word will be a part of the quote
                    Text txt = new Text(quoteWords[counterW++]);
                    if (totalTextWidth[1] + txt.getLayoutBounds().getWidth() < 388){
                        hboxBottom.getChildren().add(txt);
                        totalTextWidth[1] += txt.getLayoutBounds().getWidth();
                    }
                } else{
                    hboxBottom.getChildren().add(word);
                    totalTextWidth[1] += wordWidth;
                }

                counter++;
            } else{
                break; // if both are full
            }
        }


        System.out.println(quote);

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
