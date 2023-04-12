package com.example.view;

import javafx.scene.control.Alert;

public class PopUpWindow {
    public void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
