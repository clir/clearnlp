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

import java.util.List;

import com.clearnlp.constant.CharConst;
import com.clearnlp.dictionary.english.DTAbbreviation;
import com.clearnlp.dictionary.english.DTHyphen;
import com.clearnlp.dictionary.universal.DTCompound;
import com.clearnlp.tokenization.english.ApostropheEnglishTokenizer;
import com.clearnlp.type.LanguageType;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class EnglishTokenizer extends AbstractTokenizer
{
	private ApostropheEnglishTokenizer d_apostrophe;
	private DTAbbreviation             d_abbreviation;
	private DTCompound                 d_compound;
	private DTHyphen                   d_hyphen;
	
	public EnglishTokenizer()
	{
		d_apostrophe   = new ApostropheEnglishTokenizer();
		d_abbreviation = new DTAbbreviation();
		d_compound     = new DTCompound(LanguageType.ENGLISH);
		d_hyphen       = new DTHyphen();
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
		
		if (sym == CharConst.PERIOD)
		{
			if (d_abbreviation.isAbbreviationEndingWithPeriod(t.toLowerCase()))
				return 1;
		}
		
		return 0;
	}

	@Override
	protected boolean preserveSymbolInBetween(char[] cs, int index)
	{
		return d_hyphen.preserveHyphen(cs, index);
	}
	
	@Override
	protected boolean tokenizeWordsMore(List<String> tokens, String original, String lower, char[] lcs)
	{
		return tokenize(tokens, original, lower, lcs, d_apostrophe) || tokenize(tokens, original, lower, lcs, d_compound); 
	}
}
