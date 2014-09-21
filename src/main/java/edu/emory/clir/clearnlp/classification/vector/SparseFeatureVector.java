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
package edu.emory.clir.clearnlp.classification.vector;

import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseFeatureVector extends AbstractFeatureVector
{
	private IntArrayList i_indices;
	
	public SparseFeatureVector()
	{
		super(false);
		init();
	}
	
	public SparseFeatureVector(boolean hasWeight)
	{
		super(hasWeight);
		init();
	}
	
	private void init()
	{
		i_indices = new IntArrayList();
	}
	
	/** @param index the feature index. */
	public void addFeature(int index)
	{
		if (hasWeight())	addFeature(index, 1);
		else				i_indices.add(index);
	}
	
	/**
	 * @param index the feature index.
	 * @param weight the feature weight.
	 */
	public void addFeature(int index, double weight)
	{
		i_indices.add(index);
		d_weights.add(weight);
	}
	
	/** @return the index'th feature index. */
	public int getIndex(int index)
	{
		return i_indices.get(index);
	}
	
	public int getMaxIndex()
	{
		return i_indices.max();
	}

	/** @return \forall_i \sigma w_i^2. */
	public double sumOfSquares()
	{
		if (hasWeight())
		{
			int i, size = size();
			double sum = 0;
			
			for (i=0; i<size; i++)
				sum += MathUtils.sq(getWeight(i));
			
			return sum;	
		}
		
		return size();
	}
	
	@Override
	public int size()
	{
		return i_indices.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return i_indices.isEmpty();
	}
	
	@Override
	public void trimToSize()
	{
		i_indices.trimToSize();
		if (hasWeight()) d_weights.trimToSize();
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		int i, size = size();
		
		for (i=0; i<size; i++)
		{
			build.append(DELIM_FEATURE);
			build.append(getIndex(i));
			
			if (hasWeight())
			{
				build.append(DELIM_WEIGHT);
				build.append(getWeight(i));
			}
		}
		
		return build.toString().substring(DELIM_FEATURE.length());
	}
}