/*
 * Copyright 2011 Devin Schwab, Umang Banugaria
 *
 * This file is part of the APO Theta Upsilon App for Case Western Reserve University's Alpha Phi Omega Theta Upsilon Chapter.
 *
 * The APO Theta Upsilon program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import android.content.SharedPreferences.Editor;
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
	
	public static void saveKeys(SharedPreferences prefs)
	{
		Editor edit = prefs.edit();
		edit.putString("secretKey", Auth.Hmac.getSecretKey().toString());
		edit.putInt("counter", Auth.Hmac.getCounter());
		edit.putInt("increment", Auth.Hmac.getIncrement());
		edit.commit();
	}
	
	public static void loadKeys(SharedPreferences prefs)
	{
		Hex secretKey = new Hex(prefs.getString("secretKey", "0"));
		int counter = prefs.getInt("counter", -1);
		int increment = prefs.getInt("increment", 0);
		if(secretKey.toString().compareTo("0") == 0)
			return;
		if(counter == -1)
			return;
		if(increment == 0)
			return;
		Auth.Hmac = new DynamicHmac(secretKey, counter, increment);
	}
}