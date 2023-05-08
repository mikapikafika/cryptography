package com.example.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.example.dsa.DSAAlgorithm;
import org.example.exceptions.GuiException;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.ResourceBundle;

public class DSAController  {

    // Keys
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
    @FXML
    private Button signUsingDsa;

    @FXML
    private Button verifySignatureButton;


    // Variables
    private int L = 512;
    private int N = 160;
    private FileChooser fileChooser = new FileChooser();
    private byte[] message;
    private BigInteger[] signatureBigInt = new BigInteger[2];
    private PopUpWindow popUpWindow = new PopUpWindow();
    private DSAAlgorithm algorithm = new DSAAlgorithm();

    @FXML
    public void pressedBack() {
        StageSetup.buildStage("main-stage.fxml");
    }



    /**
     * Generates keys depending on the given L and N values and displays them in proper text fields.
     *
     * @throws GuiException if key length is not specified (is zero)
     */
    @FXML
    public void pressedGenerateKeys() throws GuiException, NoSuchAlgorithmException, InvalidKeySpecException {

        //TODO czemu q zawsze zaczyna sie od 00 xd ???
        algorithm.generateKey();
        qAndgField.setText(byteToHex(algorithm.getQ().toByteArray()) + " " + byteToHex(algorithm.getG().toByteArray()));
        publicKeyField.setText(byteToHex(algorithm.getY().toByteArray()));
        privateKeyField.setText(byteToHex(algorithm.getX().toByteArray()));
        modpField.setText(byteToHex(algorithm.getP().toByteArray()));
    }

    /**
     * Reads keys in hex from file and displays them in proper text fields.
     *
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedReadPublicKey() throws IOException {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            List<String> listOfStrings = Files.readAllLines(file.toPath());
            qAndgField.setText(listOfStrings.get(0));
            publicKeyField.setText(listOfStrings.get(1));
            modpField.setText(listOfStrings.get(2));
            String[] qAndg = listOfStrings.get(0).split(" ");
            algorithm.setQ(new BigInteger(hexToBytes(qAndg[0])));
            algorithm.setG(new BigInteger(hexToBytes(qAndg[1])));
            algorithm.setY(new BigInteger(hexToBytes(listOfStrings.get(1))));
            algorithm.setP(new BigInteger(hexToBytes(listOfStrings.get(2))));
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }
    @FXML
    public void pressedReadPrivateKey() throws IOException {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            List<String> listOfStrings = Files.readAllLines(file.toPath());
            privateKeyField.setText(listOfStrings.get(0));
            algorithm.setX(new BigInteger(hexToBytes(listOfStrings.get(0))));
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }

    /**
     * Saves generated or read keys in hex to a file
     *
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedSavePublicKey() throws IOException {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(qAndgField.getText());
            writer.newLine();
            writer.write(publicKeyField.getText());
            writer.newLine();
            writer.write(modpField.getText());
            writer.close();
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }

    }
    @FXML
    public void pressedSavePrivateKey() throws IOException {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(privateKeyField.getText());
            writer.close();
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }

    }


    /**
     * Reads chosen file
     *
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
     *
     * @throws IOException if there's no file
     */
    @FXML
    public void pressedSave() throws IOException {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileWriter fileWriter = new FileWriter(file.getPath());
            fileWriter.write(byteToHex(signatureBigInt[0].toByteArray()));
            fileWriter.write("\n");
            fileWriter.write(byteToHex(signatureBigInt[1].toByteArray()));
            fileWriter.close();
            popUpWindow.showInfo("File saved successfully :)");
        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }

    @FXML
    public void pressedSign() throws NoSuchAlgorithmException {
        // TODO dodac jakies wyskakujace okienka
        signatureBigInt = algorithm.generateSignature(this.message);
        popUpWindow.showInfo("File signed:)");
    }

    @FXML
    public void pressedReadToVerify() throws IOException {

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            List<String> listOfStrings = Files.readAllLines(file.toPath());
            signatureBigInt[0] = new BigInteger(hexToBytes(listOfStrings.get(0)));
            signatureBigInt[1] = new BigInteger(hexToBytes(listOfStrings.get(1)));

        } else {
            popUpWindow.showError("No file selected");
            throw new GuiException("No file selected");
        }
    }

    @FXML
    public void pressedVerify() throws NoSuchAlgorithmException {
        // TODO dodac jakies zabezpieczenia
        if (algorithm.verifySignature(this.message, this.signatureBigInt)) {
            popUpWindow.showInfo("The signature matches :)");
        } else {
            popUpWindow.showError("The signature does not match");
        }
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

    private String byteToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02X", b)); // convert byte to two-digit hexadecimal representation
        }
        return hex.toString();
    }
}

