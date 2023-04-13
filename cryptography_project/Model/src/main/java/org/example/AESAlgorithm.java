package org.example;


import org.example.exceptions.MessageException;
import org.example.exceptions.KeyException;

public class AESAlgorithm {
    private final int Nb = 4;
    private int Nk;  // 4, 6 or 8
    private int Nr; // 10, 12 or 14
    private int[] expandedKey = new int[Nb * (Nr + 1)];
    private byte[] primaryKey;

    public int getNb() {
        return Nb;
    }

    public int getNk() {
        return Nk;
    }

    public int getNr() {
        return Nr;
    }

    public byte[] getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Sets primaryKey, if key is the wrong size, throws KeyException
     *
     * @param primaryKey
     */
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
            default -> throw new KeyException("Invalid key size: " + keySize);
        }
    }

    public int[] getExpandedKey() {
        return expandedKey;
    }

    public void setExpandedKey(int[] expandedKey) {
        this.expandedKey = expandedKey;
    }


    /**
     * Encodes the message using a block cipher
     *
     * @param message to be encrypted
     * @return encrypted message
     * @throws MessageException
     */
    public byte[] encode(byte[] message) throws MessageException {
        if (message == null || message.length == 0) {
            throw new MessageException("Message can't be empty.");
        }

        // calculating nr of full blocks
        int nrOfFullBlocks = message.length / 16;
        if (message.length % 16 != 0) {
            nrOfFullBlocks++;   // a partial block - adding 1 number to account for that
        }
        if (nrOfFullBlocks == 0) {
            nrOfFullBlocks++;   // nr of full blocks = 1
        }

        int length = nrOfFullBlocks * 16;
        byte[] tmp = new byte[length];
        byte[] result = new byte[length];
        byte[] block = new byte[16];
        // copying message into tmp, adding bytes if needed
        for (int i = 0; i < length; i++) {
            if (i < message.length) {
                tmp[i] = message[i];
            } else {
                tmp[i] = 0;  //adding zeros if needed
            }
        }

        // encryption
        for (int i = 0; i < length; i += 16) {
            for (int j = 0; j < 16; j++) {
                block[j] = tmp[i + j];
            }
            block = encrypt(block);
            for (int j = 0; j < 16; j++) {
                result[i + j] = block[j];
            }
        }

        return result;
    }

    /**
     * Actual encryption process
     *
     * @param in input to be encrypted
     * @return encrypted input
     */
    public byte[] encrypt(byte[] in) { // Nk = 10, 12 or 14
        byte[] tmp = new byte[in.length];
        byte[][] state = new byte[Nb][Nb];
        for (int i = 0; i < in.length; i++) {
            state[i / 4][i % 4] = in[i];
        }
        addRoundKey(state, getExpandedKey(), 0);
        for (int i = 1; i < Nr; i++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, getExpandedKey(), i);
        }
        subBytes(state);
        shiftRows(state);
        addRoundKey(state, getExpandedKey(), Nr);
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = state[i / 4][i % 4];
        return tmp;
    }

    /**
     * Decodes the given message
     *
     * @param message
     * @return
     */
    public byte[] decode(byte[] message) {
        byte[] result = new byte[message.length];
        byte[] block = new byte[16];
        //copying block of data and decrypting
        for (int i = 0; i < message.length; i += 16) {
            for (int j = 0; j < 16; j++) {
                block[j] = message[i + j];
            }
            block = decrypt(block);

            for (int j = 0; j < 16; j++) {
                result[i + j] = block[j];
            }
        }
        int toCut = 0; //if the original message was not a multiple of 16 bytes, it has zeros at the end
        for (int i = result.length - 1; i >= 0; i--) {  // so we find them and delete them
            if (result[i] != 0) {
                break;
            }
            toCut++;
        }
        if (toCut > 0) {
            byte[] tmp = new byte[result.length - toCut];
            System.arraycopy(result, 0, tmp, 0, tmp.length);
            result = tmp;
        }

        return result;
    }

    /**
     * Actual decryption process
     *
     * @param in
     * @return
     */
    public byte[] decrypt(byte[] in) {  // Nk = 10, 12 or 14
        byte[] tmp = new byte[in.length];
        byte[][] state = new byte[Nb][Nb];
        for (int i = 0; i < in.length; i++) {
            state[i / 4][i % 4] = in[i];
        }
        addRoundKey(state, expandedKey, Nr);
        for (int i = Nr - 1; i >= 1; i--) {
            invSubBytes(state);
            invShiftRows(state);
            addRoundKey(state, expandedKey, i);
            invMixColumns(state);

        }
        invSubBytes(state);
        invShiftRows(state);
        addRoundKey(state, expandedKey, 0);
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = state[i / 4][i % 4];
        return tmp;
    }

    /**
     * Splits int value to four bytes
     *
     * @param intValue
     * @return
     */
    private byte[] splitIntToFourBytes(int intValue) {
        byte[] result = new byte[Nb];
        result[0] = (byte) ((intValue >> 24) & 0xFF);  // extracts the least significant 8 bits (1 byte) of an integer value
        result[1] = (byte) ((intValue >> 16) & 0xFF);
        result[2] = (byte) ((intValue >> 8) & 0xFF);
        result[3] = (byte) (intValue & 0xFF);
        return result;
    }

    /**
     * Adds round key
     *
     * @param state
     * @param expandedKey
     * @param round
     */
    private void addRoundKey(byte[][] state, int[] expandedKey, int round) {
        int start = Nb * round;
        for (int i = 0; i < Nb; i++) {
            byte[] fourBytes = splitIntToFourBytes(expandedKey[start + i]);
            for (int j = 0; j < Nb; j++) {
                state[i][j] ^= fourBytes[j]; //xor
            }
        }
    }

    /**
     * Substitutes the values in current state with the values in SBox
     *
     * @param state
     */
    public void subBytes(byte[][] state) {
        byte[][] tmp = new byte[Nb][Nb];
        for (int row = 0; row < Nb; row++) {
            for (int col = 0; col < Nb; col++) {          // we have to treat byte as unsigned value (0, 255)
                int intValue = (state[row][col] & 0xff);  // changes representation from (-128, 127) to (0, 255)
                int col_sbox = intValue % 16;
                int row_sbox = intValue / 16;
                tmp[row][col] = (byte) SBox.getSBox(row_sbox, col_sbox);
            }
        }
    }
    /**
     * Shifts rows in current state
     * First row remains unchanged
     * Second row is shifted by one position to the left
     * Third row is shifted by two positions to the left
     * Fourth row is shifted by three positions to the left
     *
     * @param state
     */
    public void shiftRows(byte[][] state) {
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

    }

    /**
     * Mixes columns in current state
     *
     * @param state
     */
    public void mixColumns(byte[][] state) {
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

    }

    /**
     * Performs mul operations
     *
     * @param a
     * @param b
     * @return
     */
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

    /**
     * Substitutes the values in current state with the values in InvertedSBox
     *
     * @param state
     */
    public void invSubBytes(byte[][] state) {
        byte[][] tmp = new byte[Nb][Nb];
        for (int row = 0; row < Nb; row++) {
            for (int col = 0; col < Nb; col++) {
                int intValue = (state[row][col] & 0xff);
                int col_sbox = intValue % 16;
                int row_sbox = intValue / 16;
                tmp[row][col] = (byte) SBox.getInvertedSBox(row_sbox, col_sbox);
            }
        }
    }

    /**
     * Shifts rows in current state
     * First row remains unchanged
     * Second row is shifted by three positions to the left
     * Third row is shifted by two positions to the left
     * Fourth row is shifted by one position to the left
     *
     * @param state
     */
    public void invShiftRows(byte[][] state) {
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
    }

    /**
     * Inverses mix columns operation
     *
     * @param state
     */
    public void invMixColumns(byte[][] state) {  // autor - chat gpt xd
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
    }


}
