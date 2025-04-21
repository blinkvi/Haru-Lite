package cc.unknown.socket.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cc.unknown.socket.api.HookRetriever;

public class AESUtil implements HookRetriever {

	public static String encrypt(String data) {
	    try {
	        SecretKey secretKey = getSecretKey();
	        byte[] iv = generateIV(secretKey);
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
	        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
	        return Base64.getEncoder().encodeToString(encryptedData);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public static String decrypt(String encryptedData) {
	    try {
	        SecretKey secretKey = getSecretKey();
	        byte[] iv = generateIV(secretKey);
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
	        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
	        byte[] decryptedData = cipher.doFinal(encryptedBytes);
	        return new String(decryptedData, StandardCharsets.UTF_8);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	private static byte[] generateIV(SecretKey secretKey) {
	    try {
	        byte[] iv = new byte[16];
	        MessageDigest sha = MessageDigest.getInstance("SHA-256");
	        byte[] keyBytes = sha.digest(secretKey.getEncoded());
	        System.arraycopy(keyBytes, 0, iv, 0, iv.length);
	        return iv;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	private static SecretKey getSecretKey() {
	    try {
	        MessageDigest sha = MessageDigest.getInstance("SHA-256");
	        byte[] key = sha.digest(secretKey.getBytes(StandardCharsets.UTF_8));
	        return new SecretKeySpec(key, "AES");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
