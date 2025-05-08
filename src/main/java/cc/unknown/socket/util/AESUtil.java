package cc.unknown.socket.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cc.unknown.socket.api.HookRetriever;

public class AESUtil implements HookRetriever {
	
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    public static String encryptString(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            new java.security.SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getStaticSecretKey(), spec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            byte[] cipherTextWithIv = new byte[IV_LENGTH_BYTE + encryptedBytes.length];
            System.arraycopy(iv, 0, cipherTextWithIv, 0, IV_LENGTH_BYTE);
            System.arraycopy(encryptedBytes, 0, cipherTextWithIv, IV_LENGTH_BYTE, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(cipherTextWithIv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptString(String encryptedText) {
        try {
            byte[] cipherTextWithIv = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            System.arraycopy(cipherTextWithIv, 0, iv, 0, IV_LENGTH_BYTE);
            byte[] encryptedBytes = new byte[cipherTextWithIv.length - IV_LENGTH_BYTE];
            System.arraycopy(cipherTextWithIv, IV_LENGTH_BYTE, encryptedBytes, 0, encryptedBytes.length);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, getStaticSecretKey(), spec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey getStaticSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
