package com.example.krypto_aes;

public class AESAlgorithm {
    // 16 bajtów do jakis testów// xd

    private final int Nb = 4;
    private byte[][] blok_podzielony = new byte[4][4];
    public byte[][] zwroc_blok() {
        String string = "rowerrowerrowerr";
        byte[] blok = string.getBytes();
        for (int i = 0; i < 16; i++) {
            blok_podzielony[i / Nb][i % Nb] = blok[i];  // to nie jest kolumn order ale tak ma rogowski
        }
        return blok_podzielony;
    } // 128 bitow 16 bajtow 4x4

    public byte[][] subBytes(byte[][] state) {  // ogolnie duza niewiadoma dla mnie jest ta numeracja w tych bloakch 4x4
        byte[][] tmp = new byte[Nb][Nb];          // na razie na pałe to jakos robie
        for (int row = 0; row < Nb; row ++) {
            for (int col = 0; col < Nb; col ++) {
                int intValue = (state[row][col] & 0xff);  // robin inta z bajta
                int col_sbox = intValue / Nb; // chyba powinny byc na odwrót operacje i dzielenie przez 16
                int row_sbox = intValue % Nb;  // jak przy kluczach
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
        for (int row = 0; row < Nb; row ++) {
            for (int col = 0; col < Nb; col ++) {
                int intValue = (state[row][col] & 0xff);
                int col_sbox = intValue / Nb; // chyba powinny byc na odwrót operacje i dzielenie przez 16
                int row_sbox = intValue % Nb; // jak przy kluczach
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
