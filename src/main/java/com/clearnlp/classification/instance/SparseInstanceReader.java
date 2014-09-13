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
package com.clearnlp.classification.instance;

import java.io.InputStream;

import com.clearnlp.classification.vector.SparseFeatureVector;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseInstanceReader extends AbstractInstanceReader<SparseInstance,SparseFeatureVector>
{
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in));}. */
	public SparseInstanceReader(InputStream in)
	{
		super(in);
	}

	@Override
	protected SparseFeatureVector createFeatureVector(String[] col)
	{
		return new SparseFeatureVector(col.length > 1);
	}
	
	@Override
	protected SparseInstance getInstance(String label, SparseFeatureVector vector)
	{
		return new SparseInstance(label, vector);
	}

	@Override
	protected void addFeature(SparseFeatureVector vector, String[] col)
	{
		if (vector.hasWeight())
			vector.addFeature(Integer.parseInt(col[0]), Double.parseDouble(col[1]));
		else
			vector.addFeature(Integer.parseInt(col[0]));
	}
}