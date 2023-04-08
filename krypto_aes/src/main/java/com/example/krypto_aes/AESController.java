package com.example.krypto_aes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import static com.example.krypto_aes.KeyHandler.*;

public class AESController {

    // key
    @FXML
    private Pane keyPane;   // do .css
    @FXML
    private TextField generateKeyField;
    @FXML
    private TextField readKeyField;
    @FXML
    private TextField saveKeyField;

    // encode
    @FXML
    private AnchorPane encodePane;
    @FXML
    private TextArea toEncodeArea;
    @FXML
    private TextArea encodedArea;
    @FXML
    private TextArea toDecodeArea;
    @FXML
    private TextArea decodedArea;


    private AESAlgorithm algorithm = new AESAlgorithm();
    private KeyHandler keyHandler = new KeyHandler();
    private FileChooser fileChooser = new FileChooser();
    private byte[] message;


    @FXML
    public void pressedBack() {
        StageSetup.buildStage("main-stage.fxml");
    }

    @FXML
    public void pressedGenerateKey() throws NoSuchAlgorithmException {
        algorithm.setPrimaryKey(keyHandler.generateKey(128));
        String displayKey = bytesToHex(algorithm.getPrimaryKey());
        generateKeyField.setText(displayKey);
    }

    @FXML
    public void pressedReadKey() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String displayKey = Arrays.toString(Files.readAllBytes(file.toPath()));
            readKeyField.setText(displayKey);
            algorithm.setPrimaryKey(HexFormat.of().parseHex(displayKey));
        }
    }

    @FXML
    public void pressedSaveKey() throws IOException {
        String key = saveKeyField.getText();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            writer.write(key);
            writer.close();
        }
    }

    // SZYFROWANIE

    @FXML
    public void pressedReadData() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            message = Files.readAllBytes(file.toPath());
            String displayMessage = new String(message);
            toEncodeArea.setText(displayMessage);
        }
    }

    @FXML
    public void pressedEncode() {
        message = toEncodeArea.getText().getBytes();
        algorithm.encode(message);
        algorithm.encrypt(message);
    }

    @FXML
    public void pressedSaveCryptogram() throws IOException {
        String displayMessage = new String(message);
        encodedArea.setText(displayMessage);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            writer.write(displayMessage);
            writer.close();
        }
    }

    // DESZYFROWANIE

    @FXML
    public void pressedReadCryptogram() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            message = Files.readAllBytes(file.toPath());
            String displayMessage = new String(message);
            toDecodeArea.setText(displayMessage);
        }
    }

    @FXML
    public void pressedDecode() {
        message = toDecodeArea.getText().getBytes();
        algorithm.decode(message);
    }

    @FXML
    public void pressedSaveData() throws IOException {
        String displayMessage = new String(message);
        decodedArea.setText(displayMessage);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            writer.write(displayMessage);
            writer.close();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02X", b)); // convert byte to two-digit hexadecimal representation
        }
        return hex.toString();
    }

}