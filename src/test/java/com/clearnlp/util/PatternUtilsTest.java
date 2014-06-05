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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

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
		assertTrue(p.matcher("A").find());
		assertTrue(p.matcher("B").find());
		assertTrue(p.matcher("aA").find());
		assertTrue(p.matcher("Bb").find());
		
		p = PatternUtils.createClosedORPattern("A", "B");
		assertTrue(p.matcher("A").find());
		assertTrue(p.matcher("B").find());
		assertFalse(p.matcher("aA").find());
		assertFalse(p.matcher("Bb").find());
	}
	
	@Test
	public void testContainsPunctuation()
	{
		assertFalse(PatternUtils.containsPunctuation("ab"));
		assertTrue(PatternUtils.containsPunctuation("$ab"));
		assertTrue(PatternUtils.containsPunctuation("a-b"));
		assertTrue(PatternUtils.containsPunctuation("ab#"));
		assertTrue(PatternUtils.containsPunctuation("$-#"));
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
	
//	@Test
//	public void testCollapsePunctuation()
//	{
//		String[] org = {"...","!!!","???","---","***","===","~~~",",,,",".!?-*=~,","..!!??--**==~~,,","....!!!!????----****====~~~~,,,,"};
//		String[] rep = {"..","!!","??","--","**","==","~~",",,",".!?-*=~,","..!!??--**==~~,,","..!!??--**==~~,,"};
//		int i, size = org.length;
//		
//		for (i=0; i<size; i++)
//			assertEquals(rep[i], PatternUtils.collapsePunctuation(org[i]));
//	}
	
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
	public void testContainsURL()
	{
		String s;
		
		s = "http://www.clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "https://www-01.clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "www.clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "wiki.clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "clearnlp.com:8080";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "clearnlp.co.kr";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "www.clearnlp.com/wiki";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "www.clearnlp.com:8080/wiki";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "id@clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "id:pw@clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "id:@clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "mailto:support@clearnlp.com";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "255.248.27.1";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "http://127.0.0.1";
		assertTrue(PatternUtils.containsHyperlink(s));
		
		s = "http://www.clearnlp.com/watch?v=IAaDVOd2sRQ";
		assertTrue(PatternUtils.containsHyperlink(s));
	}
}
