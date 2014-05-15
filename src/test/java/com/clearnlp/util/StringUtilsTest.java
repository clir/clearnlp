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

import org.junit.Test;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class StringUtilsTest
{
	@Test
	public void testToLowerUpperCase()
	{
		StringBuilder build = new StringBuilder();
		String ascii;
		int i;
		
		for (i=0; i<128; i++) build.append((char)i);
		ascii = build.toString();

		assertEquals(ascii.toLowerCase(), StringUtils.toLowerCase(ascii));
		assertEquals(ascii.toUpperCase(), StringUtils.toUpperCase(ascii));

		build = new StringBuilder();
		for (i=128; i<256; i++) build.append((char)i);
		ascii = build.toString();
		
		assertEquals(StringUtils.toLowerCase(ascii), StringUtils.toLowerCase(StringUtils.toUpperCase(ascii)));
		assertEquals(StringUtils.toUpperCase(ascii), StringUtils.toUpperCase(StringUtils.toLowerCase(ascii)));
	}
	
	@Test
	public void testStartsWithAny()
	{
		String[] suffixes = {"ab","cd","ef"};

		assertTrue(StringUtils.startsWithAny("ab", suffixes));
		assertTrue(StringUtils.startsWithAny("cd", suffixes));
		assertTrue(StringUtils.startsWithAny("ef", suffixes));
		
		assertTrue(StringUtils.startsWithAny("ab0", suffixes));
		assertTrue(StringUtils.startsWithAny("cd0", suffixes));
		assertTrue(StringUtils.startsWithAny("ef0", suffixes));
		
		assertFalse(StringUtils.startsWithAny("0ab", suffixes));
		assertFalse(StringUtils.startsWithAny("0cd", suffixes));
		assertFalse(StringUtils.startsWithAny("0ef", suffixes));
		
		assertFalse(StringUtils.startsWithAny("a", suffixes));
		assertFalse(StringUtils.startsWithAny("c", suffixes));
		assertFalse(StringUtils.startsWithAny("e", suffixes));
	}
	
	@Test
	public void testEndsWithAny()
	{
		String[] suffixes = {"ab","cd","ef"};

		assertTrue(StringUtils.endsWithAny("ab", suffixes));
		assertTrue(StringUtils.endsWithAny("cd", suffixes));
		assertTrue(StringUtils.endsWithAny("ef", suffixes));
		
		assertTrue(StringUtils.endsWithAny("0ab", suffixes));
		assertTrue(StringUtils.endsWithAny("0cd", suffixes));
		assertTrue(StringUtils.endsWithAny("0ef", suffixes));
		
		assertFalse(StringUtils.endsWithAny("ab0", suffixes));
		assertFalse(StringUtils.endsWithAny("cd0", suffixes));
		assertFalse(StringUtils.endsWithAny("ef0", suffixes));
		
		assertFalse(StringUtils.endsWithAny("b", suffixes));
		assertFalse(StringUtils.endsWithAny("d", suffixes));
		assertFalse(StringUtils.endsWithAny("f", suffixes));
	}
	
	@Test
	public void testRegionMatches()
	{
		char[] c = "abcd".toCharArray();
		char[] d = "bc".toCharArray();
		
		assertFalse(CharUtils.regionMatches(c, d, 0));
		assertTrue (CharUtils.regionMatches(c, d, 1));
		assertFalse(CharUtils.regionMatches(c, d, 2));
		assertFalse(CharUtils.regionMatches(c, d, 3));	
	}
}