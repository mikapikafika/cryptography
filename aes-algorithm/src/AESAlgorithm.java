

public class AESAlgorithm {


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

