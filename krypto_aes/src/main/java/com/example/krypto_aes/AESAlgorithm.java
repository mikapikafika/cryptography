package com.example.krypto_aes;

import javax.crypto.SecretKey;

public class AESAlgorithm {
    //TODO użytkownik może wygenerować klucz, lub wczytać go do pliku i może zapisac go do pliku

    private final int Nb = 4;
    private int Nk;  //number od 32-bit words Nk = 4, 6, 8
    private int Nr; // moze byc zmienne
//    int[] expandedKey = new int[Nb * (Nr + 1)]; //expandKey(key, Nk, Nb, Nr, expandedKey);
    int[] expandedKey;
    byte[] primaryKey;

    public void setPrimaryKey(byte[] primaryKey) {
        this.primaryKey = primaryKey;
        int keySize = primaryKey.length * 8;
        switch (keySize) {
            case 128 -> {
                this.Nr = 10;
                this.Nk = 4;
            }
            case 192 -> {
                this.Nr = 12;
                this.Nk = 6;
            }
            case 256 -> {
                this.Nr = 14;
                this.Nk = 8;
            }
            default -> throw new IllegalArgumentException("Invalid key size: " + keySize);
        }
    }

    public void setExpandedKey(int[] expandedKey) {
        this.expandedKey = expandedKey;
    }

    public byte[] encrypt(byte[] in) {
        byte[] tmp = new byte[in.length];
        byte[][] state = new byte[Nb][Nb];
        for (int i = 0; i < in.length; i++) {
            state[i / 4][i % 4] = in[i];
        }
        addRoundKey(state, expandedKey, 0);
        for (int i = 1; i < Nr; i++) {
            state = subBytes(state);
            state = shiftRows(state);
            state = mixColumns(state);
            addRoundKey(state, expandedKey, i);
        }
        state = subBytes(state);
        state = shiftRows(state);
        addRoundKey(state, expandedKey, Nr);
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = state[i / 4][i%4];
        return tmp;
    }

    public byte[] decrypt(byte[] in) {
        byte[] tmp = new byte[in.length];
        byte[][] state = new byte[Nb][Nb];
        for (int i = 0; i < in.length; i++) {
            state[i / 4][i % 4] = in[i];
        }
        addRoundKey(state, expandedKey, Nr);
        for (int i = Nr - 1 ; i >= 1; i--) {
            state = invSubBytes(state);
            state = invShiftRows(state);
            state = invMixColumns(state);
            addRoundKey(state, expandedKey, i);
        }
        state = invSubBytes(state);
        state = invShiftRows(state);
        addRoundKey(state, expandedKey, 0);
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = state[i / 4][i%4];
        return tmp;
    }
    private byte[] splitIntToFourBytes(int intValue) {
        byte[] result = new byte[Nb];
        result[0] = (byte) ((intValue >> 24) & 0xFF);
        result[1] = (byte) ((intValue >> 16) & 0xFF);
        result[2] = (byte) ((intValue >> 8) & 0xFF);
        result[3] = (byte) (intValue & 0xFF);
        return result;
    }
    private void addRoundKey(byte[][] state, int[] expandedKey, int round) { // na referencji po prostu operuje
        int start = Nb * round;                                              // zawsze mozna to zmienic
        for (int i = 0; i < Nb; i++) {
            byte[] fourBytes = splitIntToFourBytes(expandedKey[start + i]); // mam juz 4 bajty
            for (int j = 0; j < Nb; j++) {
                state[i][j] ^= fourBytes[j];
            }
        }
    }


    public byte[][] subBytes(byte[][] state) {
        byte[][] tmp = new byte[Nb][Nb];
        for (int row = 0; row < Nb; row++) {
            for (int col = 0; col < Nb; col++) {
                int intValue = (state[row][col] & 0xff);  // robin inta z bajta
                int col_sbox = intValue % 16;
                int row_sbox = intValue / 16;
                tmp[row][col] = (byte) SBox.getSBox(row_sbox, col_sbox);
            }
        }
        return tmp;
    }

    public byte[][] shiftRows(byte[][] state) {
        byte tmp;

        // Shift row 1 by 1
        tmp = state[1][0];
        state[1][0] = state[1][1];
        state[1][1] = state[1][2];
        state[1][2] = state[1][3];
        state[1][3] = tmp;

        // Shift row 2 by 2
        tmp = state[2][0];
        state[2][0] = state[2][2];
        state[2][2] = tmp;
        tmp = state[2][1];
        state[2][1] = state[2][3];
        state[2][3] = tmp;

        // Shift row 3 by 3
        tmp = state[3][3];
        state[3][3] = state[3][2];
        state[3][2] = state[3][1];
        state[3][1] = state[3][0];
        state[3][0] = tmp;

        return state; // ??? albo void?
    }

    public byte[][] mixColumns(byte[][] state) {
        for (int i = 0; i < Nb; i++) {
            byte c0 = state[0][i];
            byte c1 = state[1][i];
            byte c2 = state[2][i];
            byte c3 = state[3][i];

            // 0x02 - like multiplying by x
            // 0x03 - like multiplying by (x + 1)
            state[0][i] = (byte) (mul(0x02, c0) ^ mul(0x03, c1) ^ c2 ^ c3);
            state[1][i] = (byte) (c0 ^ mul(0x02, c1) ^ mul(0x03, c2) ^ c3);
            state[2][i] = (byte) (c0 ^ c1 ^ mul(0x02, c2) ^ mul(0x03, c3));
            state[3][i] = (byte) (mul(0x03, c0) ^ c1 ^ c2 ^ mul(0x02, c3));
        }

        return state;
    }

    private byte mul(int a, byte b) {
        int result = 0;

        for (int i = 0; i < 8; i++) {
            // Check if the i-th bit of a is 1
            if (((a >> i) & 0x01) == 1) {
                // XOR result with b - adding b
                result ^= b;
            }
            // Check if left bit of b is 1
            // Carry will happen when b is shifted left
            boolean carry = (b & 0x80) != 0;
            // Shift b 1 bit to the left
            b <<= 1;
            if (carry) {
                // XOR b with AES polynomial
                b ^= 0x1B;
            }
        }
        // The result of multiplying a and b in GF(2^8) with AES polynomial
        return (byte) result;
    }


    // Evil versions of methods above be like

    public byte[][] invSubBytes(byte[][] state) {
        byte[][] tmp = new byte[Nb][Nb];
        for (int row = 0; row < Nb; row++) {
            for (int col = 0; col < Nb; col++) {
                int intValue = (state[row][col] & 0xff);
                int col_sbox = intValue % 16;
                int row_sbox = intValue / 16;
                tmp[row][col] = (byte) SBox.getInvertedSBox(row_sbox, col_sbox);
            }
        }
        return tmp;
    }

    public byte[][] invShiftRows(byte[][] state) {
        byte tmp;

        // Shift row 1 by 3
        tmp = state[1][3];
        state[1][3] = state[1][2];
        state[1][2] = state[1][1];
        state[1][1] = state[1][0];
        state[1][0] = tmp;

        // Shift row 2 by 2
        tmp = state[2][0];
        state[2][0] = state[2][2];
        state[2][2] = tmp;
        tmp = state[2][1];
        state[2][1] = state[2][3];
        state[2][3] = tmp;

        // Shift row 3 by 1
        tmp = state[3][0];
        state[3][0] = state[3][1];
        state[3][1] = state[3][2];
        state[3][2] = state[3][3];
        state[3][3] = tmp;

        return state;
    }
    public byte[][] invMixColumns(byte[][] state) {  // autor - chat gpt xd
        for (int i = 0; i < Nb; i++) {
            byte c0 = state[0][i];
            byte c1 = state[1][i];
            byte c2 = state[2][i];
            byte c3 = state[3][i];

            // 0x0E - like multiplying by (x^3 + x^2 + x)
            // 0x0B - like multiplying by (x^3 + x + 1)
            // 0x0D - like multiplying by (x^3 + x^2 + 1)
            // 0x09 - like multiplying by (x^3 + 1)
            state[0][i] = (byte) (mul(0x0E, c0) ^ mul(0x0B, c1) ^ mul(0x0D, c2) ^ mul(0x09, c3));
            state[1][i] = (byte) (mul(0x09, c0) ^ mul(0x0E, c1) ^ mul(0x0B, c2) ^ mul(0x0D, c3));
            state[2][i] = (byte) (mul(0x0D, c0) ^ mul(0x09, c1) ^ mul(0x0E, c2) ^ mul(0x0B, c3));
            state[3][i] = (byte) (mul(0x0B, c0) ^ mul(0x0D, c1) ^ mul(0x09, c2) ^ mul(0x0E, c3));
        }

        return state;
    }

    public byte[][] byteArrayToState(byte[] byteArray) { // z 1D do 2D tablicy
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = byteArray[i * 4 + j];
            }
        }
        return state;
    }








    /* Przygotowanie podkluczy:
        generowany jest jeden podklucz początkowy, a następnie po kolejnym jednym podkluczu dla każdej rundy szyfrującej.
     */

    /* Runda inicjująca:
        każdy bajt w bloku danych jest dodawany do odpowiadającego mu bajtowi pierwszego podklucza za pomocą sumowania XOR.
     */

    /* Runda szyfrująca:
        1. Każdy bajt danych jest zastępowany innym bajtem, na podstawie z góry zdefiniowanej tabeli - sbox
        2. Przesunięcie bajtów w trzech ostatnich macierzach stanu w lewo
        3. Mnożenie kolumn: wszystkie kolumny macierzy stanu są mnożone przez stałą macierz o wielkości 4 bajty × 4 bajty
        4. Dodanie XOR wszystkich bajtów bloku danych do bajtów podklucza właściwego dla danej rundy
     */

    /* Runda kończąca:
        wykonywane są te same operacje co w normalnych rundach szyfrujących, z wyjątkiem operacji mnożenia kolumn, która w Rundzie Kończącej jest pomijana.
     */


}
