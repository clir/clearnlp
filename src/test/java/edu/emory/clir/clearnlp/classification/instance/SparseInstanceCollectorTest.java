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
package edu.emory.clir.clearnlp.classification.instance;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.clir.clearnlp.classification.instance.SparseInstance;
import edu.emory.clir.clearnlp.classification.instance.SparseInstanceCollector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseInstanceCollectorTest
{
	@Test
	public void test()
	{
		SparseInstanceCollector collector = new SparseInstanceCollector();
		
		collector.addInstance(new SparseInstance("2", getSparseFeatureVector1()));
		collector.addInstance(new SparseInstance("0", getSparseFeatureVector2()));
		collector.addInstance(new SparseInstance("1", getSparseFeatureVector3()));

		assertEquals(3, collector.getLabelSize());
		assertEquals(4, collector.getFeatureSize());
	}
	
	private SparseFeatureVector getSparseFeatureVector1()
	{
		SparseFeatureVector vector = new SparseFeatureVector();
		
		vector.addFeature(0);
		vector.addFeature(1);
		
		return vector;
	}
	
	private SparseFeatureVector getSparseFeatureVector2()
	{
		SparseFeatureVector vector = new SparseFeatureVector();
		
		vector.addFeature(1);
		vector.addFeature(2);
		
		return vector;
	}
	
	private SparseFeatureVector getSparseFeatureVector3()
	{
		SparseFeatureVector vector = new SparseFeatureVector();
		
		vector.addFeature(2);
		vector.addFeature(3);
		
		return vector;
	}
}