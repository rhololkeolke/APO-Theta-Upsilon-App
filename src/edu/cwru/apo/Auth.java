package edu.cwru.apo;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import android.util.Base64;

public class Auth{
	
	private static int counter = 0;
	private static byte[] sharedSecret = null;
	public static DynamicHmac Hmac = new DynamicHmac();
	
	// returns the number of milliseconds since 1970
	public static long getTimestamp()
	{
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}
    
	public static Hex md5(String in)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());

			byte messageDigest[] = digest.digest();
			return new Hex(messageDigest);

		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}