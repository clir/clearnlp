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

		assertEquals(true, StringUtils.startsWithAny("ab", suffixes));
		assertEquals(true, StringUtils.startsWithAny("cd", suffixes));
		assertEquals(true, StringUtils.startsWithAny("ef", suffixes));
		
		assertEquals(true, StringUtils.startsWithAny("ab0", suffixes));
		assertEquals(true, StringUtils.startsWithAny("cd0", suffixes));
		assertEquals(true, StringUtils.startsWithAny("ef0", suffixes));
		
		assertEquals(false, StringUtils.startsWithAny("0ab", suffixes));
		assertEquals(false, StringUtils.startsWithAny("0cd", suffixes));
		assertEquals(false, StringUtils.startsWithAny("0ef", suffixes));
		
		assertEquals(false, StringUtils.startsWithAny("a", suffixes));
		assertEquals(false, StringUtils.startsWithAny("c", suffixes));
		assertEquals(false, StringUtils.startsWithAny("e", suffixes));
	}
	
	@Test
	public void testEndsWithAny()
	{
		String[] suffixes = {"ab","cd","ef"};

		assertEquals(true, StringUtils.endsWithAny("ab", suffixes));
		assertEquals(true, StringUtils.endsWithAny("cd", suffixes));
		assertEquals(true, StringUtils.endsWithAny("ef", suffixes));
		
		assertEquals(true, StringUtils.endsWithAny("0ab", suffixes));
		assertEquals(true, StringUtils.endsWithAny("0cd", suffixes));
		assertEquals(true, StringUtils.endsWithAny("0ef", suffixes));
		
		assertEquals(false, StringUtils.endsWithAny("ab0", suffixes));
		assertEquals(false, StringUtils.endsWithAny("cd0", suffixes));
		assertEquals(false, StringUtils.endsWithAny("ef0", suffixes));
		
		assertEquals(false, StringUtils.endsWithAny("b", suffixes));
		assertEquals(false, StringUtils.endsWithAny("d", suffixes));
		assertEquals(false, StringUtils.endsWithAny("f", suffixes));
	}
}