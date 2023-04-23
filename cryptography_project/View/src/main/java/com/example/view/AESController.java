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
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
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
    private Button saveCryptogramButton;
    @FXML
    private TextArea toDecodeArea;
    @FXML
    private Button saveDataButton;


    private AESAlgorithm algorithm = new AESAlgorithm();
    private KeyHandler keyHandler = new KeyHandler();
    private FileChooser fileChooser = new FileChooser();
    private byte[] message;
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
            popUpWindow.showError("The key length has not been selected");
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
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String displayKey = new String(Files.readAllBytes(file.toPath()));
            primaryKey = hexToBytes(displayKey);
            try{
                algorithm.setPrimaryKey(primaryKey);
            } catch (KeyException e) {
                popUpWindow.showError("Wrong key size");
                throw new GuiException("Wrong key size");
            }
            readKeyField.setText(displayKey);
            expandedKey = new int[algorithm.getNb() * (algorithm.getNr() + 1)];
            keyHandler.expandKey(primaryKey, algorithm.getNk(), algorithm.getNb(), algorithm.getNr(), expandedKey);
            algorithm.setExpandedKey(expandedKey);
        }
        else {
            popUpWindow.showError("No file selected");
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
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }

    }

    // ENCODING

    /**
     * Reads data to encrypt from the chosen file
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedReadFile() throws IOException {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            message = Files.readAllBytes(file.toPath());
            toEncodeArea.setText(file.getAbsolutePath());
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Encodes & encrypts the given data
     * @throws GuiException if key is null or message is empty
     */
    @FXML
    public void pressedEncode() throws GuiException {
        if (primaryKey == null) {
            popUpWindow.showError("Key is null");
            throw new GuiException("Key is null");
        }
        try {
            message = algorithm.encode(message);
            popUpWindow.showInfo("File encoded successfully :)");
        } catch (MessageException e) {
            popUpWindow.showError("File can't be empty");
            throw new GuiException(e);
        }
    }

    /**
     * Saves either data or encoded data
     * @throws IOException
     */
    public void pressedSave() throws IOException {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            OutputStream outputStream = new FileOutputStream(file.getPath());
            outputStream.write(message);
            outputStream.close();
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }


    // DECODING

    /**
     * Reads encoded data to decrypt from the chosen file
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedReadDecodedFile() throws IOException {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            message = Files.readAllBytes(file.toPath());
            toDecodeArea.setText(file.getAbsolutePath());
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Decodes & decrypts the given data
     * @throws GuiException
     */
    @FXML
    public void pressedDecode() throws GuiException {
        if (primaryKey == null) {
            popUpWindow.showError("Key is null");
            throw new GuiException("Key is null");
        }
        message = algorithm.decode(message);
        popUpWindow.showInfo("File decoded successfully :)");
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
