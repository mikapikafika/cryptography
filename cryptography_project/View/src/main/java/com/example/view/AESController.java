package com.example.view;


import org.example.AESAlgorithm;
import org.example.KeyHandler;
import org.example.exceptions.MessageException;
import org.example.exceptions.KeyException;
import org.example.exceptions.GuiException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.Base64;
import java.util.ResourceBundle;

public class AESController implements Initializable {

    // key
    @FXML
    private Pane keyPane;   // .css
    @FXML
    private TextField generateKeyField;
    @FXML
    private TextField readKeyField;
    @FXML
    private TextField saveKeyField;
    @FXML
    private ChoiceBox<Integer> keyChoice;
    private Integer[] keyOptions = {128, 192, 256};

    // encode & decode
    @FXML
    private AnchorPane encodePane; // .css
    @FXML
    private TextArea toEncodeArea;
    @FXML
    private TextArea encodedArea;
    @FXML
    private Button saveCryptogramButton;
    @FXML
    private TextArea toDecodeArea;
    @FXML
    private TextArea decodedArea;
    @FXML
    private Button saveDataButton;


    private AESAlgorithm algorithm = new AESAlgorithm();
    private KeyHandler keyHandler = new KeyHandler();
    private FileChooser fileChooser = new FileChooser();
    private byte[] message;
    private byte[] result;
    private byte[] primaryKey;
    private int keyLength = 0;
    private int[] expandedKey;
    private PopUpWindow popUpWindow = new PopUpWindow();


    @FXML
    public void pressedBack() {
        StageSetup.buildStage("main-stage.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyChoice.getItems().addAll(keyOptions);
        keyChoice.setOnAction(this::getKeyLength);

        EventHandler<ActionEvent> saveHandler = actionEvent -> {
            try {
                pressedSave();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        saveDataButton.setOnAction(saveHandler);
        saveCryptogramButton.setOnAction(saveHandler);
    }

    private void getKeyLength(ActionEvent actionEvent) {
        keyLength = keyChoice.getValue();
    }

    /**
     * Generates a primary key depending on the given length and displays it in proper text field.
     * Primary key is stored in primaryKey variable
     * @throws NoSuchAlgorithmException if there's no AES algorithm
     * @throws GuiException if key length is not specified (is zero)
     */
    @FXML
    public void pressedGenerateKey() throws NoSuchAlgorithmException, GuiException {
        if (this.keyLength == 0) {
            popUpWindow.showMessage("The key length has not been selected");
            throw new GuiException("The key length has not been selected");
        }
        primaryKey = keyHandler.generateKey(keyLength);
        algorithm.setPrimaryKey(primaryKey);
        expandedKey = new int[algorithm.getNb() * (algorithm.getNr() + 1)];
        keyHandler.expandKey(primaryKey, algorithm.getNk(), algorithm.getNb(), algorithm.getNr(), expandedKey);
        algorithm.setExpandedKey(expandedKey);
        // displays text
        String displayKey = bytesToHex(algorithm.getPrimaryKey());
        generateKeyField.setText(displayKey);
    }

    /**
     * Reads primary key in hex from file, then transforms it to bytes and stores in primaryKey
     * and displays it in proper text field.
     * If key size is wrong throws GuiException
     * @throws IOException if there's no file
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
                popUpWindow.showMessage("Wrong key size");
                throw new GuiException("Wrong key size");
            }
            readKeyField.setText(displayKey);
            expandedKey = new int[algorithm.getNb() * (algorithm.getNr() + 1)];
            keyHandler.expandKey(primaryKey, algorithm.getNk(), algorithm.getNb(), algorithm.getNr(), expandedKey);
            algorithm.setExpandedKey(expandedKey);
        }
        else {
            popUpWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Saves generated or read key in hex to a file
     * @throws IOException if there's no file
     */
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
            popUpWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
        }

    }

    // ENCODING

    /**
     * Reads data to encrypt from the chosen file
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedReadData() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            message = Files.readAllBytes(file.toPath());
            String displayMessage = new String(message);
            toEncodeArea.setText(displayMessage);
        } else {
            popUpWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Encodes & encrypts the given data, then displays it in Base64
     * @throws GuiException if key is null or message is empty
     */
    @FXML
    public void pressedEncode() throws GuiException {
        if (primaryKey == null) {
            popUpWindow.showMessage("Key is null");
            throw new GuiException("Key is null");
        }
        message = toEncodeArea.getText().getBytes();
        try {
            message = algorithm.encode(message);
        } catch (MessageException e) {
            popUpWindow.showMessage("Message can't be empty");
            throw new GuiException(e);
        }
        result = Base64.getEncoder().encode(message);
        String displayMessage = new String(result);
        encodedArea.setText(displayMessage);
    }

    /**
     * Saves either data or cryptogram
     * @throws IOException
     */
    public void pressedSave() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            String stringResult = new String(result, StandardCharsets.US_ASCII);
            FileWriter writer = new FileWriter(file);
            writer.write(stringResult);
            writer.close();
        } else {
            popUpWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
        }
    }


    // DECODING

    /**
     * Reads cryptogram to encrypt from the chosen file
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedReadCryptogram() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            message = Files.readAllBytes(file.toPath());
            String displayMessage = new String(message);
            toDecodeArea.setText(displayMessage);
        } else {
            popUpWindow.showMessage("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Decodes & decrypts the given data, then displays it in Base64
     * @throws GuiException
     */
    @FXML
    public void pressedDecode() throws GuiException {
        if (primaryKey == null) {
            popUpWindow.showMessage("Key is null");
            throw new GuiException("Key is null");
        }
        String messageString = toDecodeArea.getText();
        if (messageString.length() == 0) {
            popUpWindow.showMessage("Message can't be empty");
            throw new GuiException("Message can't be empty");
        }
        message = messageString.getBytes();
        result = Base64.getDecoder().decode(message);
        result = algorithm.decode(result);
        String displayResult = new String(result);
        decodedArea.setText(displayResult);
    }


    // Helper methods

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02X", b)); // convert byte to two-digit hexadecimal representation
        }
        return hex.toString();
    }

    private byte[] hexToBytes(String string) {
        int length = string.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4)
                    + Character.digit(string.charAt(i + 1), 16));
        }
        return bytes;
    }

}
