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
package com.clearnlp.nlp;

import java.io.InputStream;

import com.clearnlp.component.morphology.AbstractMPAnalyzer;
import com.clearnlp.component.morphology.DefaultMPAnalyzer;
import com.clearnlp.component.morphology.EnglishMPAnalyzer;
import com.clearnlp.conversion.AbstractC2DConverter;
import com.clearnlp.conversion.EnglishC2DConverter;
import com.clearnlp.headrule.HeadRuleMap;
import com.clearnlp.type.LanguageType;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class NLPGetter
{
	private NLPGetter() {}
	
	/** @param in the inputstream for a headrule file. */
	static public AbstractC2DConverter getC2DConverter(LanguageType language, InputStream in)
	{
		HeadRuleMap headrules = new HeadRuleMap(in);
		return new EnglishC2DConverter(headrules);
	}
	
	static public AbstractMPAnalyzer getMPAnalyzer(LanguageType language)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishMPAnalyzer();
		default     : return new DefaultMPAnalyzer();
		}
	}
}