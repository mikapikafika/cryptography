

public class AESAlgorithm {
     // 16 bajtów do jakis testów// xd

    private final int N = 4;
    private byte[][] blok_podzielony = new byte[4][4];
    public byte[][] zwroc_blok() {
        String string = "rowerrowerrowerr";
        byte[] blok = string.getBytes();
        for (int i = 0; i < 16; i++) {
            blok_podzielony[i / N][i % N] = blok[i];  // to nie jest kolumn order ale tak ma rogowski
        }
        return blok_podzielony;
    } // 128 bitow 16 bajtow 4x4

    public byte[][] subBytes(byte[][] blok) {  // ogolnie duza niewiadoma dla mnie jest ta numeracja w tych bloakch 4x4
        byte[][] tmp = new byte[N][N];          // na razie na pałe to jakos robie
        for (int row = 0; row < N; row ++) {
            for (int col = 0; col < N; col ++) {
                int intValue = (blok[row][col] & 0xff);  // robin inta z bajta
                int col_sbox = intValue / N; // 28
                int row_sbox = intValue % N;  // 2
                tmp[row][col] = (byte) SBox.getSBox(row_sbox, col_sbox);
            }
        }
        return tmp;
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

