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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.clearnlp.constant.PatternConst;
import com.clearnlp.constant.StringConst;
import com.clearnlp.dictionary.DTCurrency;
import com.clearnlp.dictionary.DTUnit;
import com.clearnlp.util.CharUtils;
import com.clearnlp.util.PatternUtils;
import com.clearnlp.util.StringUtils;
import com.google.common.collect.Lists;

/**
 * @since 1.1.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractTokenizer
{
	private final Pattern P_YEAR = PatternUtils.createClosedPattern("\\d\\d['|\u2019]?[sS]?");
	private final Pattern P_ABBREVIATION = PatternUtils.createClosedPattern("\\p{Alnum}([\\.|-]\\p{Alnum})*");
	
	private DTCurrency d_currency;
	private DTUnit d_unit;
	
	public AbstractTokenizer()
	{
		d_currency = new DTCurrency();
		d_unit = new DTUnit();
	}
	
	public List<String> tokenize(String s)
	{
		ArrayList<String> tokens = Lists.newArrayList();
		int bIndex = 0, eIndex, len = s.length();
		char[] cs = s.toCharArray();
		
		for (eIndex=0; eIndex<len; eIndex++)
		{
			if (CharUtils.isWhiteSpace(cs[eIndex]))
			{
				if (bIndex < eIndex) addTokens(tokens, s.substring(bIndex, eIndex));
				bIndex = eIndex + 1;
			}
		}
		 
		if (bIndex < eIndex) addTokens(tokens, s.substring(bIndex, eIndex));
		finalize(tokens);
		
		tokens.trimToSize();
		return tokens;
	}
	
	/** Called by {@link #tokenizeAux(String)}. */
	private void addTokens(List<String> tokens, String s)
	{
		Matcher m;
		
		if ((m = PatternConst.HYPERLINK.matcher(s)).find() || (m = PatternConst.EMOTICON.matcher(s)).find())
		{
			int bIndex = m.start(), eIndex = m.end();
			
			if (bIndex > 0) addTokensAux(tokens, s.substring(0, bIndex));
			tokens.add(new String(m.group()));
			if (eIndex < s.length()) addTokensAux(tokens, s.substring(eIndex));
		}
		else
			addTokensAux(tokens, s);
	}
	
	/** Called by {@link #addTokens(List, String)}. */
	private void addTokensAux(List<String> tokens, String s)
	{
		char[] cs = s.toCharArray();
		int len = s.length();
		
		int bIndex = getFirstNonSymbolIndex(cs);
		
		if (bIndex == len)
		{
			tokenizeSymbols(tokens, s);
			return;
		}
		
		int eIndex = getLastNonSymbolIndex(cs);
		
		if (bIndex == 0 && eIndex == len)
		{
			tokenizeWords(tokens, s);
			return;
		}
		
		String t = s.substring(bIndex, eIndex);
		
		if (bIndex > 0)		bIndex = adjustFirstNonSymbolIndex(cs, bIndex, t);
		if (eIndex < len)	eIndex = adjustLastNonSymbolIndex (cs, eIndex, t);
		
		if (bIndex > 0)		tokenizeSymbols(tokens, s.substring(0, bIndex));	
							tokenizeWords  (tokens, s.substring(bIndex, eIndex));
		if (eIndex < len)	tokenizeSymbols(tokens, s.substring(eIndex, len));
	}

	/**
	 * Called by {@link #addTokensAux(List, String)}.
	 * @return {@code cs.length} if all characters in {@code cs} are symbols.  
	 */
	private int getFirstNonSymbolIndex(char[] cs)
	{
		int i, len = cs.length;
		
		for (i=0; i<len; i++)
		{
			if (!isSymbol(cs[i]))
				return i;
		}
		
		return i;
	}
	
	/**
	 * Called by {@link #addTokensAux(List, String)}.
	 * @return {@code 0} if all characters in {@code cs} are symbols.  
	 */
	private int getLastNonSymbolIndex(char[] cs)
	{
		int i;
		
		for (i=cs.length-1; i>=0; i--)
		{
			if (!isSymbol(cs[i]))
				return i+1;
		}
		
		return i+1;
	}
	
	/** Called by {@link #addTokensAux(List, String)}. */
	private int adjustFirstNonSymbolIndex(char[] cs, int beginIndex, String t)
	{
		char sym = cs[beginIndex-1], curr = cs[beginIndex];
		int gap;
		
		if ((gap = adjustFirstNonSymbolGap(cs, beginIndex, t)) > 0)
			beginIndex -= gap;
		else if (CharUtils.isPreDigitSymbol(sym))
		{
			if (CharUtils.isDigit(curr)) beginIndex--;		// -1, .1, +1
		}
		else if ((sym == '@' || sym == '#'))
		{
			if (CharUtils.isAlphabet(curr)) beginIndex--;	// @A, #A
		}
		else if (CharUtils.isApostrophe(sym))
		{
			if (P_YEAR.matcher(t).find()) beginIndex--;		// '90, '90s
		}
			
		return beginIndex;
	}
	
	/** Called by {@link #addTokensAux(List, String)}. */
	protected int adjustLastNonSymbolIndex(char[] cs, int endIndex, String t)
	{
		char sym = cs[endIndex];
		int gap;
		
		if ((gap = adjustLastNonSymbolGap(cs, endIndex, t)) > 0)
			endIndex += gap;
		else if (sym == '$')
		{
			if (d_currency.isCurrencyDollar(t)) endIndex++;
		}
		else if (sym == '.')
		{
			if (preservePeriod(cs, endIndex, t)) endIndex++;
		}
		
		return endIndex;
	}
	
	/** Called by {@link #adjustLastNonSymbolGap(char[], int, String)}. */
	private boolean preservePeriod(char[] cs, int endIndex, String t)
	{
		if (P_ABBREVIATION.matcher(t).find())
			return true;
		
		if (endIndex+1 < cs.length && CharUtils.isSeparatorMark(cs[endIndex+1]))
			return true;
		
		int len = t.length();
		return (2 <= len && len <= 5) && CharUtils.containsOnlyConsonants(t);
	}
	
	/** Called by {@link #adjustFirstNonSymbolIndex(char[], int, String)}. */
	abstract protected int adjustFirstNonSymbolGap(char[] cs, int beginIndex, String t);
	/** Called by {@link #adjustLastNonSymbolIndex(char[], int, String)}. */
	abstract protected int adjustLastNonSymbolGap (char[] cs, int endIndex, String t);
	
	/** Called by {@link #addTokensAux(List, String)}. */
	private void tokenizeSymbols(List<String> tokens, String s)
	{
		if (s.length() == 1)
		{
			tokens.add(s);
			return;
		}
		
		int i, f, leftBound = 0, rightBound = s.length();
		Deque<String> stack = new ArrayDeque<>();
		char[] cs = s.toCharArray();
		
		// post: leftBound = the index of the first symbol that shouldn't be tokenized 
		for (i=0; i<rightBound; i=leftBound)
		{
			f = getEdgeSymbolFlag(cs[i]);
			
			if (f == 0)
			{
				leftBound = i;
				break;
			}
			else
			{
				leftBound = getSpanIndexLR(cs, i, rightBound, f == 1);
				tokens.add(s.substring(i, leftBound));
			}
		}
		
		// post: rightBound = the index+1 of the last symbol that shoulnd't be tokenized
		for (i=rightBound-1; i>leftBound; i=rightBound-1)
		{
			f = getEdgeSymbolFlag(cs[i]);
			
			if (f == 0)
			{
				rightBound = i+1;
				break;
			}
			else
			{
				rightBound = getSpanIndexRL(cs, i, leftBound, f == 1);
				stack.push(s.substring(rightBound, i+1));
			}
		}
		
		if (leftBound < rightBound)
			tokens.add(s.substring(leftBound, rightBound));
		
		while (!stack.isEmpty())
			tokens.add(stack.pop());
	}
	
	/** Called by {@link #tokenizeSymbols(List, String)}. */
	private int getEdgeSymbolFlag(char c)
	{
		if (CharUtils.isFinalMark(c))
			return 1;
		else if (CharUtils.isBracket(c) || CharUtils.isSeparatorMark(c) || CharUtils.isQuotationMark(c))
			return 2;
		else
			return 0;
	}
	
	/**
	 * @return the right-most index in the span (exclusive).
	 * Called by {@link #tokenizeSymbols(List, String)}.
	 */
	private int getSpanIndexLR(char[] cs, int index, int rightBound, boolean finalMark)
	{
		char c = cs[index];
		int i;
		
		for (i=index+1; i<rightBound; i++)
		{
			if (!isConsecutive(cs, i, c, finalMark))
				return i;
		}
		
		return i;
	}
	
	/**
	 * @return the left-most index in the span (inclusive).  
	 * Called by {@link #tokenizeSymbols(List, String)}.
	 */
	private int getSpanIndexRL(char[] cs, int index, int leftBound, boolean finalMark)
	{
		char c = cs[index];
		int i;
		
		for (i=index-1; i>leftBound; i--)
		{
			if (!isConsecutive(cs, i, c, finalMark))
				return i+1;
		}
		
		return i+1;
	}
	
	/** Called by {@link #getSpanIndexLR(char[], int, int, boolean)} and {@link #getSpanIndexRL(char[], int, int, boolean)}. */
	private boolean isConsecutive(char[] cs, int index, char c, boolean finalMark)
	{
		return finalMark ? CharUtils.isFinalMark(cs[index]) : c == cs[index];
	}
	
	/** Called by {@link #addTokensAux(List, String)}. */
	private void tokenizeWords(List<String> tokens, String s)
	{
		int i, beginIndex = 0, endIndex, len = s.length() - 1;
		char[] cs = s.toCharArray();
		
		for (i=1; i<len; i++)
		{
			if (preserveSymbolInBetween(cs, i) || isCommaInDigit(cs, i) || isSymbolInAlphabets(cs, i) || isSymbolInDigits(cs, i))
				continue;
			
			if (isEllipsis(cs, i) || isSymbolInBetween(cs[i]))
			{
				endIndex = getSpanIndexLR(cs, i, len, false);
				if (beginIndex < i)	tokenizeWordsAux(tokens, s.substring(beginIndex, i));
				tokens.add(s.substring(i, endIndex));
				beginIndex = endIndex;
				i = endIndex  - 1;
			}
		}
		
		if (beginIndex < s.length())
			tokenizeWordsAux(tokens, s.substring(beginIndex));
	}
	
	private void tokenizeWordsAux(List<String> tokens, String s)
	{
		char[] cs= s.toCharArray();
		String lower = CharUtils.toLowerCase(cs) ? new String(cs) : s;
		
		if (!tokenizeWordsMore(tokens, s, lower, cs) && !tokenizeCurrency(tokens, s, lower, cs) && !tokenizeUnit(tokens, s, lower, cs))
				;
	}
	
	private boolean tokenizeCurrency(List<String> tokens, String original, String lower, char[] cs)
	{
		int len = original.length();
		
		for (String prefix : d_currency.getCurrencySet())
		{
			if (lower.startsWith(prefix))
			{
				int i = prefix.length();
				
				if (i < len && CharUtils.isDigit(cs[i]))
				{
					addTokens(tokens, original, i);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean tokenizeUnit(List<String> tokens, String original, String lower, char[] cs)
	{
		int len = original.length();
		
		for (String suffix : d_unit.getUnitSet())
		{
			if (lower.endsWith(suffix))
			{
				int i = len - suffix.length();
				
				if (0 < i && CharUtils.isDigit(cs[i-1]))
				{	
					addTokens(tokens, original, i);
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected void addTokens(List<String> tokens, String s, int... splits)
	{
		int beginIndex = 0;
		
		for (int split : splits)
		{
			tokens.add(s.substring(beginIndex, split));
			beginIndex = split;
		}
		
		tokens.add(s.substring(beginIndex));
	}
	
	abstract protected boolean preserveSymbolInBetween(char[] cs, int index);
	abstract protected boolean tokenizeWordsMore(List<String> tokens, String original, String lower, char[] cs);
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isCommaInDigit(char[] cs, int index)
	{
		if (cs[index] == ',')
			return (0 <= index-1 && index+3 < cs.length) && (index+4 == cs.length || !CharUtils.isDigit(cs[index+4])) && CharUtils.isDigit(cs[index-1]) && CharUtils.isDigit(cs[index+1]) && CharUtils.isDigit(cs[index+2]) && CharUtils.isDigit(cs[index+3]);
		
		return false;
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isSymbolInAlphabets(char[] cs, int index)
	{
		if (cs[index] == '&')
			return (0 <= index-1 && index+1 < cs.length) && CharUtils.isAlphabet(cs[index-1]) && CharUtils.isAlphabet(cs[index+1]);
		
		return false;
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isSymbolInDigits(char[] cs, int index)
	{
		char c = cs[index];
		
		if (CharUtils.isHyphen(c) || c == '/' || c == '\\')
			return (0 <= index-1 && index+1 < cs.length) && CharUtils.isDigit(cs[index-1]) && CharUtils.isDigit(cs[index+1]);
		
		return false;
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isEllipsis(char[] cs, int index)
	{
		return (index+1 < cs.length) && (cs[index] == '.') && (cs[index+1] == '.');
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isSymbolInBetween(char c)
	{
		return CharUtils.isBracket(c) || c == '~' || c == '&' || c == '|' || c == '/' || c == '\\' || c == ';' || c == ',';
	}
	
//	-------------------------------- Booleans -------------------------------- 
	
	protected boolean isSymbol(char c)
	{
		return CharUtils.isPunctuation(c) ||
			   CharUtils.isGeneralPunctuation(c) ||
			   CharUtils.isCurrency(c) ||
			   CharUtils.isArrow(c);
	}
	
	private void finalize(List<String> tokens)
	{
		String token, lower, prev, next;
		int i, size = tokens.size();
		
		for (i=0; i<size; i++)
		{
			token = tokens.get(i);
			lower = StringUtils.toLowerCase(token);
			
			if (i+1 < size && lower.equals("no.") && CharUtils.isDigit(tokens.get(i+1).charAt(0)))
			{
				tokens.set(i, token.substring(0, token.length()-1));
				tokens.add(i+1, StringConst.PERIOD);
				i++;
				size = tokens.size();
			}
			else if (token.length() == 1 && 0 <= i-1 && i+1 < size)
			{
				prev = tokens.get(i-1);
				next = tokens.get(i+1);
				
				if (prev.equals(StringConst.LRB) && next.equals(StringConst.RRB))
				{
					tokens.set(i-1, prev+token+next);
					tokens.remove(i);
					tokens.remove(i);
					i--;
					size = tokens.size();
				}
			}
		}
	}
}

