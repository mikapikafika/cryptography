package com.example.krypto_aes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.security.NoSuchAlgorithmException;

public class AesController {
    AESAlgorithm algorithm = new AESAlgorithm();
    KeyHandler keyHandler = new KeyHandler();
    @FXML
    public void onActionGenerateKey(ActionEvent actionEvent) throws NoSuchAlgorithmException {
        keyHandler.generateKey(128);  // na razia stala wartosc i tą akcje trzeba przypisac
                                                // do guzika, chyba, ze zrobimy jeszcze rozwijana liste wartosci 128, 192, 256

    }

}