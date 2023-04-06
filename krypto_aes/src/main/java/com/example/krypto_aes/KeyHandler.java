package com.example.krypto_aes;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class KeyHandler {
//    Nk	 Number of 32-bit words comprising the Cipher Key. For this
//    standard, Nk = 4, 6, or 8

    public byte[] generateKey(int keyLength) throws NoSuchAlgorithmException {
        KeyGenerator key = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        key.init(keyLength,random);
        SecretKey secretKey = key.generateKey();
        return secretKey.getEncoded();
    }
//    private static final byte[] rCon = {
//            (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80,
//            (byte) 0x1b, (byte) 0x36, (byte) 0x6c, (byte) 0xd8, (byte) 0xab, (byte) 0x4d, (byte) 0x9a, (byte) 0x2f
//    };

    private static final int[] rCon = {
            0x01000000, 0x02000000, 0x04000000, 0x08000000,
            0x10000000, 0x20000000, 0x40000000, 0x80000000,
            0x1B000000, 0x36000000, 0x6C000000, 0xD8000000,
            0xAB000000, 0x4D000000, 0x9A000000, 0x2F000000
    };



    public void expandKey(byte[] key, int Nk, int Nb, int Nr, int[] word) {
        int i = 0;
        while (i < Nk) {
            word[i] = (key[4 * i] << 24) | ((key[4 * i + 1] & 0xff) << 16) | ((key[4 * i + 2] & 0xff) << 8) | (key[4 * i + 3] & 0xff);
            i++;
        }

        i = Nk;

        while (i < Nb * (Nr + 1)) {
            int temp = word[i - 1];

            if (i % Nk == 0) {
                temp = subWord(rotWord(temp)) ^ rCon[(i - 1) / Nk];
            } else if (Nk > 6 && i % Nk == 4) {
                temp = subWord(temp);
            }
            word[i] = word[i - Nk] ^ temp;
            i++;
        }

//        System.out.println("Expanded Key:");
//        for (int k = 0; k < Nb * (Nr + 1); k++) {
//            System.out.printf("w%d = %08x\n", k, word[k]);
//        }
    }

    public void expandKeyForTests(byte[] key, int Nk, int Nb, int Nr, int[] word) {
        int i = 0;
        while (i < Nk) {
            word[i] = (key[4 * i] << 24) | ((key[4 * i + 1] & 0xff) << 16) | ((key[4 * i + 2] & 0xff) << 8) | (key[4 * i + 3] & 0xff);
            i++;
        }

        i = Nk;

        while (i < Nb * (Nr + 1)) {
            int temp = word[i - 1];
            System.out.printf("i = %d \t temp = %08x\n", i, temp);

            if (i % Nk == 0) {
                int rotWordResult = rotWord(temp);
                int subWordResult = subWord(rotWordResult);
                int rConResult = rCon[(i - 1) / Nk];
                temp = subWordResult ^ rConResult;
                System.out.printf("after rotWord = %08x \t after subWord = %08x \t rCon [i/Nk] = %08x\ntemp now = %08x\n", rotWordResult, subWordResult, rConResult, temp);
            } else if (Nk > 6 && i % Nk == 4) {
                temp = subWord(temp);
                System.out.printf("temp after only subWord = %08x\n", temp);
            }
            int wordNk = word[i - Nk];
            word[i] = word[i - Nk] ^ temp;
            System.out.printf("temp now = %08x\nword[i-Nk] = %08x \t word[i] after XOR = %08x\n\n\n", temp, wordNk, word[i]);
            i++;
        }

        System.out.println("Expanded Key:");
        for (int k = 0; k < Nb * (Nr + 1); k++) {
            System.out.printf("w%d = %08x\n", k, word[k]);
        }
    }


    private int subWord(int word) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int intFromByte = (word >>> (8 * i)) & 0xff;
            result = result | (SBox.getSBox(intFromByte / 16, intFromByte % 16) << (8 * i));
        }
        return result;
    }

//    private int subWord(int word) {
//        return (SBox.getSBox((word >> 24) & 0xff) << 24) |
//                (SBox.getSBox((word >> 16) & 0xff) << 16) |
//                (SBox.getSBox((word >> 8) & 0xff) << 8) |
//                (SBox.getSBox(word & 0xff));
//    }

    private int rotWord(int word) {
//        return ((word << 8) & 0xffffff00) | ((word >>> 24) & 0xff);
        return (word << 8) | (word >>> 24);

    }


}
