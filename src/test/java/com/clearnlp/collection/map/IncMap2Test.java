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
package com.clearnlp.collection.map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.map.IncMap2;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IncMap2Test
{
	@Test
	public void test()
	{
		IncMap2<String,String> map = new IncMap2<>();
		
		map.add("A", "a1");
		map.add("A", "a2");
		map.add("A", "a1");
		map.add("A", "a3");
		
		map.add("B", "b1");
		map.add("B", "b2", 2);
		map.add("B", "b3");

		assertEquals("[A, B]", map.getKeySet1().toString());
		
		assertEquals("[(a3,1), (a1,2), (a2,1)]", map.toList("A", 0).toString());
		assertEquals("[(b1,1), (b2,2), (b3,1)]", map.toList("B", 0).toString());
		assertEquals("[(a1,2)]", map.toList("A", 1).toString());
	}
}