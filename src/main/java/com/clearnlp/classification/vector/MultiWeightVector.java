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

import java.io.Serializable;

import com.clearnlp.collection.list.FloatArrayList;
import com.clearnlp.util.DSUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class MultiWeightVector extends AbstractWeightVector implements Serializable
{
	private static final long serialVersionUID = 7255272201058803937L;
	
	public MultiWeightVector()
	{
		super(false);
	}
	
	@Override
	public void expand(int labelSize, int featureSize)
	{
		if (isEmpty())
		{
			DSUtils.append(f_weights, 0f, labelSize * featureSize);
		}
		else
		{
			if (labelSize > n_labels)
				expandLabels(labelSize);
			
			if (featureSize > n_features)
				DSUtils.append(f_weights, 0f, labelSize * (featureSize - n_features));
		}
		
		trimToSize();
		
		n_labels   = labelSize;
		n_features = featureSize;
	}
	
	/** Called by {@link #expand(int, int)}. */
	private void expandLabels(int labelSize)
	{
		FloatArrayList list = new FloatArrayList(size());
		int i, diff = labelSize - n_labels;
		
		while (!isEmpty())
		{
			for (i=0; i<n_labels; i++)
				list.add(f_weights.remove(0));
			
			DSUtils.append(list, 0f, diff);
		}

		f_weights = list;
	}
	
	@Override
	public double[] getScores(SparseFeatureVector x)
	{
		double[] scores = f_weights.toDoubleArray(0, n_labels);
		int i, j, index, len = x.size();
		double weight;
		
		for (i=0; i<len; i++)
		{
			index = x.getIndex(i);
			
			if (isValidFeatureIndex(index))
			{
				index  = getWeightIndex(index);
				weight = x.getWeight(i);
				
				for (j=0; j<n_labels; j++)
					scores[j] += get(index+j) * weight;
			}
		}
		
		return scores;
	}
	
	@Override
	public int getWeightIndex(int labelIndex, int featureIndex)
	{
		return getWeightIndex(featureIndex) + labelIndex;
	}
	
	private int getWeightIndex(int featureIndex)
	{
		return featureIndex * n_labels;
	}
	
	@Override
	public float[] getWeights(int labelIndex)
	{
		float[] weights = new float[n_features];
		int i;
		
		for (i=0; i<n_features; i++)
			weights[i] = get(getWeightIndex(labelIndex, i));
		
		return weights;
	}
	
	@Override
	public void setWeights(int labelIndex, float[] weights)
	{
		int i; for (i=0; i<n_features; i++)
			set(getWeightIndex(labelIndex, i), weights[i]);
	}
}