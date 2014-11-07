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
package edu.emory.clir.clearnlp.dictionary;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface PathTokenizer
{
	String ROOT	= "edu/emory/clir/clearnlp/dictionary/tokenizer/";
	String UNIVERSAL = ROOT + "universal/";
	String ENGLISH   = ROOT + "english/";
	
	// Universal
	String CURRENCY_DOLLAR	= UNIVERSAL + "currency-dollar.txt";
	String CURRENCY			= UNIVERSAL + "currency.txt";
	String EMOTICONS		= UNIVERSAL + "emoticons.txt";
	String HTML_TAGS		= UNIVERSAL + "html-tags.txt";
	String UNITS			= UNIVERSAL + "units.txt";
	
	// English
	String EN_ABBREVIATION_PERIOD	= ENGLISH + "abbreviation-period.txt";
	String EN_HYPHEN_PREFIX			= ENGLISH + "hyphen-prefix.txt";
	String EN_HYPHEN_SUFFIX			= ENGLISH + "hyphen-suffix.txt";
	String EN_COMPOUNDS 			= ENGLISH + "compounds.txt";
}
