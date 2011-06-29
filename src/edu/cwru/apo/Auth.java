package edu.cwru.apo;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
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
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class Auth {
	
	private static byte[] AesKey = null;
	private static byte[] lastOtp = null;
	private static byte[] HmacKey = null;
	
	//returns true is an HMAC key is set
	// false otherwise
	public static boolean HmacKeyExists()
	{
		if(HmacKey != null)
			return true;
		return false;
		
	}
	
	// returns true if an AES key is set
	// false otherwise
	public static boolean AesKeyExists()
	{
		if(AesKey != null)
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

		    return Base64.encodeToString(mac.doFinal(data.getBytes()), Base64.DEFAULT);
		    
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
        String Hmac = prefs.getString("HmacKey", null);
        String Otp = prefs.getString("lastOtp", null);
        
        if((Aes != null) && (Hmac != null) && (Otp != null))
        {
        	AesKey = Base64.decode(AesKey, Base64.DEFAULT);
        	HmacKey = Base64.decode(Hmac, Base64.DEFAULT);
        	lastOtp = Base64.decode(Otp, Base64.DEFAULT);
        }
	}

	// returns a new OTP
	public static String getOtp()
	{
		// make sure there is a key to use
		if(AesKey == null)
			return null;
		
		// encrypt the last OTP
		byte[] encrypted = AesEncrypt(lastOtp);
		
		// encrypt will return the input if an error occurs
		// Have to make sure the data was actually encrypted
		if(encrypted == lastOtp)
			return null;
		
		// everything went okay
		// update the OTP and return it in Base64 encoded form
		lastOtp = encrypted;
		return Base64.encodeToString(lastOtp, Base64.DEFAULT);
	}
	
	public static boolean setOtpAndHmac(String OTP)
	{
		lastOtp = Base64.decode(OTP, Base64.DEFAULT);
		HmacKey = AesDecrypt(lastOtp);
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
	    KeyFactory keyFactory;
		try {
	        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
	                new BigInteger("00c897f9e401819e223ffbecc6f715a8d84dce9022762e0e2d54fa434787fcaf230d28bd0c3b6b39b5211f74ffc4871c421362ccfc07ae98b88fa9728f1e26b8210ebbf4981e45867fe810938294d0095d341b646b86dcbd4c246676c203cb1584d01eef0635299714d94fa12933ecd35e6c412573156d9e6e549b7804eb6e165660507d8748bcc8c60da10099bacb94d3f7b50b1883ee108489e0dd97ed7d28e564edd4ee5d6b4225f5c23cdaaf495c3fa08c3b82e1674946e4fa1e79b2493204d6953c261105ba5d0f8dcf3fcd39a51fbc18a5f58ffff169b1bed7ceeded2ae0e8e8e2238e8b77b324d1a482593b1a642e688c860e90d5a3de8515caf384133b", 16),
	                new BigInteger("11", 16));
			keyFactory = KeyFactory.getInstance("RSA", "BC");
			//RSAPublicKeySpec rsaKeySpec = new RSAPublicKeySpec(rsaKey.MODULUS, new BigInteger("11", 16));
			RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(pubKeySpec);
			
			
			//Set up the cipher to RSA encryption
		    Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			
			// make sure the Aes Key is less than a block size
			// otherwise major errors will occur
			if(AesKey.length * 8 > pubKey.getModulus().bitLength())
				return "Error: AesKey bigger than block size of RSA Key";
			
			byte[] encryptedKey = cipher.doFinal(AesKey);
			
			// return result Base64 encoded
			return Base64.encodeToString(encryptedKey, Base64.DEFAULT);
			
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
		} catch (InvalidKeySpecException e) {
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
	
	private static byte[] AesEncrypt(byte[] input)
	{
		SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
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
		}
		return input;
		
	}
	
	private static byte[] AesDecrypt(byte[] input)
	{
		SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
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
		}
		return input;
	}
	
    /*private static class RsaPublicKey {
        public BigInteger EXPONENT;
        public BigInteger MODULUS;
        public RsaPublicKey(Context context, String filename) {
            InputStream in;
			try {
				in = context.getAssets().open(filename);
	            String contents = new String();
	            try {
	                int c;
	                while ((c = in.read()) != -1) {
	                    contents += (char) c;
	                }
	            } catch (IOException e) {
	                System.err.println("Could not read RSA key resource.");
	            }
	            int linebreak = contents.indexOf("\n");
	            EXPONENT = new BigInteger(contents.substring(0, linebreak).trim());
	            MODULUS = new BigInteger(contents.substring(linebreak + 1).trim(), 16);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }*/
    
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

}
