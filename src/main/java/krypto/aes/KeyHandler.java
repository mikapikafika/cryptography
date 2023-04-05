package krypto.aes;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class KeyHandler {

    public static byte[] generateCipherKey(int Nk) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(Nk * 32);         // Nk = 4, 6 lub 8
        SecretKey key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    // Initialization Vector - chyba potrzebny
    public static IvParameterSpec generateIv(SecretKey key) {
        byte[] iv = new byte[key.getEncoded().length];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

//    public static SecretKey expandKey(SecretKey key, IvParameterSpec iv) {
//        int keySize = key.getEncoded().length * 8;
//        int numRounds = 0;
//        switch(keySize) {
//            case 128:
//                numRounds = 10;
//                break;
//            case 192:
//                numRounds = 12;
//                break;
//            case 256:
//                numRounds = 14;
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid key size: " + keySize);
//        }
//
//        byte[] expandedKey = new byte[numRounds * 16];
//        byte[] temp = new byte[4];
//        System.arraycopy(key.getEncoded(), 0, expandedKey, 0, key.getEncoded().length);
//
//        // TO CHYBA POWINNO BYĆ W JAKIEJŚ PĘTLI
//
//        // Przepisanie 4 ostatnich bajtów aktualnego rozszerzonego klucza do tymczasowego wektora 4-bajtowego.
//        for (int i = key.getEncoded().length; i < expandedKey.length; i += 4) {
//            System.arraycopy(expandedKey, i - 4, temp, 0, 4);
//        }
//
//        // Wykonanie rotacji bajtów w wektorze o jedną pozycję w lewo. Skrajnie lewy bajt należy przepisać na skrajnie prawą pozycję.
//        byte rotate = temp[0];
//        temp[0] = temp[1];
//        temp[1] = temp[2];
//        temp[2] = temp[3];
//        temp[3] = rotate;
//
//        // Zastąpienie każdego bajtu w wektorze innym bajtem, bazując na S-Box'ach.
//        for (int i = 0; i < 4; i++) {
//            int intFromByte = temp[i];
//            if (intFromByte < 0) {          // if it's below 0 add 256 to make it positive
//                intFromByte += 4;
//            }
//            temp[i] = (byte) SBox.getSBox(intFromByte / 16, intFromByte % 16);
//        }
//
//        // Operacja Rcon: zsumowanie XOR najbardziej lewego bajtu w wektorze z dwójką podniesioną do potęgi (numer aktualnej iteracji - 1).
//
//
//
//        // Zsumowanie XOR otrzymanego 4-bajtowego wektora z 4-bajtowym blokiem zaczynającym się n bajtów przed aktualnym
//        // końcem klucza, a następnie dopisanie wyniku na koniec rozszerzanego klucza. W ten sposób otrzymuje się nowe 4 bajty rozszerzanego klucza.
//
//
//
//        return new SecretKeySpec(expandedKey, "AES");
//    }


}