package com.example.krypto_aes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESController {

    @FXML
    private Pane keyPane;
    @FXML
    private Label keyLabel;
    @FXML
    private TextField generateKeyField;
    @FXML
    private Button generateKeyButton;
    @FXML
    private Label readKeyLabel;
    @FXML
    private TextField readKeyField;
    @FXML
    private Button readKeyButton;




    AESAlgorithm algorithm = new AESAlgorithm();
    KeyHandler keyHandler = new KeyHandler();

    @FXML
    public void onActionGenerateKey(ActionEvent actionEvent) throws NoSuchAlgorithmException {
        algorithm.primaryKey = keyHandler.generateKey(128);  // na razia stala wartosc i tą akcje trzeba przypisac
                                                // do guzika, chyba, ze zrobimy jeszcze rozwijana liste wartosci 128, 192, 256
        String displayKey = Base64.getEncoder().encodeToString(algorithm.primaryKey);
        generateKeyField.setText(displayKey);
    }

    @FXML
    public void onActionReadKey(ActionEvent actionEvent) {

    }

    @FXML
    public void onActionSaveKey(ActionEvent actionEvent) {

    }

}