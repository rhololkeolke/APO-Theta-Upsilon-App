package edu.cwru.apo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

public class Auth{
	
	private static byte[] AesKey = null;
	private static byte[] lastOtp = null;
	private static byte[] HmacKey = null;
	private static PublicKey RsaPubKey = null;
	
	private static String lastHmac = null;
	
	//returns true is an HMAC key is set
	// false otherwise
	public static boolean HmacKeyExists()
	{
		if(Auth.HmacKey != null)
			return true;
		return false;
		
	}
	
	// returns true if an AES key is set
	// false otherwise
	public static boolean AesKeyExists()
	{
		if(Auth.AesKey != null)
			return true;
		return false;
	}
	
	// generates an HMAC for all the parameters currently in the client
	public static String getHmac(RestClient client)
	{
		// get the parameters currently in the client
		List<NameValuePair> params = client.getParams();
		
		//concatenate all of the data
		Iterator<NameValuePair> iter = params.iterator();
		String data = "";
		while(iter.hasNext())
			data = data + iter.next().getValue();
		
		//generate the HMAC based on the concatenated data
		SecretKeySpec sk;
		try {
			if(HmacKey == null)
				return null;
			sk = new SecretKeySpec(HmacKey, "HmacMD5");
			Mac mac;
			mac = Mac.getInstance("HmacMD5");
		    mac.init(sk);

		    return URLEncoder.encode(Base64.encodeToString(mac.doFinal(data.getBytes()), Base64.DEFAULT));
		    
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void loadKeys(SharedPreferences prefs)
	{
        String Aes = prefs.getString("AesKey", null);
        String secretKey = prefs.getString("HmacKey", null);
        String Otp = prefs.getString("lastOtp", null);
        String Hmac = prefs.getString("lastHmac", null);
        
        if((Aes != null) && (secretKey != null) && (Otp != null) && (Hmac != null))
        {
        	AesKey = Base64.decode(Aes, Base64.DEFAULT);
        	HmacKey = Base64.decode(secretKey, Base64.DEFAULT);
        	lastOtp = Base64.decode(Otp, Base64.DEFAULT);
        	lastHmac = Hmac;
        }
	}
	
	public static boolean loadRsaKey(InputStream is)
	{
		try {
			byte[] keyBytes = new byte[is.available()];
			is.read(keyBytes);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RsaPubKey = kf.generatePublic(spec);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean saveKeys(SharedPreferences pref)
	{
		Editor prefsEditor = pref.edit();
		if(AesKey != null)
			prefsEditor.putString("AesKey", Base64.encodeToString(AesKey, Base64.DEFAULT));
		else
			return false;
		if(HmacKey != null)
			prefsEditor.putString("HmacKey", Base64.encodeToString(HmacKey, Base64.DEFAULT));
		else
			return false;
		if(lastOtp != null)
			prefsEditor.putString("lastOtp", Base64.encodeToString(lastOtp, Base64.DEFAULT));
		else
			return false;
		if(lastHmac != null)
			prefsEditor.putString("lastHmac", lastHmac);
		else
			return false;
		
		return true;
			
	}

	public  static void clearKeys(SharedPreferences pref)
	{
		Editor prefsEditor = pref.edit();
		prefsEditor.clear(); // clear all the saved keys
		
		// clear all the keys in memory
		AesKey = null;
		lastOtp = null;
		HmacKey = null;
		lastHmac = null;
	}
	// returns a new OTP
	public static String getOtp(String Hmac)
	{
		// make sure there is a key to use
		if(AesKey == null)
			return null;
		
		// encrypt the last OTP
		byte[] encrypted = AesEncrypt(lastOtp, URLDecoder.decode(Hmac)); // CHANGE THE IV
		
		// encrypt will return the input if an error occurs
		// Have to make sure the data was actually encrypted
		if(encrypted == lastOtp)
			return null;
		
		// everything went okay
		// update the OTP and return it in Base64 encoded form
		lastOtp = encrypted;
		return URLEncoder.encode(Base64.encodeToString(lastOtp, Base64.DEFAULT));
	}
	
	public static boolean rollbackOtp()
	{
		if(AesKey == null || lastHmac == null)
			return false;
		
		// decrypt the last OTP
		byte[] decrypted = AesDecrypt(lastOtp, lastHmac); 
		
		// make sure it decrypted correctly
		if(decrypted == lastOtp)
			return false;
		
		// everything went okay
		// update the OTP
		lastOtp = decrypted;
		return true;
		
	}
	
	public static boolean setOtpAndHmac(String OTP, String Iv)
	{

		lastOtp = Base64.decode(URLDecoder.decode(OTP), Base64.DEFAULT);
		byte[] AesIv = Base64.decode(URLDecoder.decode(Iv), Base64.DEFAULT);
		HmacKey = AesDecrypt(lastOtp, AesIv);

		if(HmacKey != lastOtp)
			return true;
		return false;
	}
	
	// generates an AES key of length size
	public static void generateAesKey(int size)
	{
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(Auth.getTimestamp());
			keyGen.init(size, random);
			SecretKey key = keyGen.generateKey();
			AesKey = key.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getAesKey(Context context)
	{
		// load the RSA key from packaged file
		//RsaPublicKey rsaKey = new RsaPublicKey(context, "rsa_public_key.res");
	    if(RsaPubKey == null)
	    	return "Error: RsaPubKey not loaded";
		try {
			//Set up the cipher to RSA encryption
		    Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, RsaPubKey);
			
			// make sure the Aes Key is less than a block size
			// otherwise major errors will occur
			if(AesKey.length * 8 > ((RSAKey) RsaPubKey).getModulus().bitLength())
				return "Error: AesKey bigger than block size of RSA Key";
			
			byte[] encryptedKey = cipher.doFinal(AesKey);
		    			
			String base64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
			base64 = URLEncoder.encode(base64);
			// return result Base64 encoded
			return base64;
			
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

		return null;
	}

	// returns the number of milliseconds since 1970
	public static long getTimestamp()
	{
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}
	
	private static byte[] AesEncrypt(byte[] input, String iv)
	{
		try {
			SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");

			Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.substring(0, 16).getBytes()));
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
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
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
		
	}
	
	private static byte[] AesEncrypt(byte[] input, byte[] iv)
	{
		try {
			SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");

			Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
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
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
		
	}
	
	private static byte[] AesDecrypt(byte[] input, String iv)
	{
		try {
			SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.substring(0, 16).getBytes()));
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
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
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "".getBytes();
	}
	
	public static byte[] AesDecrypt(byte[] input, byte[] iv)
	{
		try {
			SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
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
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "".getBytes();
	}
    
	public static String md5(String in)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			
			byte messageDigest[] = digest.digest();
	        
	        // Create Hex String
	        /* StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();*/
			return bytesToHex(messageDigest);

		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String bytesToHex(byte[] input)
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
	
	public static byte[] hexToBytes(String hex) 
	{
		String HEXINDEX = "0123456789abcdef";
		int l = hex.length() / 2;
		byte data[] = new byte[l];
		int j = 0;

		for (int i = 0; i < l; i++) 
		{
		    char c = hex.charAt(j++);
		    int n, b;
	
		    n = HEXINDEX.indexOf(c);
			b = (n & 0xf) << 4;
			c = hex.charAt(j++);
			n = HEXINDEX.indexOf(c);
			b += (n & 0xf);
			data[i] = (byte) b;
		}

		return data;
	}

	private String padString(String source) 
	{
		char paddingChar = ' ';
		int size = 16;
		int padLength = size - source.length() % size;
	
		for (int i = 0; i < padLength; i++) 
		{
			source += paddingChar;
		}
	
		return source;
	}

	public static String getAesKeyInsecure()
	{
		if(AesKey == null)
			generateAesKey(256);
    	return bytesToHex(AesKey);
	}
}
