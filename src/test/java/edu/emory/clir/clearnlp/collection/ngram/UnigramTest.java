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
package edu.emory.clir.clearnlp.collection.ngram;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class UnigramTest
{
	@Test
	public void test()
	{
		Unigram<String> map = new Unigram<>();
		
		map.add("A");
		map.add("B", 2);
		map.add("C");
		map.add("A");
		map.add("B", 2);
		map.add("D");
		
		assertEquals("[(C,1), (D,1), (B,4), (A,2)]", map.toList(0).toString());
		assertEquals("[(C,0.125), (D,0.125), (B,0.5), (A,0.25)]", map.toList(0d).toString());
		
		assertEquals("[(B,4), (A,2)]", map.toList(1).toString());
		assertEquals("[(B,0.5), (A,0.25)]", map.toList(0.2).toString());
		
		assertEquals("[A, B, C, D]", map.keySet(0).toString());
		assertEquals("[A, B, C, D]", map.keySet(0d).toString());
		
		assertEquals("[A, B]", map.keySet(1).toString());
		assertEquals("[A, B]", map.keySet(0.2).toString());
		
//		System.out.println(map.getBest());
	}
}
