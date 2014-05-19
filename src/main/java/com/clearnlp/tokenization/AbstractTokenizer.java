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

import com.clearnlp.constant.CharConst;
import com.clearnlp.constant.PatternConst;
import com.clearnlp.constant.StringConst;
import com.clearnlp.dictionary.AbstractDTTokenizer;
import com.clearnlp.dictionary.universal.DTCurrency;
import com.clearnlp.dictionary.universal.DTUnit;
import com.clearnlp.util.CharUtils;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.PatternUtils;
import com.clearnlp.util.StringUtils;
import com.google.common.collect.Lists;

/**
 * @since 1.1.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractTokenizer
{
	private final Pattern P_ABBREVIATION = PatternUtils.createClosedPattern("\\p{Alnum}([\\.|-]\\p{Alnum})*");
	private final Pattern P_YEAR = PatternUtils.createClosedPattern("\\d\\d['|\u2019]?[sS]?");

	private DTCurrency d_currency;
	private DTUnit     d_unit;
	
	public AbstractTokenizer()
	{
		d_currency = new DTCurrency();
		d_unit     = new DTUnit();
	}
	
//	----------------------------------- Tokenize -----------------------------------
	
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
		 
		if (bIndex < eIndex) addTokens(tokens, s.substring(bIndex));
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
	
	private boolean isSymbol(char c)
	{
		return CharUtils.isPunctuation(c) ||
			   CharUtils.isGeneralPunctuation(c) ||
			   CharUtils.isCurrency(c) ||
			   CharUtils.isArrow(c);
	}
	
	/** Called by {@link #addTokensAux(List, String)}. */
	private int adjustFirstNonSymbolIndex(char[] cs, int beginIndex, String t)
	{
		char sym = cs[beginIndex-1], curr = cs[beginIndex];
		int gap;
		
		if ((gap = adjustFirstNonSymbolGap(cs, beginIndex, t)) > 0)
		{
			beginIndex -= gap;
		}
		else if (CharUtils.isPreDigitSymbol(sym))
		{
			if (CharUtils.isDigit(curr)) beginIndex--;		// -1, .1, +1
		}
		else if ((sym == CharConst.AT || sym == CharConst.POUND))
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
		{
			endIndex += gap;
		}
		else if (sym == CharConst.DOLLAR)
		{
			if (d_currency.isCurrencyDollar(t)) endIndex++;
		}
		else if (sym == CharConst.PERIOD)
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
	
//	----------------------------------- Tokenize symbols -----------------------------------
	
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
	
//	----------------------------------- Tokenize words -----------------------------------
	
	/** Called by {@link #addTokensAux(List, String)}. */
	private void tokenizeWords(List<String> tokens, String s)
	{
		int i, beginIndex = 0, endIndex, len = s.length() - 1;
		char[] cs = s.toCharArray();
		
		for (i=1; i<len; i++)
		{
			if (preserveSymbolInBetween(cs, i) || isCommaInDigits(cs, i) || isSymbolInAlphabets(cs, i) || isSymbolInDigits(cs, i))
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
		char[] lcs = s.toCharArray();
		String lower = CharUtils.toLowerCase(lcs) ? new String(lcs) : s;
		
		if (!tokenizeWordsMore(tokens, s, lower, lcs) && !tokenize(tokens, s, lower, lcs, d_currency) && !tokenize(tokens, s, lower, lcs, d_unit))
			tokens.add(s);
	}
	
	abstract protected boolean preserveSymbolInBetween(char[] cs, int index);
	abstract protected boolean tokenizeWordsMore(List<String> tokens, String original, String lower, char[] cs);
	
	protected boolean tokenize(List<String> tokens, String original, String lower, char[] lcs, AbstractDTTokenizer tokenizer)
	{
		String[] t = tokenizer.tokenize(original, lower, lcs);
		
		if (t != null)
		{
			DSUtils.addAll(tokens, t);
			return true;
		}
		
		return false;
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isCommaInDigits(char[] cs, int index)
	{
		if (cs[index] == CharConst.COMMA)
			return (0 <= index-1 && index+3 < cs.length) && (index+4 == cs.length || !CharUtils.isDigit(cs[index+4])) && CharUtils.isDigit(cs[index-1]) && CharUtils.isDigit(cs[index+1]) && CharUtils.isDigit(cs[index+2]) && CharUtils.isDigit(cs[index+3]);
		
		return false;
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isSymbolInAlphabets(char[] cs, int index)
	{
		if (cs[index] == CharConst.AMPERSAND)
			return (0 <= index-1 && index+1 < cs.length) && CharUtils.isAlphabet(cs[index-1]) && CharUtils.isAlphabet(cs[index+1]);
		
		return false;
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isSymbolInDigits(char[] cs, int index)
	{
		char c = cs[index];
		
		if (CharUtils.isHyphen(c) || c == CharConst.FW_SLASH || c == CharConst.BW_SLASH)
			return (0 <= index-1 && index+1 < cs.length) && CharUtils.isDigit(cs[index-1]) && CharUtils.isDigit(cs[index+1]);
		
		return false;
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isEllipsis(char[] cs, int index)
	{
		return (index+1 < cs.length) && (cs[index] == CharConst.PERIOD) && (cs[index+1] == CharConst.PERIOD);
	}
	
	/** Called by {@link #tokenizeWords(List, String)}. */
	private boolean isSymbolInBetween(char c)
	{
		return CharUtils.isBracket(c) || CharUtils.isArrow(c) || c == CharConst.PLUS || c == CharConst.EQUAL || c == CharConst.TILDA || c == CharConst.AMPERSAND || c == CharConst.PIPE || c == CharConst.FW_SLASH || c == CharConst.BW_SLASH || c == CharConst.SEMICOLON || c == CharConst.COMMA;
	}
	
//	----------------------------------- Finalize -----------------------------------
	
	/** Called by {@link #tokenize(String)}. */
	private void finalize(List<String> tokens)
	{
		int i, j, size = tokens.size();
		String token, lower;
		
		for (i=0; i<size; i++)
		{
			token = tokens.get(i);
			lower = StringUtils.toLowerCase(token);
			
			if ((j = tokenizeNo(tokens, token, lower, i)) != 0 || (mergeParenthesis(tokens, token, i)) != 0)
			{
				size = tokens.size();
				i += j;
			}
		}
		
		tokenizeLastPeriod(tokens);
	}
	
	/** Called by {@link #finalize()}. */
	private int tokenizeNo(List<String> tokens, String token, String lower, int index)
	{
		if (index+1 < tokens.size() && lower.equals("no.") && CharUtils.isDigit(tokens.get(index+1).charAt(0)))
		{
			tokens.set(index  , StringUtils.trim(token, 1));
			tokens.add(index+1, StringConst.PERIOD);
			return 1;
		}
		
		return 0;
	}
	
	/** Called by {@link #finalize()}. */
	private int mergeParenthesis(List<String> tokens, String token, int index)
	{
		if (token.length() == 1 && 0 <= index-1 && index+1 < tokens.size())
		{
			String prev = tokens.get(index-1);
			String next = tokens.get(index+1);
			
			if (prev.equals(StringConst.LRB) && next.equals(StringConst.RRB))
			{
				tokens.set(index-1, prev+token+next);
				tokens.remove(index);
				tokens.remove(index);
				return -1;
			}
		}
		
		return 0;
	}
	
	/** Called by {@link #finalize()}. */
	private void tokenizeLastPeriod(List<String> tokens)
	{
		int last = tokens.size() - 1;
		String token = tokens.get(last);
		
		if (token.endsWith(StringConst.PERIOD))
		{
			tokens.set(last, StringUtils.trim(token, 1));
			tokens.add(StringConst.PERIOD);
		}
	}
}
