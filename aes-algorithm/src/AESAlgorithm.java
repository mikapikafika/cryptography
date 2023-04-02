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
                int col_sbox = intValue / Nb; // 28
                int row_sbox = intValue % Nb;  // 2
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
