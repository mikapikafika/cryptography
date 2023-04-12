package com.example.view;

import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        StageSetup.buildStage(stage, "main-stage.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}