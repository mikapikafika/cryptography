package org.example.dsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

public class DSAAlgorithm {


    // generowanie klucza

    //generuje sie od razu 4 klucze ? q oraz g klucz publiczny y prywatny x MOD p

    // podpisywanie byte array - pliki etc
    // podipsywanie tekstu ?? tak ma rogowski
    // odpowiednio deszyfrowanie tego i tego


    // trzebaz BIG inigera korzystac
    // czy trzeba robić skrót wiadomosci (hash) >???? i dopiero go szyfrowac

    private BigInteger p;
    private BigInteger q;
    private BigInteger g;
    private BigInteger h;
    private BigInteger x;
    private BigInteger y;
    private BigInteger k;
    private BigInteger r;
    private BigInteger s;
    private PublicKey publicKey; // XDD
    private PrivateKey privateKey;

    public void generateKey(int L, int N) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // keySize = L
        // L podzielne przez 64
        // szczerze nie losowałabym tylko dawała gotowe pary ale to jak chcesz, te są bezpieczniejsze, standard od 500 do 1000 jest przestarzały (pozdrawiam wykłady)
        // pary L i N:
        //  (1024, 160), (2048, 224), (2048, 256), or (3072, 256)
        SecureRandom random = new SecureRandom();

        // ja nie wiem czy to tak ma byc ale trudno xd
        do {
            this.p = BigInteger.probablePrime(L, random);
            this.q = BigInteger.probablePrime(N, random);
        } while (!this.p.subtract(BigInteger.ONE).mod(this.q).equals(BigInteger.ZERO)); // dk czy to potrzebne czy p-1 bedzie multiple of q zawsze
        // & tak żeby p-1 było wielokrotnością q

        do {
            this.h = new BigInteger(L - 2, random); // -2 robi rogowski ja nie zagłębiałam się na razie dlaczego
            this.g = this.h.modPow(p.subtract(BigInteger.ONE).divide(this.q), this.p);  // g to h do potęgi (p-1)/q
        } while (this.g.compareTo(BigInteger.ONE) == 0);  // g nie może być 1

        do {
            this.x = new BigInteger(N - 2, random);
        } while (this.x.compareTo(BigInteger.ZERO) <= 0 || this.x.compareTo(this.q) >= 0); // 0 < x < q
        this.y = this.g.modPow(this.x, this.p);

        DSAPublicKeySpec dsaPublicKeySpec = new DSAPublicKeySpec(y, p, q, g);
        DSAPrivateKeySpec dsaPrivateKeySpec = new DSAPrivateKeySpec(x, p, q, g);

        // albo KeyPairGenerator? czy coś?
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        PublicKey publicKey = keyFactory.generatePublic(dsaPublicKeySpec);
        PrivateKey privateKey = keyFactory.generatePrivate(dsaPrivateKeySpec);
    }

    public void hashMessage() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        String message = "testowa wiadomość na razie stringiem";
        byte[] hash = messageDigest.digest(message.getBytes(StandardCharsets.UTF_8));

        // zamiana na hex bo uwielbiamy hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String hashValue = hexString.toString();
        System.out.println("hash wynosi (to do testowania): " + hashValue);
    }

    public void generateSignature() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        String message = "testowa wiadomość na razie stringiem";
        //BigInteger hash = new BigInteger(1, hashMessage()); // trzeba pomyśleć jak to poprzekazywać
        int intq = this.q.intValue();
        do {
            this.k = new BigInteger(intq, random);
        } while (this.k.compareTo(BigInteger.ZERO) <= 0 || this.k.compareTo(this.q) >= 0); // 0 < k < q
        this.r = this.g.modPow(this.k, this.p).mod(this.q);
        //this.s = this.k.modInverse(q).multiply(hash.add(x.multiply(r)).mod(q)); // s = (k^-1 * (hash(message) + x * r)) mod q
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        DSAAlgorithm dsa = new DSAAlgorithm();
        dsa.hashMessage();
    }
}

