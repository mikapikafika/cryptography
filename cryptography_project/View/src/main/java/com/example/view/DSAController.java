package com.example.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.example.dsa.DSAAlgorithm;
import org.example.exceptions.GuiException;
import org.example.exceptions.KeyException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

public class DSAController implements Initializable {

    // Keys
    @FXML
    private ChoiceBox<String> keyChoice;
    private String[] keyOptions = {"(1024, 160)", "(2048, 224)", "(2048, 256)", "(3072, 256)"};
    @FXML
    private TextField qAndgField;
    @FXML
    private TextField publicKeyField;
    @FXML
    private TextField privateKeyField;
    @FXML
    private TextField modpField;

    // Signing / verifying
    @FXML
    private Button readToSignButton;
    @FXML
    private Button saveSignedButton;
    @FXML
    private Button readToVerifyButton;
    @FXML
    private Button saveVerifiedButton;

    // Variables
    private int L = 0;
    private int N = 0;
    private FileChooser fileChooser = new FileChooser();
    private byte[] message;
    private PopUpWindow popUpWindow = new PopUpWindow();
    private DSAAlgorithm algorithm = new DSAAlgorithm();

    @FXML
    public void pressedBack() {
        StageSetup.buildStage("main-stage.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyChoice.getItems().addAll(keyOptions);
        keyChoice.setOnAction(this::getKeyParameters);

        EventHandler<ActionEvent> saveHandler = actionEvent -> {
            try {
                pressedSave();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        saveSignedButton.setOnAction(saveHandler);
        saveVerifiedButton.setOnAction(saveHandler);

        EventHandler<ActionEvent> readHandler = actionEvent -> {
            try {
                pressedRead();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        readToSignButton.setOnAction(readHandler);
        readToVerifyButton.setOnAction(readHandler);
    }

    private void getKeyParameters(ActionEvent actionEvent) {
        switch (keyChoice.getValue()) {
            case "(1024, 160)" -> {
                this.L = 1024;
                this.N = 160;
            }
            case "(2048, 224)" -> {
                this.L = 2048;
                this.N = 224;
            }
            case "(2048, 256)" -> {
                this.L = 2048;
                this.N = 256;
            }
            case "(3072, 256)" -> {
                this.L = 3072;
                this.N = 256;
            }
        }
    }

    /**
     * Generates keys depending on the given L and N values and displays them in proper text fields.
     * @throws GuiException if key length is not specified (is zero)
     */
    @FXML
    public void pressedGenerateKeys() throws GuiException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (this.L == 0 || this.N == 0) {
            popUpWindow.showError("L and N values haven't been selected");
            throw new GuiException("L and N values haven't been selected");
        }

        algorithm.generateKey();

        qAndgField.setText("");
        publicKeyField.setText("");
        privateKeyField.setText("");
        modpField.setText("");
    }

    /**
     * Reads keys in hex from file and displays them in proper text fields.
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedReadKeys() throws IOException {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {


            qAndgField.setText("");
            publicKeyField.setText("");
            privateKeyField.setText("");
            modpField.setText("");
        }
        else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Saves generated or read keys in hex to a file
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedSaveKeys() throws IOException {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);


            writer.write(qAndgField.getText());
            writer.newLine();
            writer.write(publicKeyField.getText());
            writer.newLine();
            writer.write(privateKeyField.getText());
            writer.newLine();
            writer.write(modpField.getText());
            writer.newLine();
            writer.close();
        }
        else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }

    }



    /**
     * Reads chosen file
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedRead() throws IOException {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            message = Files.readAllBytes(file.toPath());
            popUpWindow.showInfo("File read successfully :)");
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Saves chosen file
     * @throws IOException if there's no file
     */
    public void pressedSave() throws IOException {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            OutputStream outputStream = new FileOutputStream(file.getPath());
            outputStream.write(message);
            outputStream.close();
            popUpWindow.showInfo("File saved successfully :)");
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }
}
