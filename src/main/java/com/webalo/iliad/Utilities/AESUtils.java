package com.webalo.iliad.Utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtils {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "shared_secret_ke"; // Deve essere lunga 32 caratteri

    // Metodo per decifrare una stringa
    public static String decrypt(String encrypted) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        return new String(cipher.doFinal(decodedBytes));
    }

    // Metodo per cifrare una stringa (per test o uso lato client)
    public static String encrypt(String raw) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(raw.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
