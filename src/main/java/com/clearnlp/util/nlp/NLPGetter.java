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
package com.clearnlp.util.nlp;

import java.io.InputStream;

import com.clearnlp.component.morphology.AbstractMPAnalyzer;
import com.clearnlp.component.morphology.DefaultMPAnalyzer;
import com.clearnlp.component.morphology.EnglishMPAnalyzer;
import com.clearnlp.conversion.AbstractC2DConverter;
import com.clearnlp.conversion.EnglishC2DConverter;
import com.clearnlp.conversion.headrule.HeadRuleMap;
import com.clearnlp.tokenization.AbstractTokenizer;
import com.clearnlp.tokenization.EnglishTokenizer;
import com.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class NLPGetter
{
	private NLPGetter() {}
	
	/** @param in the inputstream for a headrule file. */
	static public AbstractC2DConverter getC2DConverter(TLanguage language, InputStream in)
	{
		HeadRuleMap headrules = new HeadRuleMap(in);
		return new EnglishC2DConverter(headrules);
	}
	
	static public AbstractTokenizer getTokenizer(TLanguage language)
	{
		return new EnglishTokenizer();
	}
	
	static public AbstractMPAnalyzer getMPAnalyzer(TLanguage language)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishMPAnalyzer();
		default     : return new DefaultMPAnalyzer();
		}
	}
}