package com.example.krypto_aes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

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
    private byte[] result;
    private byte[] primaryKey;
    private int[] expandedKey = new int[4 * (10 + 1)];


    @FXML
    public void pressedBack() {
        StageSetup.buildStage("main-stage.fxml");
    }

    @FXML
    public void pressedGenerateKey() throws NoSuchAlgorithmException {
        primaryKey = keyHandler.generateKey(128);
        keyHandler.expandKey(primaryKey, 4, 4, 10, expandedKey);
        algorithm.setPrimaryKey(primaryKey);
        algorithm.setExpandedKey(expandedKey);
        String displayKey = bytesToHex(algorithm.getPrimaryKey());
        generateKeyField.setText(displayKey);
    }

    @FXML
    public void pressedReadKey() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String displayKey = new String(Files.readAllBytes(file.toPath()));
            readKeyField.setText(displayKey);
            primaryKey = hexToBytes(displayKey);
            keyHandler.expandKey(primaryKey, 4, 4, 10, expandedKey);
            algorithm.setPrimaryKey(primaryKey);
            algorithm.setExpandedKey(expandedKey);
        }
    }

    @FXML
    public void pressedSaveKey() throws IOException {
        // Two keys, either user can paste theirs or save the generated one
        String userKey = saveKeyField.getText();
        String generatedKey = generateKeyField.getText();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            if (!generatedKey.isEmpty()) {
                writer.write(generatedKey);
            } else {
                writer.write(userKey);
            }
            writer.close();
        }
    }

    // SZYFROWANIE - CHYBA DZIAŁA

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
        message = algorithm.encode(message);
//        result = algorithm.encrypt(message);
        result = Base64.getEncoder().encode(message);        // może tak??? coś to daje XD
        String displayMessage = new String(result);
        encodedArea.setText(displayMessage);
    }

    @FXML
    public void pressedSaveCryptogram() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            String stringResult = new String(result, StandardCharsets.US_ASCII);
            FileWriter writer = new FileWriter(file);
            writer.write(stringResult);
            writer.close();
        }
    }

    // DESZYFROWANIE - NIE DZIAŁA

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
        String messageString = toDecodeArea.getText();
        message = hexToBytes(messageString);
        // co powinno być pierwsze xd
//        message = algorithm.decrypt(message);
        result = algorithm.decode(message);
        result = Base64.getEncoder().encode(result);        // nie wiem czy takie coś tu też?
        String displayResult = new String(result);
        encodedArea.setText(displayResult);
    }

    // tego nie tykałam nawet
    @FXML
    public void pressedSaveData() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            writer.write(Arrays.toString(result));
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

    private static byte[] hexToBytes(String string) {
        int length = string.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4)
                    + Character.digit(string.charAt(i+1), 16));
        }
        return bytes;
    }

}