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
package com.clearnlp.dictionary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DTAbbreviationTest
{
	@Test
	public void test()
	{
		DTAbbreviation dt = new DTAbbreviation();

		
		assertTrue(dt.isAbbreviationStartingWithApostrophe("90"));
		assertTrue(dt.isAbbreviationStartingWithApostrophe("90s"));
		assertTrue(dt.isAbbreviationEndingWithPeriod("A"));
		assertTrue(dt.isAbbreviationEndingWithPeriod("1"));
		assertTrue(dt.isAbbreviationEndingWithPeriod("A.1"));
		assertTrue(dt.isAbbreviationEndingWithPeriod("A-1"));

		assertFalse(dt.isAbbreviationStartingWithApostrophe("9"));
		assertFalse(dt.isAbbreviationStartingWithApostrophe("900"));
		assertFalse(dt.isAbbreviationEndingWithPeriod("A1"));
		assertFalse(dt.isAbbreviationEndingWithPeriod("A:1"));
	}
}
