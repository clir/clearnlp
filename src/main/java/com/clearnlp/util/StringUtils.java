/**
 * Copyright 2014, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearnlp.util;

import com.clearnlp.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class StringUtils
{
	private StringUtils() {}
	
	/**
	 * About 2+ times faster than {@link String#toUpperCase()}.
	 * This method converts characters in [128, 256) correctly where {@link String#toUpperCase()} doesn't. 
	 */
	static public String toUpperCase(String str)
	{
		char[] array = str.toCharArray();
		char c; int i; boolean b = false;
		
		for (i=str.length()-1; i>=0; i--)
		{
			c = array[i];
			
			if ((97 <= c && c <= 122) || (224 <= c && c <= 254 && c != 247))
			{
				array[i] = (char)(c-32);
				b = true;
			}
			else if (c == 154 || c == 156 || c == 158)
			{
				array[i] = (char)(c-16);
				b = true;
			}
			else if (c == 255)
			{
				array[i] = (char)159;
				b = true;
			}
		}
		
		return b ? new String(array) : str;
	}
	
	/**
	 * About 2+ times faster than {@link String#toLowerCase()}.
	 * This method converts characters in [128, 256) correctly where {@link String#toUpperCase()} doesn't.
	 */
	static public String toLowerCase(String str)
	{
		char[] array = str.toCharArray();
		char c; int i; boolean b = false;
		
		for (i=str.length()-1; i>=0; i--)
		{
			c = array[i];
			
			if ((65 <= c && c <= 90) || (192 <= c && c <= 222 && c != 215))
			{
				array[i] = (char)(c+32);
				b = true;
			}
			else if (c == 138 || c == 140 || c == 142)
			{
				array[i] = (char)(c+16);
				b = true;
			}
			else if (c == 159)
			{
				array[i] = (char)255;
				b = true;
			}
		}
		
		return b ? new String(array) : str;
	}
	
	/** @return the specific number of spaces. */
	static public String spaces(int length)
	{
		StringBuilder build = new StringBuilder();
		int i;
		
		for (i=0; i<length; i++)
			build.append(StringConst.SPACE);
		
		return build.toString();
	}
	
	static public boolean startsWithAny(String str, String... suffixes)
	{
		for (String suffix : suffixes)
		{
			if (str.startsWith(suffix))
				return true;
		}
		
		return false;
	}
	
	static public boolean endsWithAny(String str, String... suffixes)
	{
		for (String suffix : suffixes)
		{
			if (str.endsWith(suffix))
				return true;
		}
		
		return false;
	}
	
	/** @return {@link StringConst#EMPTY} if {@code endIndex <= beginIndex || beginIndex < 0}. */
	static public String substring(String s, int beginIndex, int endIndex)
	{
		if (endIndex <= beginIndex || beginIndex < 0)
			return StringConst.EMPTY;
		
		return s.substring(beginIndex, endIndex);
	}
}