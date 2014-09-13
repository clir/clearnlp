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
package com.clearnlp.classification.train;

import java.util.Arrays;
import java.util.Random;

import com.clearnlp.classification.instance.IntInstance;
import com.clearnlp.classification.model.SparseModel;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.MathUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractOnlineTrainer extends AbstractTrainer
{
	protected double[] d_average;
	protected Random   r_rand;
	
	/** @param average if {@code true}, weights are averaged. */
	public AbstractOnlineTrainer(SparseModel model, boolean average)
	{
		super(model);
		init(average);
	}
	
	/** @param average if {@code true}, weights are averaged. */
	public AbstractOnlineTrainer(StringModel model, int labelCutoff, int featureCutoff, boolean average)
	{
		super(model, labelCutoff, featureCutoff);
		init(average);
	}

	private void init(boolean average)
	{
		d_average = average ? new double[w_vector.size()] : null;
		r_rand = new Random(RANDOM_SEED);
	}

	public void train()
	{	
		if (average()) Arrays.fill(d_average, 0);
		DSUtils.shuffle(l_instances, r_rand);
		int i, size = getInstanceSize();
		
		for (i=0; i<size; i++)
			update(getInstance(i), i+1);
		
		if (average())
			setAverageWeights(size+1);
	}
	
	protected boolean average()
	{
		return d_average != null;
	}
	
	private void setAverageWeights(int count)
	{
		double c = -MathUtils.reciprocal(count);
		int i, size = w_vector.size();
		
		for (i=0; i<size; i++)
			w_vector.add(i, (float)(c*d_average[i]));
	}
	
	abstract protected boolean update(IntInstance instance, int averageCount);
}