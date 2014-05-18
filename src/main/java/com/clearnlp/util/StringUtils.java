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
		boolean b = CharUtils.toUpperCase(array);
		return b ? new String(array) : str;
	}
	
	/**
	 * About 2+ times faster than {@link String#toLowerCase()}.
	 * This method converts characters in [128, 256) correctly where {@link String#toUpperCase()} doesn't.
	 */
	static public String toLowerCase(String str)
	{
		char[] array = str.toCharArray();
		boolean b = CharUtils.toLowerCase(array);
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
}