package edu.cwru.apo;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hmac {
	
	protected byte[] secretKey = null;
	protected int defaultKeyLength = 128;
	protected String mode = "HmacMD5";
	
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
