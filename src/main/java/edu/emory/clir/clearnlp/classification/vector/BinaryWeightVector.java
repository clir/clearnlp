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

import java.io.Serializable;

import edu.emory.clir.clearnlp.util.DSUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BinaryWeightVector extends AbstractWeightVector implements Serializable
{
	private static final long serialVersionUID = -245160429516771704L;
	public  static final int  POSITIVE = 0;
	public  static final int  NEGATIVE = 1;
	
	public BinaryWeightVector()
	{
		super(true);
	}
	
	@Override
	public void expand(int labelSize, int featureSize)
	{
		int diff = featureSize - n_features;
		
		if (diff > 0)
			DSUtils.append(f_weights, 0f, diff);

		trimToSize();

		n_labels   = labelSize;
		n_features = featureSize;
	}
	
	@Override
	public double[] getScores(SparseFeatureVector x)
	{
		int i, index, len = x.size();
		double score = get(0);
		
		for (i=0; i<len; i++)
		{
			index = x.getIndex(i);
			
			if (isValidFeatureIndex(index))
				score += get(index) * x.getWeight(i);
		}
		
		double[]  scores = new double[2];
		scores[POSITIVE] =  score;
		scores[NEGATIVE] = -score;
		return scores;
	}
	
	@Override
	public double[] getScores(SparseFeatureVector x, int[] include)
	{
		return getScores(x);
	}
	
	@Override
	public int getWeightIndex(int labelIndex, int featureIndex)
	{
		return featureIndex;
	}
	
	@Override
	public float[] getWeights(int labelIndex)
	{
		float inv = (labelIndex == POSITIVE) ? 1 : -1; 
		float[] weights = new float[n_features];
		int i;
		
		for (i=0; i<n_features; i++)
			weights[i] = get(i) * inv;
		
		return weights;
	}
	
	public void setWeights(int labelIndex, float[] weights)
	{
		float inv = (labelIndex == POSITIVE) ? 1 : -1; 
		int i;
		
		for (i=0; i<n_features; i++)
			set(i, weights[i] * inv);
	}
}