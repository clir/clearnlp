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
package com.clearnlp.dictionary;

import java.util.regex.Pattern;

import com.clearnlp.util.PatternUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DTAbbreviation
{
	private Pattern WORD_DOT_PAIRS = PatternUtils.createClosedPattern("\\p{Alnum}([\\.|-]\\p{Alnum})*");
	private Pattern CONSONANTS     = PatternUtils.createClosedPattern("[bcdfghjklmnpqrstvwxz]{2,5}");
	private Pattern YEAR = PatternUtils.createClosedPattern("\\d\\d[sS]?");
	
	public boolean isAbbreviationStartingWithApostrophe(String lower)
	{
		return YEAR.matcher(lower).find();
	}
	
	public boolean isAbbreviationEndingWithPeriod(String lower)
	{
		return WORD_DOT_PAIRS.matcher(lower).find() || CONSONANTS.matcher(lower).find();
	}
}
