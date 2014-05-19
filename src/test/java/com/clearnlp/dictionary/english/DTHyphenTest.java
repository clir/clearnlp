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
package com.clearnlp.dictionary.english;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DTHyphenTest
{
	@Test
	public void test()
	{
		DTHyphen dt = new DTHyphen();
		
		assertTrue(dt.isPrefix("inter"));
		assertTrue(dt.isSuffix("ful"));
		assertTrue(dt.preserveHyphen("inter-connect".toCharArray(), 5));
		assertTrue(dt.preserveHyphen("beauti-ful".toCharArray(), 6));
		assertTrue(dt.preserveHyphen("b-a-d".toCharArray(), 1));
		assertTrue(dt.preserveHyphen("b-a-d".toCharArray(), 3));
		
		assertFalse(dt.preserveHyphen("inte-connect".toCharArray(), 4));
		assertFalse(dt.preserveHyphen("beauti-fu".toCharArray(), 6));
		assertFalse(dt.preserveHyphen("b-c-d".toCharArray(), 1));
		assertFalse(dt.preserveHyphen("b-c-d".toCharArray(), 3));
	}
}
