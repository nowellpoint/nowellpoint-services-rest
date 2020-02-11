package com.nowellpoint.services.rest.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureValue {
	
	public static String encryptBase64(String key, String value) {
		return Base64.getEncoder().encodeToString( encrypt( key, value.getBytes( StandardCharsets.UTF_8 ) ) );
	}
	
	public static String decryptBase64(String key, String base64EncodedString) {
		System.out.println(base64EncodedString);
		return new String( decrypt( key, Base64.getDecoder().decode( base64EncodedString ) ), StandardCharsets.UTF_8 );
	}

	public static byte[] encrypt(String key, byte[] value) {	
		return doFinal(key, Cipher.ENCRYPT_MODE, value);
	}
	
	public static byte[] decrypt(String key, byte[] value) {	
		return doFinal(key, Cipher.DECRYPT_MODE, value);
	}
	
	private static byte[] doFinal(String keyString, int cipherMode, byte[] bytes) {
		try {
			byte[] key = keyString.getBytes( StandardCharsets.UTF_8 );
			
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 32);
		    
			SecretKey secretKey = new SecretKeySpec(key, "AES");
		    
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			
			IvParameterSpec spec = new IvParameterSpec(new byte[cipher.getBlockSize()]);
			
			cipher.init(cipherMode, secretKey, spec);
			
			return cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
	}
}