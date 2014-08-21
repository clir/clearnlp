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

import java.util.List;

import com.clearnlp.classification.instance.IntInstance;
import com.clearnlp.classification.model.SparseModel;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.vector.AbstractWeightVector;
import com.clearnlp.util.BinUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractTrainer
{
	protected final int RANDOM_SEED = 5; 
	protected List<IntInstance> l_instances;
	volatile protected AbstractWeightVector w_vector;

	public AbstractTrainer(SparseModel model)
	{
		l_instances = model.initializeForTraining();
		w_vector    = model.getWeightVector();
		logInfo();
	}
	
	public AbstractTrainer(StringModel model, int labelCutoff, int featureCutoff)
	{
		l_instances = model.initializeForTraining(labelCutoff, featureCutoff);
		w_vector    = model.getWeightVector();
		logInfo();
	}
	
	private void logInfo()
	{
		StringBuilder build = new StringBuilder();
		
		build.append("Training\n");
		build.append("- Labels   : ");	build.append(getLabelSize());		build.append("\n");
		build.append("- Features : ");	build.append(getFeatureSize());		build.append("\n");
		build.append("- Instances: ");	build.append(getInstanceSize());	build.append("\n");
		
		BinUtils.LOG.info(build.toString());
	}
	
	abstract public void train();
	
	public int getLabelSize()
	{
		return w_vector.getLabelSize();
	}
	
	public int getFeatureSize()
	{
		return w_vector.getFeatureSize();
	}
	
	public int getInstanceSize()
	{
		return l_instances.size();
	}
	
	public IntInstance getInstance(int index)
	{
		return l_instances.get(index);
	}
	
	public AbstractWeightVector getWeightVector()
	{
		return w_vector;
	}
}