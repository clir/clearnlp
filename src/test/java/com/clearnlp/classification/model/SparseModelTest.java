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
package com.clearnlp.classification.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.clearnlp.classification.instance.SparseInstance;
import com.clearnlp.classification.instance.SparseInstanceReader;
import com.clearnlp.classification.vector.AbstractWeightVector;
import com.clearnlp.util.IOUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class SparseModelTest
{
	@Test
	public void testBinary() throws Exception
	{
		SparseInstanceReader reader = new SparseInstanceReader(IOUtils.createFileInputStream("src/test/resources/classification/model/binary-sparse.train"));
		SparseModel model = new SparseModel(true);
		AbstractWeightVector vector = model.getWeightVector();
		SparseInstance instance;
		
		while ((instance = reader.next()) != null)
			model.addInstance(instance);
		
		reader.close();
		model.initializeForTraining();
		
		assertEquals(   2, model.getLabelSize());
		assertEquals(  13, model.getFeatureSize());
		assertEquals(  13, vector.size());
		assertEquals(true, vector.isBinaryLabel());
	}
	
	@Test
	public void testMulti() throws Exception
	{
		SparseInstanceReader reader = new SparseInstanceReader(IOUtils.createFileInputStream("src/test/resources/classification/model/multi-sparse.train"));
		SparseModel model = new SparseModel(false);
		AbstractWeightVector vector = model.getWeightVector();
		SparseInstance instance;
		
		while ((instance = reader.next()) != null)
			model.addInstance(instance);
		
		reader.close();
		model.initializeForTraining();
		
		assertEquals( 3, model.getLabelSize());
		assertEquals( 7, model.getFeatureSize());
		assertEquals(21, vector.size());
	}
}