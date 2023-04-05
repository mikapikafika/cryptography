package com.example.krypto_aes;

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
            System.out.println("[2] - Decode a file");
            System.out.println("[3] - Exit the program");
            System.out.print("Enter a number: ");

            int num = scanner.nextInt();
            System.out.println("\n\n\n");

            switch (num) {
                case 1 -> {
                    AESAlgorithm algorithm = new AESAlgorithm();
                    byte[][] arr = algorithm.zwroc_blok();
                    printForTests(arr);

                    arr = algorithm.subBytes(arr);
                    System.out.println("subBytes");
                    printForTests(arr);

                    arr = algorithm.shiftRows(arr);
                    System.out.println("shiftRows");
                    printForTests(arr);

//                    arr = algorithm.mixColumns(arr);
//                    System.out.println("mixColumns");
//                    printForTests(arr);

                    arr = algorithm.invShiftRows(arr);
                    System.out.println("invShiftRows");
                    printForTests(arr);

                    arr = algorithm.invSubBytes(arr);
                    System.out.println("invSubBytes");
                    printForTests(arr);
                }
                case 2 -> {
                    // blyblyblyyyy
                    KeyHandler keyHandler = new KeyHandler();
                    try {
                        byte[] array;
                        array = keyHandler.generateKey(128);
                        print1DArray(array);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 3 -> {

                    scanner.close();
                    return;
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
    public static void print1DArray(byte[]arr) {
        for (int i = 0; i < arr.length; i += 4) {
            for (int j = i; j < i + 4 && j < arr.length; j++) {
                System.out.print(arr[j] & 0xff);
                System.out.print(" ");
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        showMenu();
    }
}
