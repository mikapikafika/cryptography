package com.example.krypto_aes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class SceneController {

    @FXML
    private Pane instructionBg;
    @FXML
    private Label instruction;
    @FXML
    private Button buttonAES;
    @FXML
    private Button buttonDES;
    @FXML
    public void switchToAES() {
        StageSetup.buildStage("aes-stage.fxml");
    }
}
