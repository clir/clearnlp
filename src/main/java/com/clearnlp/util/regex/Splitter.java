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
package com.clearnlp.util.regex;

import java.util.regex.Pattern;

import com.clearnlp.constant.PatternConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class Splitter implements PatternConst
{
	static public String[] split(String s, Pattern p)
	{
		return p.split(s);
	}
	
	static public String[] splitSpace(String s)
	{
		return split(s, SPACE);
	}
	
	static public String[] splitUnderscore(String s)
	{
		return split(s, UNDERSCORE);
	}

	static public String[] splitWhiteSpaces(String s)
	{
		return split(s, WHITESPACES);
	}
	
	static public String[] splitTabs(String s)
	{
		return split(s, TAB);
	}
	
	static public String[] splitHyphens(String s)
	{
		return split(s, HYPHEN);
	}
	
	static public String[] splitCommas(String s)
	{
		return split(s, COMMA);
	}
	
	static public String[] splitColons(String s)
	{
		return split(s, COLON);
	}
}