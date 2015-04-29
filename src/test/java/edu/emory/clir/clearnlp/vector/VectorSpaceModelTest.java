/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.vector;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VectorSpaceModelTest
{
	@Test
	public void test()
	{
		List<String> terms = DSUtils.toArrayList("A","B","C","D","E");
		Set<String> stopWords = DSUtils.toHashSet("B","D");
		
		ObjectIntHashMap<String> map = VectorSpaceModel.getBagOfWords(terms, stopWords, 3);
		
		assertEquals(6, map.size());
		assertEquals(1, map.get("A"));
		assertEquals(1, map.get("C"));
		assertEquals(1, map.get("E"));
		assertEquals(1, map.get("A_C"));
		assertEquals(1, map.get("C_E"));
		assertEquals(1, map.get("A_C_E"));
	}
}
