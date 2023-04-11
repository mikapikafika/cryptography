package com.example.krypto_aes;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class PopUpWindow {
    public void showMessage(String message) {
//        Stage stage = new Stage();
//        VBox vbox = new VBox();
//        TextField textField = new TextField(message);
//        textField.setFont(Font.font(25));
//        vbox.getChildren().add(textField);
//        vbox.getChildren().add(alert);
//        Scene stageScene = new Scene(vbox, 500, 100);
//        stage.setScene(stageScene);
//        stage.show();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}