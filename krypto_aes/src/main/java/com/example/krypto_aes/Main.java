package com.example.krypto_aes;

import com.example.krypto_aes.exceptions.MessageException;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main {

    /**
     * Menu :)
     */
    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("What you want to do?");
            System.out.println("[1] - Encode a file");
            System.out.println("[2] - Test GUI");
            System.out.println("[3] - ??");
            System.out.println("[4] - Test the key :)");
            System.out.print("Enter a number: ");

            int num = scanner.nextInt();
            System.out.println("\n\n\n");

            switch (num) {
                case 1 -> {
                    String string = "rowerrowerrowerr";
                    byte[] blok = string.getBytes();
                    System.out.println("default");
                    print1DArray(blok);
                    KeyHandler keyHandler = new KeyHandler();
                    AESAlgorithm algorithm = new AESAlgorithm();
                    try {
                        byte[] primaryKey = keyHandler.generateKey(128);
                        int[] expandedKey = new int[4 * (10 + 1)]; //tak z dupy to na razie jest xddd
                        keyHandler.expandKey(primaryKey, 4, 4, 10, expandedKey);
                        algorithm.setPrimaryKey(primaryKey);
                        algorithm.setExpandedKey(expandedKey);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }

                    byte[] afterEncrypting = algorithm.encrypt(blok);
                    System.out.println("after encryption");
                    print1DArray(afterEncrypting);
                    byte[] afterDecrypting = algorithm.decrypt(afterEncrypting);
                    System.out.println("after decryption");
                    print1DArray(afterDecrypting);


                    byte[] afterEncode = new byte[0];
                    try {
                        afterEncode = algorithm.encode(blok);
                    } catch (MessageException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("after encoding");
                    print1DArray(afterEncode);

                    byte[] afterDecode = algorithm.decode(afterEncode);
                    System.out.println("after decoding");
                    print1DArray(afterDecode);


                    String string2 = "OLA MA KOTA A KOT MA OLE";
                    System.out.println(string2);
                    byte[] blok2 = string2.getBytes();

                    byte[] afterEncode2 = new byte[0];
                    try {
                        afterEncode2 = algorithm.encode(blok2);
                    } catch (MessageException e) {
                        throw new RuntimeException(e);
                    }
                    String str = new String(afterEncode2);
                    System.out.println(str);

                    byte[] afterDecode2 = algorithm.decode(afterEncode2);
                    String str2 = new String(afterDecode2);
                    System.out.println(str2);
                }
                case 2 -> {
                    String string = "tekst tekst";
                    byte[] mess = string.getBytes();
                    AESAlgorithm algorithm = new AESAlgorithm();
                    byte[] blok = new byte[0];
                    try {
                        blok = algorithm.encode(mess);
                    } catch (MessageException e) {
                        throw new RuntimeException(e);
                    }
                    algorithm.encrypt(blok);
                }
                case 3 -> {

                    AESAlgorithm algorithm = new AESAlgorithm();
                    byte[][] output = {{(byte) 0x32, (byte) 0x43, (byte) 0xf6, (byte) 0xa8},
                            {(byte) 0x88, (byte) 0x5a, (byte) 0x30, (byte) 0x8d},
                            {(byte) 0x31, (byte) 0x31, (byte) 0x98, (byte) 0xa2},
                            {(byte) 0xe0, (byte) 0x37, (byte) 0x07, (byte) 0x34}};

                    algorithm.subBytes(output);
                    algorithm.shiftRows(output);
                    algorithm.mixColumns(output);


                    algorithm.invMixColumns(output);
                    algorithm.invShiftRows(output);
                    algorithm.invSubBytes(output);

                    printForTests(output);


                    scanner.close();
                    return;
                }
                case 4 -> {
                    KeyHandler keyHandler = new KeyHandler();
                    byte[] cipherKey = {(byte) 0x2b, 0x7e, 0x15, 0x16, 0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
                            (byte) 0xab, (byte) 0xf7, 0x15, (byte) 0x88, 0x09, (byte) 0xcf, 0x4f, 0x3c};
                }

                default -> {
                    System.out.println("Invalid option selected, leaving the program... ");
                    scanner.close();
                    return;
                }
            }
        }
    }

    public static void printForTests(byte[][] arr) {  //col wise
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(arr[i][j] & 0xff);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void print1DArray(byte[] arr) {
        for (int i = 0; i < arr.length; i += 4) {
            for (int j = i; j < i + 4 && j < arr.length; j++) {
                System.out.print(arr[j]  & 0xff);
                System.out.print(" ");
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        showMenu();
    }
}
