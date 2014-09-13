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

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.clearnlp.collection.pair.DoubleIntPair;
import com.clearnlp.collection.pair.Pair;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DSUtilsTest
{
	@Test
	public void testListUtils()
	{
		List<String> list = Lists.newArrayList("B","A","E","C","D");
		
		DSUtils.sortReverseOrder(list);
		assertEquals("[E, D, C, B, A]", list.toString());
		
		DSUtils.swap(list, 1, 3);
		assertEquals("[E, B, C, D, A]", list.toString());
		
		Random rand = new Random(5);
		DSUtils.shuffle(list, rand);
		assertEquals("[D, E, B, A, C]", list.toString());
		
		DSUtils.shuffle(list, rand, 3);
		assertEquals("[E, B, D, A, C]", list.toString());
	}
	
	@Test
	public void testHasIntersection()
	{
		Set<String> s1 = Sets.newHashSet("A","B");
		Set<String> s2 = Sets.newHashSet("C");
		
		assertFalse(DSUtils.hasIntersection(s1, s2));
		
		s2.add("A");
		assertTrue(DSUtils.hasIntersection(s1, s2));
	}
	
	@Test
	public void testRange()
	{
		assertEquals("[0, 1, 2, 3]"	, Arrays.toString(DSUtils.range(0, 4, 1)));
		assertEquals("[0, 2]"		, Arrays.toString(DSUtils.range(0, 4, 2)));
		assertEquals("[0, 3]"		, Arrays.toString(DSUtils.range(0, 4, 3)));
		assertEquals("[0]"			, Arrays.toString(DSUtils.range(0, 4, 4)));
		assertEquals("[0]"			, Arrays.toString(DSUtils.range(0, 4, 5)));
		
		assertEquals("[]", Arrays.toString(DSUtils.range(0,  4, -1)));
		assertEquals("[]", Arrays.toString(DSUtils.range(0, -4,  1)));
		
		assertEquals("[0, -1, -2, -3]"	, Arrays.toString(DSUtils.range(0, -4, -1)));
		assertEquals("[4, 3, 2, 1]"		, Arrays.toString(DSUtils.range(4,  0, -1)));
		assertEquals("[4, 2]"			, Arrays.toString(DSUtils.range(4,  0, -2)));
		assertEquals("[4]"				, Arrays.toString(DSUtils.range(4,  0, -5)));
	}
	
	@Test
	public void testTop()
	{
		double[] array = {3, 1, 2, 0, 4};
		Pair<DoubleIntPair,DoubleIntPair> ps = DSUtils.top2(array);
		
		DoubleIntPair p = ps.o1;
		assertEquals(p.i, 4);
		assertEquals(p.d, 4, 0);
		
		p = ps.o2;
		assertEquals(p.i, 0);
		assertEquals(p.d, 3, 0);
	}
}