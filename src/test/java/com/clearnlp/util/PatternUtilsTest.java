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

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import com.clearnlp.util.PatternUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class PatternUtilsTest
{
	@Test
	public void testGetORPattern()
	{
		Pattern p; 
		
		p = PatternUtils.createORPattern("A", "B");
		assertEquals(true, p.matcher("A").find());
		assertEquals(true, p.matcher("B").find());
		assertEquals(true, p.matcher("aA").find());
		assertEquals(true, p.matcher("Bb").find());
		
		p = PatternUtils.createClosedORPattern("A", "B");
		assertEquals(true , p.matcher("A").find());
		assertEquals(true , p.matcher("B").find());
		assertEquals(false, p.matcher("aA").find());
		assertEquals(false, p.matcher("Bb").find());
	}
	
	@Test
	public void testContainsOnlyDigits()
	{
		assertEquals(true , PatternUtils.containsOnlyDigits("12"));
		assertEquals(false, PatternUtils.containsOnlyDigits("a1"));
		assertEquals(false, PatternUtils.containsOnlyDigits("1b"));
		assertEquals(false, PatternUtils.containsOnlyDigits("1-2"));
	}
	
	@Test
	public void testContainsPunctuation()
	{
		assertEquals(false, PatternUtils.containsPunctuation("ab"));
		assertEquals(true , PatternUtils.containsPunctuation("$ab"));
		assertEquals(true , PatternUtils.containsPunctuation("a-b"));
		assertEquals(true , PatternUtils.containsPunctuation("ab#"));
		assertEquals(true , PatternUtils.containsPunctuation("$-#"));
	}
	
	@Test
	public void testCollapseDigits()
	{
		String[] arr0 = {"10%","$10",".01","97.33","1,000,000","10:30","10-20","10/20","$12.34,56:78-90/12%"};
		
		for (String s : arr0)
			assertEquals("0", PatternUtils.collapseDigits(s));
		
		assertEquals("A.0", PatternUtils.collapseDigits("A.12"));
		assertEquals("A:0", PatternUtils.collapseDigits("A:12"));
		assertEquals("$A0", PatternUtils.collapseDigits("$A12"));
		assertEquals("A0$", PatternUtils.collapseDigits("A12$"));
		assertEquals("A0" , PatternUtils.collapseDigits("A12%"));
		assertEquals("%A0", PatternUtils.collapseDigits("%A12"));
	}
	
	@Test
	public void testCollapsePunctuation()
	{
		String[] org = {"...","!!!","???","---","***","===","~~~",",,,",".!?-*=~,","..!!??--**==~~,,","....!!!!????----****====~~~~,,,,"};
		String[] rep = {"..","!!","??","--","**","==","~~",",,",".!?-*=~,","..!!??--**==~~,,","..!!??--**==~~,,"};
		int i, size = org.length;
		
		for (i=0; i<size; i++)
			assertEquals(rep[i], PatternUtils.collapsePunctuation(org[i]));
	}
	
	@Test
	public void testRevertBrackets()
	{
		String[] org = {"-LRB-","-RRB-","-LCB-","-RCB-","-LSB-","-RSB-","-LRB--RRB-","-LCB--RCB-","-LSB--RSB-"};
		String[] rep = {"(",")","{","}","[","]","()","{}","[]"};
		int i, size = org.length;
		
		for (i=0; i<size; i++)
			assertEquals(rep[i], PatternUtils.revertBrackets(org[i]));
	}

	@Test
	public void test()
	{
		assertEquals(true, PatternUtils.containsURL("http://www.clearnlp.com"));
		assertEquals(true, PatternUtils.containsURL("www.clearnlp.com"));
		assertEquals(true, PatternUtils.containsURL("clearnlp.com"));
		assertEquals(true, PatternUtils.containsURL("mailto:jinho@clearnlp.com"));
		assertEquals(true, PatternUtils.containsURL("jinho@clearnlp.com"));
		
		assertEquals(false, PatternUtils.containsURL("index.html"));
	}
}