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

import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseFeatureVectorTest
{
	@Test
	public void testSparseFeatureVector()
	{
		// features without weights
		SparseFeatureVector vector = new SparseFeatureVector();

		vector.addFeature(0);
		vector.addFeature(1);
		
		assertEquals(0, vector.getIndex(0));
		assertEquals(2, vector.size());
		assertEquals("0 1", vector.toString());
		assertEquals(2, vector.sumOfSquares(), 0);
		
		// features with weights
		vector = new SparseFeatureVector(true);
		
		vector.addFeature(0, 0.1);
		vector.addFeature(1, 0.2);
		
		assertEquals(0, vector.getIndex(0));
		assertTrue(0.2 == vector.getWeight(1));
		assertEquals(2, vector.size());
		assertEquals("0:0.1 1:0.2", vector.toString());
		assertEquals("0.05", String.format("%4.2f", vector.sumOfSquares()));
	}
}