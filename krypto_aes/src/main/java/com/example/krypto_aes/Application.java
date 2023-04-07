package com.example.krypto_aes;

import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        StageSetup.buildStage(stage, "main-stage.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}