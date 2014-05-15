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
package com.clearnlp.classification.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clearnlp.classification.vector.StringFeatureVector;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class StringFeatureVectorTest
{
	@Test
	public void testStringFeatureVector()
	{
		// features without weights
		StringFeatureVector vector = new StringFeatureVector();

		vector.addFeature(0, "A");
		vector.addFeature(1, "B");
		
		assertEquals(0, vector.getType(0));
		assertEquals("B", vector.getValue(1));
		assertEquals(2, vector.size());
		assertEquals("0:A 1:B", vector.toString());
		
		// features with weights
		vector = new StringFeatureVector(true);
		
		vector.addFeature(0, "A", 0.1);
		vector.addFeature(1, "B", 0.2);
		
		assertEquals(0, vector.getType(0));
		assertEquals("B", vector.getValue(1));
		assertTrue(0.2 == vector.getWeight(1));
		assertEquals(2, vector.size());
		assertEquals("0:A:0.1 1:B:0.2", vector.toString());
	}
}