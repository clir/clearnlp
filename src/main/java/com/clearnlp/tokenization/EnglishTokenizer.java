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
package com.clearnlp.tokenization;

import java.util.Arrays;
import java.util.List;

import com.clearnlp.dictionary.english.DTAbbreviation;
import com.clearnlp.dictionary.english.DTCompound;
import com.clearnlp.dictionary.english.DTHyphen;
import com.clearnlp.util.CharUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class EnglishTokenizer extends AbstractTokenizer
{
	private final String[] APOSTROPHE_SUFFIX = {"d","m","s","t","z","ll","nt","re","ve"};
	
	private DTAbbreviation d_abbreviation;
	private DTHyphen d_hyphen;
	private DTCompound d_compound;
	
	public EnglishTokenizer()
	{
		super();
		d_abbreviation = new DTAbbreviation();
		d_hyphen = new DTHyphen();
	}
	
	@Override
	protected int adjustFirstNonSymbolGap(char[] cs, int beginIndex, String t)
	{
		return 0;
	}
	
	@Override
	protected int adjustLastNonSymbolGap(char[] cs, int endIndex, String t)
	{
		char sym = cs[endIndex];
		
		if (sym == '.')
		{
			if (d_abbreviation.isAbbreviationEndingWithPeriod(t.toLowerCase()))
				return 1;
		}
		
		return 0;
	}

	@Override
	protected boolean preserveSymbolInBetween(char[] cs, int index)
	{
		return preserveHyphen(cs, index);
	}
	
	private boolean preserveHyphen(char[] cs, int index)
	{
		if (CharUtils.isHyphen(cs[index]))
		{
			int len = cs.length;
			char[] tmp;
			
			if (index > 0)
			{
				tmp = Arrays.copyOfRange(cs, 0, index);
				CharUtils.toLowerCase(tmp);
				
				if (d_hyphen.isPrefix(new String(tmp)))
					return true;	
			}
			
			if (index+1 < len)
			{
				tmp = Arrays.copyOfRange(cs, index+1, len);
				CharUtils.toLowerCase(tmp);
				
				if (d_hyphen.isSuffix(new String(tmp)))
					return true;	
			}
			
			if (index+2 < len)
			{
				if (CharUtils.isVowel(cs[index+1]) && CharUtils.isHyphen(cs[index+2]))
					return true;
			}
			
			if (0 <= index-2)
			{
				if (CharUtils.isVowel(cs[index-1]) && CharUtils.isHyphen(cs[index-2]))
					return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean tokenizeWordsMore(List<String> tokens, String original, String lower, char[] cs)
	{
		return tokenizeApostrophe(tokens, original, lower, cs) || tokenizeCompound(tokens, original, lower, cs);
	}
	
	private boolean tokenizeApostrophe(List<String> tokens, String original, String lower, char[] cs)
	{
		int i;
		
		for (String suffix : APOSTROPHE_SUFFIX)
		{
			i = isApostropheSuffix(cs, lower, suffix);
			
			if (i > 0)
			{
				addTokens(tokens, original, i);
				return true;
			}
		}
		
		return false;
	}
	
	private int isApostropheSuffix(char[] cs, String lower, String suffix)
	{
		if (lower.endsWith(suffix))
		{
			if (suffix.equals("t"))
			{
				int i = lower.length() - suffix.length() - 2;
				
				if (0 < i && cs[i] == 'n' && CharUtils.isApostrophe(cs[i+1]))
					return i;
			}
			else
			{
				int i = lower.length() - suffix.length() - 1;
				
				if (0 < i && CharUtils.isApostrophe(cs[i]))
					return i;
			}
		}
		
		return -1;
	}
	
	private boolean tokenizeCompound(List<String> tokens, String original, String lower, char[] cs)
	{
		int[] splits = d_compound.getSplitIndices(lower);
		
		if (splits != null)
		{
			addTokens(tokens, original, splits);
			return true;
		}
		
		return false;
		
	}
}
