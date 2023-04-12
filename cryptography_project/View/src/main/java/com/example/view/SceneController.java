package com.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

public class SceneController {

    @FXML
    private Button aesButton;
    @FXML
    private Button dsaButton;
    @FXML
    private Region chooseRegion;

    @FXML
    public void switchToAES() {
        StageSetup.buildStage("aes-stage.fxml");
    }

    @FXML
    public void switchToDSA() {
        StageSetup.buildStage("dsa-stage.fxml");
    }
}

