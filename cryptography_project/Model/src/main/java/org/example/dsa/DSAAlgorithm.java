package org.example.dsa;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class DSAAlgorithm {

    private BigInteger p, q, g, h, x, y, k, r, s, u1, u2, v;
    private final int L = 512;      // L-bit prime p, key length
    private final int N = 160;      // N-bit prime q


    /**
     * Generates all the essential key parameters
     */
    public void generateKey() {
        SecureRandom random = new SecureRandom();

        // q & p
        // ProbablePrime returns a positive BigInteger
        // that is probably prime, with the specified bitLength
        // (in this case, N)
        this.q = BigInteger.probablePrime(N, random);
        BigInteger pTemp, pMinusOne;
        do {
            pTemp = BigInteger.probablePrime(L, random);
            pMinusOne = pTemp.subtract(BigInteger.ONE);           // pom1 - 1
            pTemp = pTemp.subtract(pMinusOne.remainder(q));       // pom1 - 1 is divisible by q
        } while (!pTemp.isProbablePrime(2));              // until pom1 is probablePrime with certainty level 2 (high level)
        this.p = pTemp;

        // h & g
        pMinusOne = p.subtract(BigInteger.ONE);
        do {
            this.h = new BigInteger(L - 2, random);
            this.g = h.modPow(pMinusOne.divide(q), p);             // g = h^((p - 1)/q)
        } while (g.compareTo(BigInteger.ONE) == 0);                // g != 1

        // x & y
        do {
            this.x = new BigInteger(N - 2, random);
        } while (x.compareTo(BigInteger.ZERO) <= 0 || x.compareTo(q) >= 0);  // 0 < x < q
        this.y = g.modPow(x, p);                                   // y = g^x mod p
    }


    /**
     * Hashes the message
     * @param text text to hash
     * @return hashed text in bytes
     * @throws NoSuchAlgorithmException
     */
    private byte[] hashMessage(byte[] text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(text);
    }


    /**
     * Generates the signature
     * @param text text to sign
     * @return signature
     * @throws NoSuchAlgorithmException
     */
    public BigInteger[] generateSignature(byte[] text) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        // generates a BigInteger from the hash value of the text message
        // 1 = BigInteger should be positive
        BigInteger hash = new BigInteger(1, hashMessage(text));

        // k
        do {
            this.k = new BigInteger(N - 2, random);
        } while (k.compareTo(BigInteger.ZERO) <= 0 || k.compareTo(q) >= 0);     // 0 < k < q

        // signature = (r, s)
        BigInteger[] signature = new BigInteger[2];
        this.r = g.modPow(k, p).mod(q);                                            // r = (g^k mod p) mod q
        this.s = k.modInverse(q).multiply(hash.add(x.multiply(r)).mod(q));         // s = (k^-1 * (hash(message) + x * r)) mod q
        signature[0] = this.r;
        signature[1] = this.s;
        return signature;
    }


    /**
     * Verifies the signature
     * @param text signed text to verify
     * @param signature signature to verify
     * @return either true or false
     * @throws NoSuchAlgorithmException
     */
    public boolean verifySignature(byte[] text, BigInteger[] signature) throws NoSuchAlgorithmException {
        BigInteger hash = new BigInteger(1, hashMessage(text));

        BigInteger w = signature[1].modInverse(q);                          // w = s^-1 mod q
        u1 = hash.multiply(w).mod(q);                                       // u1 = hash(message) * w mod q
        u2 = signature[0].multiply(w).mod(q);                               // u2 = r * w mod q
        v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);        // v = (g^u1 * y^u2 mod p) mod q
        return v.compareTo(signature[0]) == 0;                              // valid only if v = r
    }


    // Getters & setters used in the Controller class

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }
}

