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
package com.clearnlp.constant;

import java.util.regex.Pattern;

import com.clearnlp.util.PatternUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public interface PatternConst
{
	Pattern COMMA		= Pattern.compile(StringConst.COMMA);
	Pattern COLON		= Pattern.compile(StringConst.COLON);
	Pattern HYPHEN		= Pattern.compile(StringConst.HYPHEN);
	Pattern SEMICOLON	= Pattern.compile(StringConst.SEMICOLON);
	Pattern UNDERSCORE	= Pattern.compile(StringConst.UNDERSCORE);
	
	Pattern SPACE		= Pattern.compile(StringConst.SPACE);
	Pattern TAB			= Pattern.compile(StringConst.TAB);
	Pattern WHITESPACES	= Pattern.compile("\\s+");
	Pattern PUNCTUATION	= Pattern.compile("\\p{Punct}");

	Pattern DIGITS		= Pattern.compile("\\d+");
	Pattern DIGITS_ONLY	= Pattern.compile("^\\d+$");
	Pattern DIGITS_LIKE	= PatternUtils.createORPattern("\\d%","\\$\\d","^\\.\\d","\\d\\.\\d","\\d,\\d","\\d:\\d","\\d-\\d","\\d\\/\\d");
	
	jregex.Pattern URL	= new jregex.Pattern("((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[.\\!\\/\\\\w]*))?|(\\w+\\.)+(com|edu|gov|int|mil|net|org|biz)$)");
}