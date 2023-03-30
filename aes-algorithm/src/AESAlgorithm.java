import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

public class AESAlgorithm {

    // generating the key from a random number
    // generated from a Cryptographically Secure (Pseudo-)Random Number Generator like the SecureRandom class
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);  // n - user can choose; we can swap it to 128, 192 or 256
        return keyGenerator.generateKey();
    }

    // IV is a pseudo-random value and has the same size as the block that is encrypted
    public static IvParameterSpec generateIv() {
        byte[] initializationVector = new byte[16];
        new SecureRandom().nextBytes(initializationVector);
        return new IvParameterSpec(initializationVector);
    }

    public AESAlgorithm() throws NoSuchAlgorithmException {
    }
}

