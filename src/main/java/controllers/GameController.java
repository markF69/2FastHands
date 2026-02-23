package controllers;

import javafx.fxml.FXML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


public class GameController{

    @FXML
    private void initialize(){
        try{
            List<String> words = Files.readAllLines(Paths.get("dictionary/english1k.txt"));
            Collections.shuffle(words); // random order
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Failed to load english 1k");
        }


    }
}
