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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.regex.Matcher;

import org.junit.Test;

import com.clearnlp.util.Joiner;
import com.clearnlp.util.constant.PatternConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PatternConstTest
{
//	@Test
//	public void testYearAbbrevation()
//	{
//		System.out.println(PatternConst.YEAR_ABBREVIATION.matcher("90s").find());
//		System.out.println(PatternConst.YEAR_ABBREVIATION.matcher("90s'").find());
//		System.out.println(PatternConst.YEAR_ABBREVIATION.matcher("90's").find());
//		System.out.println(PatternConst.YEAR_ABBREVIATION.matcher("90").find());
//		System.out.println(PatternConst.YEAR_ABBREVIATION.matcher("900").find());
//	}
	
	@Test
	public void test()
	{
		assertEquals("[a, b]", Arrays.toString(PatternConst.COLON.split("a:b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.COMMA.split("a,b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.HYPHEN.split("a-b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.SEMICOLON.split("a;b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.UNDERSCORE.split("a_b")));
	}
	
	@Test
	public void testEndingMarkers()
	{
		Matcher m;
		
		m = PatternConst.PUNCT_FINALS.matcher("a.?!b.c...d??e!!f");
		
		m.find();
		assertEquals(".?!", m.group());
		
		m.find();
		assertEquals("...", m.group());
		
		m.find();
		assertEquals("??", m.group());
		
		m.find();
		assertEquals("!!", m.group());
	}
	
	@Test
	public void testSeparators()
	{
		Matcher m;
		
		m = PatternConst.PUNCT_SEPARATORS.matcher("-*=~,`'");
		assertFalse(m.find());

		String[] s = {"--","***","==","~~~",",,","```","''"};
		m = PatternConst.PUNCT_SEPARATORS.matcher(Joiner.join(s, ""));
		int i, size = s.length;
		
		for (i=0; i<size; i++)
		{
			m.find();
			assertEquals(s[i], m.group());
		}
	}
}