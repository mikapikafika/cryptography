package com.example.krypto_aes;

import javax.crypto.*;
import java.security.*;

public class KeyHandler {

    private static final int[] rCon = {
            0x01000000, 0x02000000, 0x04000000, 0x08000000,
            0x10000000, 0x20000000, 0x40000000, 0x80000000,
            0x1B000000, 0x36000000, 0x6C000000, 0xD8000000,
            0xAB000000, 0x4D000000, 0x9A000000, 0x2F000000
    };

    /**
     * Generates a key for AES using KeyGenerator, SecretRandom + SecretKey
     * @param keyLength 128, 192 or 256
     * @return generated key
     * @throws NoSuchAlgorithmException
     */
    public byte[] generateKey(int keyLength) throws NoSuchAlgorithmException {
        KeyGenerator key = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        key.init(keyLength,random);
        SecretKey secretKey = key.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * Expands the kay
     * @param key to expand
     * @param Nk No. of 32-bit words in the key - 4, 6 or 8
     * @param Nb No. of columns in state - 4
     * @param Nr No. of rounds - 10, 12 or 14
     * @param word array to store the rest of the expanded key
     */
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
    }


    /**
     * Substitutes the bytes of a word with values from the sBox
     * @param word word to be substituted
     * @return result after substituting the bytes with sBox values
     */
    private int subWord(int word) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int intFromByte = (word >>> (8 * i)) & 0xff;
            result = result | (SBox.getSBox(intFromByte / 16, intFromByte % 16) << (8 * i));
        }
        return result;
    }

    /**
     * Rotates the bytes 1 byte to the left
     * @param word word to be rotated
     * @return rotated word
     */
    private int rotWord(int word) {
        return (word << 8) | (word >>> 24);
    }

}
