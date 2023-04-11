package com.example.krypto_aes;

import com.example.krypto_aes.exceptions.GuiException;
import com.example.krypto_aes.exceptions.KeyException;
import com.example.krypto_aes.exceptions.MessageException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

public class AESController implements Initializable {

    // key
    @FXML
    private Pane keyPane;   // do .css
    @FXML
    private TextField generateKeyField;
    @FXML
    private TextField readKeyField;
    @FXML
    private TextField saveKeyField;
    @FXML
    private ChoiceBox<Integer> keyChoice;
    private Integer[] keyOptions = {128, 192, 256};

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
    private int keyLength = 0;
    private int[] expandedKey;
    PopOutWindow popOutWindow = new PopOutWindow();


    @FXML
    public void pressedBack() {
        StageSetup.buildStage("main-stage.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyChoice.getItems().addAll(keyOptions);
        keyChoice.setOnAction(this::getKeyLength);
    }

    private void getKeyLength(ActionEvent actionEvent) {
        keyLength = keyChoice.getValue();
    }

    /**
     * Generates a primary key depending on the given length and displays it in proper text field
     * if key length is not specified (is zero) throws GuiException
     * primary key is stored in primaryKey variable
     * @throws NoSuchAlgorithmException
     * @throws GuiException
     */
    @FXML
    public void pressedGenerateKey() throws NoSuchAlgorithmException, GuiException {
        if (this.keyLength == 0) {
            popOutWindow.showMessage("The key length has not been selected");
            throw new GuiException("The key length has not been selected");
        }
        primaryKey = keyHandler.generateKey(keyLength);
        algorithm.setPrimaryKey(primaryKey);
        expandedKey = new int[algorithm.getNb() * (algorithm.getNr() + 1)];
        keyHandler.expandKey(primaryKey, algorithm.getNk(), algorithm.getNb(), algorithm.getNr(), expandedKey);
        algorithm.setExpandedKey(expandedKey);
        String displayKey = bytesToHex(algorithm.getPrimaryKey());
        generateKeyField.setText(displayKey);
    }

    /**
     * Reads primary key in hex from file, then transforms it to bytes and stores in primaryKey
     * and displays it in proper text field
     * if key size is wrong throws GuiException
     * @throws IOException
     */
    @FXML
    public void pressedReadKey() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String displayKey = new String(Files.readAllBytes(file.toPath()));
            primaryKey = hexToBytes(displayKey);
            try{
                algorithm.setPrimaryKey(primaryKey);
            } catch (KeyException e) {
                popOutWindow.showMessage("Wrong key size");
                throw new GuiException("Wrong key size");
            }
            readKeyField.setText(displayKey);
            expandedKey = new int[algorithm.getNb() * (algorithm.getNr() + 1)];
            keyHandler.expandKey(primaryKey, algorithm.getNk(), algorithm.getNb(), algorithm.getNr(), expandedKey);
            algorithm.setExpandedKey(expandedKey);
        }
        else {
            popOutWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
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
        else {
            popOutWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
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
        } else {
            popOutWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
        }
    }

    @FXML
    public void pressedEncode() throws GuiException {
        if (primaryKey == null) {
            popOutWindow.showMessage("Key is null");
            throw new GuiException("Key is null");
        }
        message = toEncodeArea.getText().getBytes();
        try {
            message = algorithm.encode(message);
        } catch (MessageException e) {
            popOutWindow.showMessage("Message can't be empty");
            throw new GuiException(e);
        }
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
        } else {
            popOutWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
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
        } else {
            popOutWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
        }
    }

    @FXML
    public void pressedDecode() throws GuiException {
        if (primaryKey == null) {
            popOutWindow.showMessage("Key is null");
            throw new GuiException("Key is null");
        }
        String messageString = toDecodeArea.getText();
        message = messageString.getBytes();
        result = Base64.getDecoder().decode(message);
        result = algorithm.decode(result);
        String displayResult = new String(result);
        decodedArea.setText(displayResult);
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
                    + Character.digit(string.charAt(i + 1), 16));
        }
        return bytes;
    }

}