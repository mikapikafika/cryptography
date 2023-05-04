package org.example.dsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class DSAAlgorithm {


    // generowanie klucza

    //generuje sie od razu 4 klucze ? q oraz g klucz publiczny y prywatny x MOD p

    // podpisywanie byte array - pliki etc
    // podipsywanie tekstu ?? tak ma rogowski
    // odpowiednio deszyfrowanie tego i tego


    // trzebaz BIG inigera korzystac
    // czy trzeba robić skrót wiadomosci (hash) >???? i dopiero go szyfrowac


    private BigInteger p, q, g, h, x, y, k, r, s, w, u1, u2, v;

    private final int L = 512;
    private final int N = 160;

    public void generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // keySize = L
        // L podzielne przez 64
        // szczerze nie losowałabym tylko dawała gotowe pary ale to jak chcesz, te są bezpieczniejsze, standard od 500 do 1000 jest przestarzały (pozdrawiam wykłady)
        // pary L i N:
        //  (1024, 160), (2048, 224), (2048, 256), or (3072, 256)
        SecureRandom random = new SecureRandom();

        this.q = BigInteger.probablePrime(N, random);
        BigInteger pom1, pom2;       // jak rogowski na razie zrobione
        do {
            pom1 = BigInteger.probablePrime(L, random);
            pom2 = pom1.subtract(BigInteger.ONE);
            pom1 = pom1.subtract(pom2.remainder(q));
        } while (!pom1.isProbablePrime(2)); // dk czy to potrzebne czy p-1 bedzie multiple of q zawsze
        this.p = pom1;
        // & tak żeby p-1 było wielokrotnością q


        do {
            this.h = new BigInteger(L - 2, random); // -2 robi rogowski ja nie zagłębiałam się na razie dlaczego
            this.g = this.h.modPow(p.subtract(BigInteger.ONE).divide(this.q), this.p);  // g to h do potęgi (p-1)/q
        } while (this.g.compareTo(BigInteger.ONE) == 0);  // g nie może być 1

        do {
            this.x = new BigInteger(N - 2, random);
        } while (this.x.compareTo(BigInteger.ZERO) <= 0 || this.x.compareTo(this.q) >= 0); // 0 < x < q
        this.y = this.g.modPow(this.x, this.p);
    }

    public byte[] hashMessage(byte[] text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(text);

        return hash;
    }

    public BigInteger[] generateSignature(byte[] text) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();

        BigInteger hash = new BigInteger(1, hashMessage(text));
        do {
            this.k = new BigInteger(N - 2, random);
        } while (this.k.compareTo(BigInteger.ZERO) <= 0 || this.k.compareTo(this.q) >= 0); // 0 < k < q
        BigInteger[] signature = new BigInteger[2];
        this.r = this.g.modPow(this.k, this.p).mod(this.q);
        this.s = this.k.modInverse(q).multiply(hash.add(x.multiply(r)).mod(q)); // s = (k^-1 * (hash(message) + x * r)) mod q
        signature[0] = this.r;
        signature[1] = this.s;
        return signature;
    }

    public boolean verifySignature(byte[] text, BigInteger[] signature) throws NoSuchAlgorithmException {
        BigInteger hash = new BigInteger(1, hashMessage(text));
        u1 = hash.multiply(signature[1].modInverse(q)).mod(q);
        u2 = signature[0].multiply(signature[1].modInverse(q)).mod(q);
        v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);
        return v.compareTo(signature[0]) == 0;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        DSAAlgorithm dsa = new DSAAlgorithm();
        try {
            dsa.generateKey();
            String string = "wlazl kotek na plotek";
            BigInteger[] signature = dsa.generateSignature(string.getBytes());
            String stringFalse = "dsdsdsd";
            System.out.println(dsa.verifySignature(stringFalse.getBytes(), signature));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

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

