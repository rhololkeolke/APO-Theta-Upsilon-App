package edu.cwru.apo;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class Auth {
	
	private static String	digits = "0123456789abcdef";
	private static String OTP;
	public static SecretKey AESKey;
	public static SecretKeySpec AESKeySpec;
	public static PublicKey RSAPubKey;

	public static String md5(String in)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			return bytesToString(digest.digest());
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String HMAC(String input, String key)
	{
		SecretKeySpec sk;
		try {
			sk = new SecretKeySpec(key.getBytes(), "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(sk);
			
			return bytesToString(mac.doFinal(input.getBytes()));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String HMAC(Map<String, String> kvPairs, String key)
	{
		String v;
		String data = "";
		Iterator<String> itKeys = kvPairs.keySet().iterator();
		
		
		while(itKeys.hasNext()){
			v = kvPairs.get(itKeys.next());
			data = v + data;
		}        
        
	   SecretKeySpec sk;
		try {
			sk = new SecretKeySpec(key.getBytes(), "HmacMD5");
			Mac mac;
			mac = Mac.getInstance("HmacMD5");
		    mac.init(sk);

		    return bytesToString(mac.doFinal(data.getBytes()));
		    
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return null;
	}
	
	public static String RSAEncrypt(byte[] input)
	{
		if(RSAPubKey != null)
		{
			try {
				Cipher cipher = Cipher.getInstance("RSA/NONE/PKSC1", "BC");
				cipher.init(Cipher.ENCRYPT_MODE, RSAPubKey);
				return bytesToString(cipher.doFinal(input));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static String bytesToString(byte[] input)
	{
		int len = input.length;
		StringBuilder sb = new StringBuilder(len << 1);
		for(int i = 0; i<len; i++)
		{
			sb.append(Character.forDigit((input[i] & 0xf0) >> 4, 16));
			sb.append(Character.forDigit(input[i] & 0x0f, 16));
		}
		return sb.toString();
	}
	
	public static String AESencrypt(String seed, String cleartext) throws Exception {
	         byte[] rawKey = getRawKey(seed.getBytes());
	         byte[] result = encrypt(rawKey, cleartext.getBytes());
	         return Base64.encodeToString(result, Base64.DEFAULT);
	 }
 
	 public static String AESdecrypt(String seed, String encrypted) throws Exception {
	         byte[] rawKey = getRawKey(seed.getBytes());
	         byte[] enc = Base64.decode(encrypted, Base64.DEFAULT);
	         byte[] result = decrypt(rawKey, enc);
	         return new String(result);
	 }

	 private static byte[] getRawKey(byte[] seed) throws Exception {
	         KeyGenerator kgen = KeyGenerator.getInstance("AES");
	         SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	         sr.setSeed(seed);
	     kgen.init(128, sr); // 192 and 256 bits may not be available
	     SecretKey skey = kgen.generateKey();
	     byte[] raw = skey.getEncoded();
	     return raw;
	 }
 
	 private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
	     SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	         Cipher cipher = Cipher.getInstance("AES");
	     cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	     byte[] encrypted = cipher.doFinal(clear);
	         return encrypted;
	 }

	 private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
	     SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	         Cipher cipher = Cipher.getInstance("AES");
	     cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	     byte[] decrypted = cipher.doFinal(encrypted);
	         return decrypted;
	 }

	 public static String toHex(String txt) {
	         return toHex(txt.getBytes());
	 }
	 public static String fromHex(String hex) {
	         return new String(toByte(hex));
	 }
 
	 public static byte[] toByte(String hexString) {
	         int len = hexString.length()/2;
	         byte[] result = new byte[len];
	         for (int i = 0; i < len; i++)
	                 result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
	         return result;
	 }

	 public static String toHex(byte[] buf) {
	         if (buf == null)
	                 return "";
	         StringBuffer result = new StringBuffer(2*buf.length);
	         for (int i = 0; i < buf.length; i++) {
	                 appendHex(result, buf[i]);
	         }
	         return result.toString();
	 }
	 private final static String HEX = "0123456789ABCDEF";
	 private static void appendHex(StringBuffer sb, byte b) {
	         sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	 }

}
