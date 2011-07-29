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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hmac {
	
	protected byte[] secretKey = null;
	protected int defaultKeyLength = 128;
	protected String mode = "HmacSHA1";
	
	public Hmac()
	{
		SecureRandom random = new SecureRandom();
		secretKey = new byte[defaultKeyLength];
		random.nextBytes(secretKey);
	}
	
	public Hmac(String mode, int keyLength)
	{
		SecureRandom random = new SecureRandom();
		secretKey = new byte[keyLength];
		random.nextBytes(secretKey);
		this.mode = mode;
	}
	
	public Hmac(String mode, Hex secretKey)
	{
		this.secretKey = secretKey.toBytes();
		this.mode = mode;
	}
	
	public Hex getSecretKey()
	{
		return new Hex(secretKey);
	}
	
	public String getMode()
	{
		return mode;
	}
	
	// NOTE: this does not check if provided secret key is valid
	public void setKey(Hex secretKey)
	{
		this.secretKey = secretKey.toBytes();
	}
	
	// NOTE: this does not check if provided mode is valid
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	public Hex generate(String data) throws InvalidKeyException,NoSuchAlgorithmException
	{
		if(secretKey == null)
			return null;
		SecretKeySpec sk = new SecretKeySpec(secretKey, mode);
		Mac mac;
		mac = Mac.getInstance(mode);
	    mac.init(sk);

	    return new Hex(mac.doFinal(data.getBytes()));
	}

}
