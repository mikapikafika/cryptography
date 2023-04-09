package com.example.krypto_aes;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class SceneController {

    @FXML
    private Pane instructionBg;

    @FXML
    public void switchToAES() {
        StageSetup.buildStage("aes-stage.fxml");
    }
}
