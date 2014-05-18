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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.clearnlp.constant.PatternConst;
import com.clearnlp.util.pair.Pair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class PatternUtils implements PatternConst
{
	static public final String META_URL = "#url#";
	
//	====================================== Getters ======================================

	static public Pattern createClosedPattern(String regex)
	{
		return Pattern.compile("^(?:"+regex+")$");
	}
	
	static public Pattern createClosedORPattern(String... regex)
	{
		return createClosedPattern(createORString(regex));
	}
	
	static public Pattern createORPattern(String... regex)
	{
		return Pattern.compile(createORString(regex));
	}
		
	static public String createORString(String... regex)
	{
		StringBuilder build = new StringBuilder();
		
		for (String r : regex)
		{
			build.append("|");
			build.append(r);
		}
		
		return build.substring(1);		
	}
	
	/** @return {@code null} if not exists. */
	static public String getGroup(Pattern pattern, String str, int index)
	{
		Matcher m = pattern.matcher(str);
		return m.find() ? m.group(index) : null;
	}
	
//	====================================== Booleans ======================================
	
	static public boolean containsHyperlink(String s)
	{
		return HYPERLINK.matcher(s).find();
	}
	
	static public boolean containsEmoticon(String s)
	{
		return EMOTICON.matcher(s).find();
	}
	
	/** @return {@code true} if the specific string contains only digits. */
	static public boolean containsPunctuation(String s)
	{
		return PUNCT.matcher(s).find();
	}

//	====================================== Digits ======================================
	
	/** Collapses all digit-like characters in the specific word-form to {@code "0"}. */
	static public String collapseDigits(String form)
	{
		form = DIGITS_LIKE.matcher(form).replaceAll("0");
		return DIGITS.matcher(form).replaceAll("0");
	}
	
//	====================================== Punctuation ======================================
	
//	/** Collapses redundant punctuation in the specific word-form (e.g., {@code "!!!" -> "!!"}). */
//	static public String collapsePunctuation(String form)
//	{
//		return R_PUNCT2.replace(form);
//	}
//	
//	/** Called by {@link #collapsePunctuation(String)}. */
//	static final private Replacer R_PUNCT2 = new jregex.Pattern("\\.{2,}|\\!{2,}|\\?{2,}|\\-{2,}|\\*{2,}|\\={2,}|\\~{2,}|\\,{2,}").replacer(new Substitution()
//	{
//		public void appendSubstitution(MatchResult match, TextBuffer dest)
//		{
//			char c = match.group(0).charAt(0);
//			dest.append(c);
//			dest.append(c);
//		}
//	});

	/** Reverts coded brackets to their original forms (e.g., from {@code "-LBR-"} to {@code "("}). */
	static public String revertBrackets(String form)
	{
		for (Pair<Pattern,String> p : L_BRACKETS)
			form = p.o1.matcher(form).replaceAll(p.o2);
		
		return form;
	}
	
	/** Called by {@link #revertBrackets(String)}. */
	@SuppressWarnings("serial")
	static private final List<Pair<Pattern, String>> L_BRACKETS = new ArrayList<Pair<Pattern,String>>(6)
	{{
		add(new Pair<Pattern,String>(Pattern.compile("-LRB-"), "("));
		add(new Pair<Pattern,String>(Pattern.compile("-RRB-"), ")"));
		add(new Pair<Pattern,String>(Pattern.compile("-LSB-"), "["));
		add(new Pair<Pattern,String>(Pattern.compile("-RSB-"), "]"));
		add(new Pair<Pattern,String>(Pattern.compile("-LCB-"), "{"));
		add(new Pair<Pattern,String>(Pattern.compile("-RCB-"), "}"));
	}};
	
//	====================================== URL ======================================

	static public boolean containsURL(String str)
	{
		return HYPERLINK.matcher(str).find();
	}
	
//	====================================== Simplify ======================================

	/**
	 * @return a simplified form of the specific word-form.
	 * @see #containsURL(String)
	 * @see #collapseDigits(String)
	 * @see #collapsePunctuation(String)
	 */
	static public String getSimplifiedWordForm(String form)
	{
		if (containsURL(form))	return META_URL;
		
		form = collapseDigits(form);
//		form = collapsePunctuation(form);
		
		return form;
	}
	
	static public String getSimplifiedLowercaseWordForm(String form)
	{
		return StringUtils.toLowerCase(getSimplifiedWordForm(form));
	}
}