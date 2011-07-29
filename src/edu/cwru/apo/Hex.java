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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


// stores information as strings of hexadecimal
// This was made a separate class so it would be clear in the source code
// what was a general string and what was a hex string
public class Hex {
	
	private String hex;
	
	public Hex(byte[] input)
	{
		int len = input.length;
		StringBuilder sb = new StringBuilder(len << 1);
		for(int i = 0; i<len; i++)
		{
			sb.append(Character.forDigit((input[i] & 0xf0) >> 4, 16));
			sb.append(Character.forDigit(input[i] & 0x0f, 16));
		}
		hex = sb.toString();
	}
	
	public Hex(String input) {
		input.toLowerCase();
		
		if(Pattern.matches("[0-9a-f]+", input))
		{
			hex = input;
		}
	}

	// Use Regex to make sure input is correct format
	// if so save it, otherwise set it to null
	public boolean toHex(String input)
	{
		//make the string lowercase
		input.toLowerCase();
		
		// if it exists set it to input, if not set it to null
		if(Pattern.matches("[0-9a-f]+", input))
		{
			hex = input;	
			return true;
		}
		return false;
	}
	
	// check if hex was set correctly
	public boolean isNull()
	{
		if(hex == null)
			return true;
		return false;
	}
	
	public byte[] toBytes()
	{
		if (hex == null)
		{
			return null;
		}
		else if (hex.length() < 2) 
		{
			return null;
		}
		else
		{
			int len = hex.length() /2;
			byte[] buffer = new byte[len];
			for(int i=0; i<len; i++)
			{
				buffer[i] = (byte) Integer.parseInt(hex.substring(i*2, i*2+2), 16);
			}
			return buffer;
		}
	}
	
	public String toString()
	{
		return hex;
	}

}
