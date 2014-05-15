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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class CharTokenizer
{
	private char c_delim;
	
	public CharTokenizer(char delim)
	{
		initDelimiter(delim);
	}
	
	public void initDelimiter(char delim)
	{
		c_delim = delim;
	}
	
	public List<String> tokenize(String s)
	{
		return tokenize(s, false);
	}

	public List<String> tokenize(String s, boolean includeDelim)
	{
		ArrayList<String> tokens = Lists.newArrayList();
		int bIndex = 0, eIndex, len = s.length();
		char[] cs = s.toCharArray();
		char c;
		
		for (eIndex=0; eIndex<len; eIndex++)
		{
			c = cs[eIndex];
			
			if (c == c_delim)
			{
				if (bIndex < eIndex)
					tokens.add(s.substring(bIndex, eIndex));
				
				if (includeDelim)
					tokens.add(Character.toString(c));
				
				bIndex = eIndex + 1;
			}
		}
		
		if (bIndex < len)
			tokens.add(s.substring(bIndex));
		
		tokens.trimToSize();
		return tokens;
	}
}
