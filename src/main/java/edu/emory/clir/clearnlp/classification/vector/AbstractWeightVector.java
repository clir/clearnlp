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

import edu.emory.clir.clearnlp.collection.list.FloatArrayList;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractWeightVector implements Serializable
{
	private static final long serialVersionUID = -5894588398786815810L;
	
	protected FloatArrayList f_weights;
	protected boolean        b_binary;
	protected int            n_labels;
	protected int            n_features;
	
	public AbstractWeightVector(boolean binary)
	{
		b_binary = binary;
		reset();
	}
	
	public void reset()
	{
		f_weights  = new FloatArrayList();
		n_labels   = 0;
		n_features = 0;
	}
	
	/** Expands the weight vector size with more labels and features. */
	abstract public void expand(int labelSize, int featureSize);
	/** @return the array of scores of all labels given the feature vector. */
	abstract public double[] getScores(SparseFeatureVector x);
	/**
	 * @param include get scores for only these indices.
	 * @return the array of scores of all labels given the feature vector.
	 */
	abstract public double[] getScores(SparseFeatureVector x, int[] include);
	/**
	 * @return the index of the weight vector given the label and feature indices.
	 * If this is a binary model, returns the {@code featureIndex}.
	 */
	abstract public int getWeightIndex(int labelIndex, int featureIndex);
	/** @return the weight vector of the specific label. */
	abstract public float[] getWeights(int labelIndex);
	/** Sets the weight vector of the specific label. */
	abstract public void setWeights(int labelIndex, float[] weights);
	
	public int getLabelSize()
	{
		return n_labels;
	}
	
	public int getFeatureSize()
	{
		return n_features;
	}
	
	public boolean isBinaryLabel()
	{
		return b_binary;
	}
	
	public float get(int weightIndex)
	{
		return f_weights.get(weightIndex);
	}
	
	public void set(int weightIndex, float value)
	{
		f_weights.set(weightIndex, value);
	}
	
	public void set(double[] array)
	{
		f_weights.set(array);
	}
	
	public void add(int weightIndex, float value)
	{
		f_weights.set(weightIndex, f_weights.get(weightIndex)+value);
	}
	
	public void multiply(int weightIndex, float value)
	{
		f_weights.set(weightIndex, f_weights.get(weightIndex)*value);
	}
	
	public void add(int labelIndex, int featureIndex, float value)
	{
		add(getWeightIndex(labelIndex, featureIndex), value);
	}
	
	public int size()
	{
		return f_weights.size();
	}
	
	public boolean isEmpty()
	{
		return f_weights.isEmpty();
	}
	
	public void trimToSize()
	{
		f_weights.trimToSize();
	}
	
	protected boolean isValidFeatureIndex(int index)
	{
		return 0 < index && index < n_features;
	}
	
	public FloatArrayList cloneWeights()
	{
		return f_weights.clone();
	}

	public void setWeights(FloatArrayList weights)
	{
		f_weights = weights;
	}
	
	public double[] getScores(SparseFeatureVector x, boolean normalize)
	{
		double[] scores = getScores(x);
		if (normalize) normalize(scores);
		return scores;
	}
	
	private void normalize(double[] scores)
	{
		int i, size = scores.length;
		double d, sum = 0;
		
		for (i=0; i<size; i++)
		{
			d = Math.exp(scores[i]);
			scores[i] = d;
			sum += d;
		}
		
		for (i=0; i<size; i++)
			scores[i] /= sum;
	}
}