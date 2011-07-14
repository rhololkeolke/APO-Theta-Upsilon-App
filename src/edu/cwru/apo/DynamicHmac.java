package edu.cwru.apo;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DynamicHmac extends Hmac {
	
	private int counter = 0;
	
	public DynamicHmac()
	{
		SecureRandom random = new SecureRandom();
		secretKey = new byte[defaultKeyLength];
		random.nextBytes(secretKey);
	}
	
	public DynamicHmac(String mode, int keyLength)
	{
		SecureRandom random = new SecureRandom();
		secretKey = new byte[keyLength];
		random.nextBytes(secretKey);
		this.mode = mode;
	}
	
	public Hex generate(String data) throws InvalidKeyException,NoSuchAlgorithmException
	{
		if(secretKey == null)
			return null;
		SecretKeySpec sk = new SecretKeySpec(HOTP(), mode);
		Mac mac;
		mac = Mac.getInstance(mode);
	    mac.init(sk);

	    return new Hex(mac.doFinal(data.getBytes()));
	}
	
	private byte[] HOTP()
	{
		
		try {
			if(secretKey == null)
				return null;
			SecretKeySpec sk = new SecretKeySpec(secretKey, mode);
			Mac mac;
			mac = Mac.getInstance(mode);
		    mac.init(sk);
		    
		    byte[] hmac_result =  mac.doFinal(intToBytes(counter));
		    
		    // make sure the index will be inbounds
		    if(hmac_result.length < 19)
		    	return null;

		    // get the last 4 bits of the 19th byte of the hmac_result
		    // this acts as an offset
		    int offset = (int)hmac_result[19] & 0xf; 
		    
		    // get the binary code
		    int bin_code = (int)((hmac_result[offset] & 0x7f) << 24
		    						| (hmac_result[offset+1] & 0xff) << 16
		    						| (hmac_result[offset+2] & 0xff) << 8
		    						| (hmac_result[offset+3] & 0xff));
		    
		    return intToBytes(bin_code);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] intToBytes(int input)
	{
	        return new byte[] {
	                (byte)(input >>> 24),
	                (byte)(input >>> 16),
	                (byte)(input >>> 8),
	                (byte)input};
	}

}
