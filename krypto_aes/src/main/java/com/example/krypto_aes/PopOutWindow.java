package com.example.krypto_aes;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PopOutWindow {
    public void showMassage(String message) {
        Stage stage = new Stage();
        VBox vbox = new VBox();
        TextField textField = new TextField(message);
        textField.setFont(Font.font(25));
        vbox.getChildren().add(textField);
        Scene stageScene = new Scene(vbox, 500, 100);
        stage.setScene(stageScene);
        stage.show();
    }
}