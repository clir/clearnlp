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

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.instance.StringInstanceCollector;
import edu.emory.clir.clearnlp.classification.map.FeatureMap;
import edu.emory.clir.clearnlp.classification.map.LabelMap;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringInstanceCollectorTest
{
	@Test
	public void test()
	{
		StringInstanceCollector collector = new StringInstanceCollector();
		
		collector.addInstance(new StringInstance("L1", getStringFeatureVector1()));
		collector.addInstance(new StringInstance("L2", getStringFeatureVector2()));
		collector.addInstance(new StringInstance("L2", getStringFeatureVector3()));

		testLabels(collector.getLabelMap());
		testFeatures(collector.getFeatureMap());
	}
	
	private StringFeatureVector getStringFeatureVector1()
	{
		StringFeatureVector vector = new StringFeatureVector();
		
		vector.addFeature(0, "a1");
		vector.addFeature(1, "b1");
		
		return vector;
	}
	
	private StringFeatureVector getStringFeatureVector2()
	{
		StringFeatureVector vector = new StringFeatureVector();
		
		vector.addFeature(0, "a2");
		vector.addFeature(2, "c2");
		
		return vector;
	}
	
	private StringFeatureVector getStringFeatureVector3()
	{
		StringFeatureVector vector = new StringFeatureVector();
		
		vector.addFeature(0, "a2");
		vector.addFeature(1, "b1");
		
		return vector;
	}
	
	private void testLabels(ObjectIntHashMap<String> labels)
	{
		assertEquals(2, labels.size());
		assertEquals(1, labels.get("L1"));
		assertEquals(2, labels.get("L2"));
		
		LabelMap lmap = new LabelMap();
		lmap.expand(labels, 1);
		
		assertEquals(   1, lmap.size());
		assertEquals("L2", lmap.getLabel(0));
		assertEquals(   0, lmap.getLabelIndex("L2"));
		assertEquals(  -1, lmap.getLabelIndex("L1"));
		
		lmap.expand(labels, 0);
		
		assertEquals(   2, lmap.size());
		assertEquals("L2", lmap.getLabel(0));
		assertEquals("L1", lmap.getLabel(1));
		assertEquals(   0, lmap.getLabelIndex("L2"));
		assertEquals(   1, lmap.getLabelIndex("L1"));
	}
	
	private void testFeatures(IntObjectHashMap<ObjectIntHashMap<String>> features)
	{
		assertEquals(3, features.size());
		ObjectIntHashMap<String> map;
		
		map = features.get(0);
		assertEquals(2, map.size());
		assertEquals(1, map.get("a1"));
		assertEquals(2, map.get("a2"));
		
		map = features.get(1);
		assertEquals(1, map.size());
		assertEquals(2, map.get("b1"));
		
		map = features.get(2);
		assertEquals(1, map.size());
		assertEquals(1, map.get("c2"));
		
		FeatureMap fmap = new FeatureMap();
		fmap.expand(features, 1);
		
		assertEquals(3, fmap.size());
		assertEquals(1, fmap.getFeatureIndex(0, "a2"));
		assertEquals(2, fmap.getFeatureIndex(1, "b1"));
		assertEquals(0, fmap.getFeatureIndex(0, "a1"));
		assertEquals(0, fmap.getFeatureIndex(2, "c2"));
		
		fmap.expand(features, 0);
		
		assertEquals(5, fmap.size());
		assertEquals(1, fmap.getFeatureIndex(0, "a2"));
		assertEquals(2, fmap.getFeatureIndex(1, "b1"));
		assertEquals(3, fmap.getFeatureIndex(0, "a1"));
		assertEquals(4, fmap.getFeatureIndex(2, "c2"));
	}
}