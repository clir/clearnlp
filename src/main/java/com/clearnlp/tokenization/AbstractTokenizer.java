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
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;

import com.clearnlp.constant.PatternConst;
import com.clearnlp.constant.StringConst;
import com.clearnlp.dictionary.DTAbbreviation;
import com.clearnlp.dictionary.DTCurrency;
import com.clearnlp.dictionary.DTHtml;
import com.clearnlp.util.CharUtils;
import com.clearnlp.util.StringUtils;
import com.google.common.collect.Lists;

/**
 * @since 1.1.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractTokenizer
{
	private DTCurrency d_currency;
	private DTHtml     d_html;
	
	private DTAbbreviation d_abbreviation;
	
	public AbstractTokenizer(boolean html)
	{
		if (html) d_html = new DTHtml();
		d_currency = new DTCurrency(); 
	}
	
	/**
	 * Replaces HTML tags to characters (e.g., "&nbsp;", "&quot;").
	 * Tokenizes white-spaces.
	 * Preserves hyperlinks (e.g., "http://www.clearnlp.com").
	 * Preserves emoticons (e.g., ":-")).
	 * Preserves consecutive finals (e.g., "...", ".?!").
	 * Preserves consecutive separators (e.g., ",,,", "---").
	 */
	public List<String> tokenize(String s)
	{
		if (d_html != null) s = d_html.replace(s);
		List<String> tokens = tokenizeEdge(s);
		
		
		return tokens;
	}
	
	/** Called by {@link #tokenize(String)}. */
	private List<String> tokenizeEdge(String s)
	{
		int eIndex, bIndex = 0, len = s.length();
		List<String> tokens = Lists.newArrayList();
		char[] cs = s.toCharArray();
		
		for (eIndex=0; eIndex<len; eIndex++)
		{
			if (CharUtils.isWhiteSpace(cs[eIndex]))
			{
				addTokens(tokens, StringUtils.substring(s, bIndex, eIndex));
				bIndex = eIndex + 1;
			}
		}
		 
		addTokens(tokens, StringUtils.substring(s, bIndex, eIndex));
		return tokens;
	}
	
	/**
	 * Called by {@link #tokenizeEdge(String)}.
	 * @param startIndex inclusive.
	 * @param endIndex exclusive.
	 */
	private void addTokens(List<String> tokens, String s)
	{
		if (s.equals(StringConst.EMPTY)) return;
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
		
		if (bIndex > 0)		bIndex = adjustFirstNonSymbolIndex(tokens, cs, bIndex, t);
		if (eIndex < len)	eIndex = adjustLastNonSymbolIndex (tokens, cs, eIndex, t);
		
		if (bIndex > 0)
			tokenizeSymbols(tokens, s.substring(0, bIndex));	
		
		if (bIndex == 0 && eIndex == len)
			tokenizeWords(tokens, s);
		else
			tokenizeWords(tokens, s.substring(bIndex, eIndex));
		
		if (eIndex < len)
			tokenizeSymbols(tokens, s.substring(eIndex, len));
	}

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
	protected int adjustFirstNonSymbolIndex(List<String> tokens, char[] cs, int beginIndex, String t)
	{
		char sym = cs[beginIndex-1], curr = cs[beginIndex];
		
		if (CharUtils.isPreDigitSymbol(sym) && CharUtils.isDigit(curr))	// -1, .1, +1
			beginIndex--;
		else if ((sym == '@' || sym == '#') && CharUtils.isAlphabet(curr))	// @A, #A
			beginIndex--;
		else if (sym == '\'' && d_abbreviation.isAbbreviationStartingWithApostrophe(t.toLowerCase()))
			beginIndex--;
			
		return beginIndex;
	}

	/** Called by {@link #addTokensAux(List, String)}. */
	protected int adjustLastNonSymbolIndex(List<String> tokens, char[] cs, int endIndex, String t)
	{
		char sym = cs[endIndex];
		
		if (sym == '$')
		{
			if (d_currency.isCurrencyDollar(t))
				endIndex++;
		}
		else if (sym == '.')
		{
			if (d_abbreviation.isAbbreviationEndingWithPeriod(t) && (endIndex+1 == cs.length || !CharUtils.isFinalMark(cs[endIndex+1])))
				endIndex++;
			else if (endIndex+1 < cs.length && CharUtils.isSeparatorMark(cs[endIndex+1]))
				endIndex++;
		}
		
		return endIndex;
	}
	
	private void tokenizeSymbols(List<String> tokens, String s)
	{
		if (s.length() == 1)
		{
			tokens.add(s);
			return;
		}
		
		int i, leftBound = 0, rightBound = s.length();
		Deque<String> stack = new ArrayDeque<>();
		char[] cs = s.toCharArray();
		char c;
		
		for (i=0; i<rightBound; i=leftBound)
		{
			c = cs[i];
			
			if (CharUtils.isFinalMark(c))
				leftBound = getSpanIndexLR(cs, i, rightBound, true);
			else if (CharUtils.isBracket(c) || CharUtils.isSeparatorMark(c) || CharUtils.isQuotationMark(c))
				leftBound = getSpanIndexLR(cs, i, rightBound, false);
			else
			{
				leftBound = i;
				break;
			}
			
			tokens.add(s.substring(i, leftBound));
		}
		
		for (i=rightBound-1; i>leftBound; i=rightBound-1)
		{
			c = cs[i];
			
			if (CharUtils.isFinalMark(c))
				rightBound = getSpanIndexRL(cs, i, leftBound, true);
			else if (CharUtils.isBracket(c) || CharUtils.isSeparatorMark(c) || CharUtils.isQuotationMark(c))
				rightBound = getSpanIndexRL(cs, i, leftBound, false);
			else
			{
				rightBound = i+1;
				break;
			}
			
			stack.push(s.substring(rightBound, i+1));
		}
		
		if (leftBound < rightBound)
			tokens.add(s.substring(leftBound, rightBound));
		
		while (!stack.isEmpty())
			tokens.add(stack.pop());
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
	
	private void tokenizeWords(List<String> tokens, String s)
	{
		int i, beginIndex = 0, endIndex, len = s.length();
		char[] cs = s.toCharArray();
		char c;
		
		for (i=0; i<len; i++)
		{
			c = cs[i];
			
			// ~ & - + = 
			if (c == ';' || c == '"' || c == '|' || c == '=' || c == '~' || c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']')
				tokenize(i);
			else if (c == '.' || c == '?' || c == '!')
				tokenize(i);
			
			switch (cs[i])
			{
			case ',' : if (!isSkipComma(cs, i)) tokenize(i); break; 
			case ':' : if (!isSkipColon(cs, i)) tokenize(i); break;
			case '-' : if (!isSkipHyphen(cs, i)) tokenize(i); break;
			case '&' : if (!isSkipAmpersand(cs, i)) tokenize(i); break;
			case '/' : if (!isSkipSlash(cs, i)) tokenize(i); break;
			case '\\': if (!isSkipSlash(cs, i)) tokenize(i); break;
			case '\'': if (!isSkipApostrophe(cs, i)) tokenize(i); break;
			}
		}
	}
	
	
	
	private boolean isSkipComma(char[] cs, int index)
	{
		int len = cs.length;
		
		if (0 <= index-1 && index+3 < len && (index+4 == len || !CharUtils.isDigit(cs[index+4])))
			return CharUtils.isDigit(cs[index-1]) && CharUtils.isDigit(cs[index+1]) && CharUtils.isDigit(cs[index+2]) && CharUtils.isDigit(cs[index+3]);
		
		return false;
	}
	
	private boolean isSkipHyphen(char[] cs, int index)
	{
		return false;
	}
	
	private boolean isSkipColon(char[] cs, int index)
	{
		return false;
	}
	
	private boolean isSkipSlash(char[] cs, int index)
	{
		return false;
	}
	
	private boolean isSkipApostrophe(char[] cs, int index)
	{
		return false;		
	}
	
	private boolean isSkipAmpersand(char[] cs, int index)
	{
		return false;
	}
	
	private int tokenize(int index)
	{
		return 3;
	}
	
//	-------------------------------- Booleans -------------------------------- 
	
	protected boolean isSymbol(char c)
	{
		return CharUtils.isPunctuation(c) ||
			   CharUtils.isGeneralPunctuation(c) ||
			   CharUtils.isCurrency(c) ||
			   CharUtils.isArrow(c);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private final String EDGE = "(\\(|\\)|\\[|\\]|\\{|\\}|<|>|,|;|`|'|\")+";
//	
//	private final Pattern P_PRE  = Pattern.compile("^(\\p{Punct})*(\\(|\\[|\\{|<`|'|\")+");
//	private final Pattern P_POST = Pattern.compile("\\(|\\)|\\[|\\]|\\{|\\}|<|>|,|;|`|'|\"");
//	
//	Pattern P_APOSTROPHY = Pattern.compile("(\\p{Alnum})(\\')(s|d|m|z|t|ll|re|ve|nt)");
//	
//	
//	private NumberProtector protector_number = null;
//	private NumberProtector protector_dollar = null;
//	private NumberProtector protector_apostrophy = null;
//	private NumberProtector protector_filename = null;
//	
////	protected final List<ObjectCharPair<String>> PROTECT; 
////	private final CharObjectHashMap<String> M_PROTECT;
//	
//	protected Set<String> s_protectedForms;
//	protected boolean     b_twit = false;
//	
//	
//	protected DTEmoticon d_emoticon;
//	
//	protected AbstractTokenMatcher m_finals;
//	protected AbstractTokenMatcher m_separators;
//	protected AbstractTokenMatcher m_dot;
//	
//	
	
//	
//	
//	
//	private final Pattern[] PROTECT_NUMBER;
	
//	public AbstractTokenizer(boolean html, boolean unicode)
//	{
//		if (html)		d_html    = new DTHtml();
//		if (unicode)	d_unicode = new DTUnicode();
//		
//		
//		
//		PROTECT = initProtects();
//		M_PROTECT = initProtectMap(PROTECT);
//		
//		s_protectedForms = Sets.newHashSet();
//		d_emoticon = new DTEmoticon();
//		m_finals = new TokenMatcher0(PatternConst.PUNCT_FINALS);
//		m_separators = new TokenMatcher0(PatternConst.PUNCT_SEPARATORS);
//		m_dot = new TokenMatcher12(Pattern.compile("(\\p{Alnum}\\.)(\\p{Punct}+)"));
//		
//		
//		
//	}
	
	
	
	
//	private final String PUNCT_FRONT = "`|'|\"|\\(|\\{|\\[|<"; 
	
//	private List<ObjectIntPair<Pattern>> initPatterns()
//	{
//		ArrayList<ObjectIntPair<Pattern>> list = Lists.newArrayList();
//		
//		// ,;|/\"[]{}()&
//		list.add(new ObjectIntPair<Pattern>(Pattern.compile("(\\d)(,)(\\d\\d\\d)(?!\\d)"), 2));
//		list.add(new ObjectIntPair<Pattern>(Pattern.compile("(\\d)(\\/)(\\d)"), 2));
//		
//		
//		
//		Pattern.compile("^(\\p{Punct})*(-|\\+|\\.)(\\d)");		// -1, +1, .1, (-1)
//		Pattern.compile("^(\\p{Punct})*(@|#)(\\p{Alpha})");		// @A, #A, (@A)
//		Pattern.compile("^(\\p{Punct})+");
//		
//		Pattern.compile("^(AU|B|BB|BM|BN|BS|BZ|C|CA|FJ|HK|JM|JY|KY|LR|NA|NT|NZ|SB|SG|US|USD|XC|ZB)(\\$)(\\d|\\p{Punct})", Pattern.CASE_INSENSITIVE);	// US$0, US$.
//		Pattern.compile("(\\.)(\\p{Punct})*$");
//		Pattern.compile("(\\p{Punct})+$");
//		
//		
//		
//		list.trimToSize();
//		return list;
//	}
	
	
	
	
	
	
//	/**
//	 * Replaces non-UTF8 characters to UTF8 characters (e.g., smart quotes).
//	 * Replaces HTML tags (e.g., "&nbsp;", "&quot;").
//	 * Tokenizes white-spaces.
//	 * Preserves emoticons (e.g., ":-")).
//	 * Preserves hyperlinks (e.g., "http://www.clearnlp.com").
//	 * Preserves consecutive finals (e.g., "...", ".?!").
//	 * Preserves consecutive separators (e.g., ",,,", "---").
//	 */
//	public List<String> tokenize(String s)
//	{
//		if (d_html != null) s = d_html.replace(s);
//		List<String> tokens = tokenizeInit(s);
//		
//		
////		@#$.
////		IN: ,
////		Separate: ,?
////		<>?;"{}[]|\\`
////		Apostrophe: 's
////		Slash: /\d{2}?\d{1,2}
////		Hyphen: -\d{2,4} affix
////		Colon: :\d{2}
//		
//		
//		m_finals.match(tokens);
//		m_separators.match(tokens);
//		m_dot.match(tokens);
//		replace(tokens, protector_number);
//		replace(tokens, protector_dollar);
//		replace(tokens, protector_apostrophy);
//		replace(tokens, protector_filename);
//		
//		return tokens;
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private List<ObjectCharPair<String>> initProtects()
//	{
//		ArrayList<ObjectCharPair<String>> list = Lists.newArrayList();
//		
//		list.add(new ObjectCharPair<String>(" d", '.'));
//		list.add(new ObjectCharPair<String>(" h", '-'));
//		list.add(new ObjectCharPair<String>(" a", '\''));
//		list.add(new ObjectCharPair<String>(" m", '&'));
//		
//		list.trimToSize();
//		return list;
//	}
//	
//	private CharObjectHashMap<String> initProtectMap(List<ObjectCharPair<String>> list)
//	{
//		CharObjectHashMap<String> map = new CharObjectHashMap<>();
//		
//		for (ObjectCharPair<String> p : list)
//			map.put(p.c, p.o);
//		
//		return map;
//	}
//	
//	private void init()
//	{
//		String PRE = "`|!|#|$|%|(|)|[|]|{|}|<|>|;|'|\"|<|>|,|\\?";
//	}
//	
//	
//	/**
//	 * @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}.
//	 * @return a list of token in the specific reader.
//	 */
//	public List<String> getTokenList(InputStream in)
//	{
//		BufferedReader reader = IOUtils.createBufferedReader(in);
//		ArrayList<String> tokens = Lists.newArrayList();
//		String line;
//		
//		try
//		{
//			while ((line = reader.readLine()) != null)
//				tokens.addAll(getTokenList(line));
//		}
//		catch (IOException e) {e.printStackTrace();}
//		
//		tokens.trimToSize();
//		return tokens;
//	}
//	
//	/** @return a list of tokens from the specific string. */
//	abstract public List<String> getTokenList(String rawText);
//	
//	public void setTwitter(boolean isTwitter)
//	{
//		b_twit = isTwitter;
//	}
//	
//	public void addProtectedForm(String form)
//	{
//		s_protectedForms.add(form);
//	}
//	
//	public void removeProtectedForm(String form)
//	{
//		s_protectedForms.remove(form);
//	}
//	
//	protected void replace(List<String> tokens, AbstractReplacer replacer)
//	{
//		for (String token : tokens)
//		{
//			if (!token.isProtect())
//				token.setWordForm(replacer.replace(token.getWordForm()));
//		}
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	abstract protected class AbstractTokenMatcher
//	{
//		protected final Pattern PATTERN;
//		
//		public AbstractTokenMatcher(Pattern pattern)
//		{
//			PATTERN = pattern;
//		}
//		
//		public void match(List<String> tokens)
//		{
//			int i, size = tokens.size();
//			String token;
//			
//			for (i=0; i<size; i++)
//			{
//				token = tokens.get(i);
//				
//				if (!token.isProtect())
//				{
//					i = addTokens(tokens, token.getWordForm(), i);
//					size = tokens.size();
//				}
//			}
//		}
//		
//		private int addTokens(List<String> tokens, String form, int index)
//		{
//			Matcher m = PATTERN.matcher(form);
//			int last = 0, curr;
//			
//			while (m.find())
//			{
//				curr = m.start();
//				
//				if (last < curr)
//					tokens.add(index++, new String(form.substring(last, curr)));
//				
//				last = m.end();
//				index = addMatch(tokens, m, index);
//			}
//			
//			if (last < form.length())
//				tokens.add(index++, new String(form.substring(last)));
//			
//			return index;
//		}
//
//		abstract protected int addMatch(List<String> tokens, Matcher m, int index);
//	}
//
//	protected class TokenMatcher0 extends AbstractTokenMatcher
//	{
//		public TokenMatcher0(Pattern pattern)
//		{
//			super(pattern);
//		}
//
//		@Override
//		protected int addMatch(List<String> tokens, Matcher m, int index)
//		{
//			tokens.add(index++, new String(m.group(0), true));
//			return index;
//		}
//	}
//	
//	protected class TokenMatcher12 extends AbstractTokenMatcher
//	{
//		public TokenMatcher12(Pattern pattern)
//		{
//			super(pattern);
//		}
//		
//		@Override
//		protected int addMatch(List<String> tokens, Matcher m, int index)
//		{
//			tokens.add(index++, new String(m.group(1)));
//			tokens.add(index++, new String(m.group(2)));
//			return index;
//		}
//	}
}

