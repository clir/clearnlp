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
package edu.emory.clir.clearnlp.classification.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.classification.instance.IntInstance;
import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.instance.StringInstanceReader;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.AbstractWeightVector;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.util.IOUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringModelTest
{
	@Test
	public void testBinary() throws Exception
	{
		StringInstanceReader reader = new StringInstanceReader(IOUtils.createFileInputStream("src/test/resources/classification/model/binary-string.train"));
		List<StringInstance> instances = Lists.newArrayList();
		StringInstance instance;
		
		while ((instance = reader.next()) != null)
			instances.add(instance);
		
		reader.close();
		
		StringModel model = new StringModel(true);
		AbstractWeightVector vector = model.getWeightVector();
		
		for (StringInstance inst : instances)
			model.addInstance(inst);

		model.initializeForTraining(2, 1);
		assertEquals(   1, model.getLabelSize());
		assertEquals(   4, model.getFeatureSize());
		assertEquals(   4, vector.size());
		assertTrue(vector.isBinaryLabel());
		
		for (StringInstance inst : instances)
			model.addInstance(inst);
		
		List<IntInstance> list = model.initializeForTraining(0, 0);
		
		assertEquals(   2, model.getLabelSize());
		assertEquals(  13, model.getFeatureSize());
		assertEquals(  13, vector.size());
		assertTrue(vector.isBinaryLabel());
		
		String[] sparse = {"1 5 2 11", "0 6 2 10", "1 4 7 3", "0 1 9 12", "0 1 8 3"};
		int i, size = sparse.length;
		
		for (i=0; i<size; i++)
			assertEquals(sparse[i], list.get(i).toString());
		
		vector.set( 0,  0);
		vector.set( 1,  2);
		vector.set( 2,  0);
		vector.set( 3, -1);
		vector.set( 4, -1);
		vector.set( 5, -1);
		vector.set( 6,  1);
		vector.set( 7, -1);
		vector.set( 8,  1);
		vector.set( 9,  1);
		vector.set(10,  1);
		vector.set(11, -1);
		vector.set(12,  1);
		
		testBinaryAux(model);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
		model.save(out);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bout.toByteArray())));
		model.load(in);
		in.close();
		
		testBinaryAux(model);
	}

	private void testBinaryAux(StringModel model)
	{
		StringFeatureVector x0;
		StringPrediction p;
		double[] scores;
		
		x0 = new StringFeatureVector();
		x0.addFeature(0, "jinho");
		x0.addFeature(1, "martin");
		x0.addFeature(2, "s");
		
		scores = model.getScores(x0);
		assertEquals(scores[0], -1, 0);
		assertEquals(scores[1],  1, 0);
		
		p = model.predictBest(x0);
		assertEquals("male", p.getLabel());
		assertEquals(1, p.getScore(), 0);

		x0 = new StringFeatureVector(true);
		x0.addFeature(0, "jinho", 2);
		x0.addFeature(1, "martin", 2);
		x0.addFeature(2, "s", 5);

		scores = model.getScores(x0);
		assertEquals(scores[0],  1, 0);
		assertEquals(scores[1], -1, 0);
		
		p = model.predictBest(x0);
		assertEquals("female", p.getLabel());
		assertEquals(1, p.getScore(), 0);

		x0 = new StringFeatureVector();
		x0.addFeature(1, "jinho");
		x0.addFeature(0, "martin");
		x0.addFeature(2, "s");

		scores = model.getScores(x0);
		assertEquals(scores[0],  1, 0);
		assertEquals(scores[1], -1, 0);
		
		x0 = new StringFeatureVector();
		x0.addFeature(1, "jinho");
		x0.addFeature(0, "martin");
		x0.addFeature(2, "h");
		
		p = model.predictBest(x0);
		assertEquals("male", p.getLabel());
		assertEquals(1, p.getScore(), 0);
	}
	
	@Test
	public void testMulti() throws Exception
	{
		StringInstanceReader reader = new StringInstanceReader(IOUtils.createFileInputStream("src/test/resources/classification/model/multi-string.train"));
		List<StringInstance> instances = Lists.newArrayList();
		StringInstance instance;
		
		while ((instance = reader.next()) != null)
			instances.add(instance);
		
		reader.close();
		
		StringModel model = new StringModel(false);
		AbstractWeightVector vector = model.getWeightVector();
		
		for (StringInstance inst : instances)
			model.addInstance(inst);

		model.initializeForTraining(1, 1);
		assertEquals(2, model.getLabelSize());
		assertEquals(4, model.getFeatureSize());
		assertEquals(8, vector.size());
		assertFalse(vector.isBinaryLabel());
		
		for (StringInstance inst : instances)
			model.addInstance(inst);
		
		List<IntInstance> list = model.initializeForTraining(0, 0);
		
		assertEquals( 3, model.getLabelSize());
		assertEquals( 7, model.getFeatureSize());
		assertEquals(21, vector.size());
		
		String[] sparse = {"2 4 2 3", "0 1 5", "1 1 2", "0 3", "1 6"};
		int i, size = sparse.length;
		
		for (i=0; i<size; i++)
			assertEquals(sparse[i], list.get(i).toString());

		vector.set(vector.getWeightIndex(0, 1),  1);
		vector.set(vector.getWeightIndex(1, 1),  1);
		vector.set(vector.getWeightIndex(2, 1), -1);
		
		vector.set(vector.getWeightIndex(0, 2), -1);
		vector.set(vector.getWeightIndex(1, 2),  0);
		vector.set(vector.getWeightIndex(2, 2),  1);
		
		vector.set(vector.getWeightIndex(0, 3),  1);
		vector.set(vector.getWeightIndex(1, 3), -1);
		vector.set(vector.getWeightIndex(2, 3),  1);
		
		vector.set(vector.getWeightIndex(0, 4), -1);
		vector.set(vector.getWeightIndex(1, 4), -1);
		vector.set(vector.getWeightIndex(2, 4),  1);
		
		vector.set(vector.getWeightIndex(0, 5),  1);
		vector.set(vector.getWeightIndex(1, 5), -1);
		vector.set(vector.getWeightIndex(2, 5), -1);
		
		vector.set(vector.getWeightIndex(0, 6), -1);
		vector.set(vector.getWeightIndex(1, 6),  1);
		vector.set(vector.getWeightIndex(2, 6), -1);
		
		assertEquals("[0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0]", Arrays.toString(vector.getWeights(0)));
		assertEquals("[0.0, 1.0, 0.0, -1.0, -1.0, -1.0, 1.0]", Arrays.toString(vector.getWeights(1)));
		assertEquals("[0.0, -1.0, 1.0, 1.0, 1.0, -1.0, -1.0]", Arrays.toString(vector.getWeights(2)));
		
		testMultiAux(model);
		
		vector.set(vector.getWeightIndex(0, 0), 0);
		vector.set(vector.getWeightIndex(1, 0), 0);
		vector.set(vector.getWeightIndex(2, 0), 0);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
		model.save(out);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bout.toByteArray())));
		model.load(in);
		in.close();
		
		testMultiAux(model);
	}

	private void testMultiAux(StringModel model)
	{
		AbstractWeightVector vector = model.getWeightVector();
		Pair<StringPrediction,StringPrediction> p2;
		List<StringPrediction> pl;
		StringFeatureVector x0;
		StringPrediction p;
		double[] scores;
		
		x0 = new StringFeatureVector();
		x0.addFeature(0, "bright");
		x0.addFeature(1, "dry");
		x0.addFeature(2, "dark");
				
		scores = model.getScores(x0);
		assertEquals(scores[0], -3, 0);
		assertEquals(scores[1],  0, 0);
		assertEquals(scores[2],  1, 0);
		
		p = model.predictBest(x0);
		assertEquals("sunny", p.getLabel());
		assertEquals(1, p.getScore(), 0);
		
		p2 = model.predictTop2(x0);
		
		p = p2.o1;
		assertEquals("sunny", p.getLabel());
		assertEquals(1, p.getScore(), 0);
		
		p = p2.o2;
		assertEquals("cloudy", p.getLabel());
		assertEquals(0, p.getScore(), 0);
		
		pl = model.predictAll(x0);
		
		p = pl.get(0);
		assertEquals("sunny", p.getLabel());
		assertEquals(1, p.getScore(), 0);
		
		p = pl.get(1);
		assertEquals("cloudy", p.getLabel());
		assertEquals(0, p.getScore(), 0);
		
		p = pl.get(2);
		assertEquals("rainy", p.getLabel());
		assertEquals(-3, p.getScore(), 0);
		
		vector.add(vector.getWeightIndex(0, 0), 5);
		vector.add(vector.getWeightIndex(1, 0), 4);
		vector.add(vector.getWeightIndex(2, 0), 3);
		vector.add(vector.getWeightIndex(0, 0), 5);
		vector.add(vector.getWeightIndex(1, 0), 6);
		vector.add(vector.getWeightIndex(2, 0), 7);
		
		x0 = new StringFeatureVector(true);
		x0.addFeature(1, "bright", 2d);
		x0.addFeature(2, "dry"   , 2d);
		x0.addFeature(0, "dark"  , 2d);
		
		scores = model.getScores(x0);
		assertEquals(scores[0], 12, 0);
		assertEquals(scores[1], 12, 0);
		assertEquals(scores[2],  8, 0);
		
		p = model.predictBest(x0);
		assertEquals("rainy", p.getLabel());
		assertEquals(12, p.getScore(), 0);
		
		p2 = model.predictTop2(x0);
		
		p = p2.o1;
		assertEquals("rainy", p.getLabel());
		assertEquals(12, p.getScore(), 0);
		
		p = p2.o2;
		assertEquals("cloudy", p.getLabel());
		assertEquals(12, p.getScore(), 0);
		
		pl = model.predictAll(x0);
		
		p = pl.get(0);
		assertEquals("rainy", p.getLabel());
		assertEquals(12, p.getScore(), 0);
		
		p = pl.get(1);
		assertEquals("cloudy", p.getLabel());
		assertEquals(12, p.getScore(), 0);
		
		p = pl.get(2);
		assertEquals("sunny", p.getLabel());
		assertEquals(8, p.getScore(), 0);
	}
}