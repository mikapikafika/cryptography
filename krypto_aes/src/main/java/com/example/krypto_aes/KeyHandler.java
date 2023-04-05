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
    private static final byte[] rCon = {
            (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1b, (byte) 0x36, (byte) 0x6c, (byte) 0xd8, (byte) 0xab, (byte) 0x4d, (byte) 0x9a, (byte) 0x2f
    };

    public SecretKey expandKey(SecretKey key) {
        int keySize = key.getEncoded().length * 8;
        int numRounds = 0;
        switch(keySize) {
            case 128:
                numRounds = 10;
                break;
            case 192:
                numRounds = 12;
                break;
            case 256:
                numRounds = 14;
                break;
            default:
                throw new IllegalArgumentException("Invalid key size: " + keySize);
        }

        byte[] expandedKey = new byte[(numRounds + 1) * 16];
        byte[] temp = new byte[4];
//        int rconIteration = 1;
        System.arraycopy(key.getEncoded(), 0, expandedKey, 0, key.getEncoded().length);

        // TO CHYBA POWINNO BYĆ W JAKIEJŚ PĘTLI

        // Przepisanie 4 ostatnich bajtów aktualnego rozszerzonego klucza do tymczasowego wektora 4-bajtowego.
        for (int i = key.getEncoded().length; i < expandedKey.length; i += 4) {
            System.arraycopy(expandedKey, i - 4, temp, 0, 4);


            // Wykonanie rotacji bajtów w wektorze o jedną pozycję w lewo. Skrajnie lewy bajt należy przepisać na skrajnie prawą pozycję.
            byte rotate = temp[0];
            temp[0] = temp[1];
            temp[1] = temp[2];
            temp[2] = temp[3];
            temp[3] = rotate;

            // Zastąpienie każdego bajtu w wektorze innym bajtem, bazując na S-Box'ach.
            for (int j = 0; j < 4; j++) {
                int intFromByte = temp[j] & 0xff;
                temp[j] = (byte) SBox.getSBox(intFromByte / 16, intFromByte % 16);
            }
            temp[0] ^= rCon[i/4];
            // Operacja Rcon: zsumowanie XOR najbardziej lewego bajtu w wektorze z dwójką podniesioną do potęgi (numer aktualnej iteracji - 1).



            // XOR the current 4-byte word with the block `n` bytes before the end of the current key
            for (int j = 0; j < 4; j++) {
                expandedKey[i + j] = (byte) (expandedKey[i + j - key.getEncoded().length] ^ temp[j]);
            }

            // Zsumowanie XOR otrzymanego 4-bajtowego wektora z 4-bajtowym blokiem zaczynającym się n bajtów przed aktualnym
            // końcem klucza, a następnie dopisanie wyniku na koniec rozszerzanego klucza. W ten sposób otrzymuje się nowe 4 bajty rozszerzanego klucza.
            for (int j = 0; j < 4; j++) {
                expandedKey[i + j] = (byte) (expandedKey[i + j - 4] ^ temp[j]);
            }
        }


        return new SecretKeySpec(expandedKey, "AES");
    }




}
