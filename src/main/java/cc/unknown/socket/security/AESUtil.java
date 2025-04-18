package cc.unknown.socket.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cc.unknown.util.Accessor;
import lombok.SneakyThrows;

public class AESUtil implements Accessor {

	@SneakyThrows
	public static String encrypt(String data) {
        SecretKey secretKey = getSecretKey();
        byte[] iv = generateIV(secretKey);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
	}

	@SneakyThrows
	public static String decrypt(String encryptedData){
        SecretKey secretKey = getSecretKey();
        byte[] iv = generateIV(secretKey);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
	}
	
	@SneakyThrows
	private static byte[] generateIV(SecretKey secretKey) {
        byte[] iv = new byte[16]; 
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(secretKey.getEncoded());
        System.arraycopy(keyBytes, 0, iv, 0, iv.length);
        return iv;
	}

	@SneakyThrows
	private static SecretKey getSecretKey() {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
	}
}
