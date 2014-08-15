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
package com.clearnlp.experiment;

import org.kohsuke.args4j.Option;

import com.clearnlp.classification.configuration.AbstractTrainConfiguration;
import com.clearnlp.classification.configuration.LiblinearTrainConfiguration;
import com.clearnlp.classification.model.AbstractModel;
import com.clearnlp.classification.model.SparseModel;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.train.AbstractTrainer;
import com.clearnlp.classification.train.LiblinearL2SVM;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class LiblinearClassify extends AbstractClassifyOneVsAll
{
	@Option(name="-c", usage="the cost (default: 0.1)", required=false, metaVar="<double>")
	private double d_cost = 0.1;
	@Option(name="-e", usage="the tolerance of termination criterion (default: 0.1)", required=false, metaVar="<double>")
	private double d_epsilon  = 0.1;
	@Option(name="-b", usage="the bias (default: 0)", required=false, metaVar="<double>")
	private double d_bias = 0.0;

	public LiblinearClassify(String[] args)
	{
		super(args);
	}

	@Override
	protected AbstractTrainConfiguration createTrainConfiguration()
	{
		return new LiblinearTrainConfiguration(i_vectorType, b_binary, i_labelCutoff, i_featureCutoff, i_numberOfThreads, d_cost, d_epsilon, d_bias);
	}

	@Override
	protected AbstractTrainer getTrainer(AbstractTrainConfiguration trainConfiguration, AbstractModel<?, ?> model)
	{
		LiblinearTrainConfiguration c = (LiblinearTrainConfiguration)trainConfiguration;
		
		if (isSparseModel(model))
			return new LiblinearL2SVM((SparseModel)model, c.getNumberOfThreads(), c.getCost(), c.getEpsilon(), c.getBias());
		else
			return new LiblinearL2SVM((StringModel)model, c.getLabelCutoff(), c.getFeatureCutoff(), false, c.getNumberOfThreads(), c.getCost(), c.getEpsilon(), c.getBias());
	}
	
	static public void main(String[] args)
	{
		new LiblinearClassify(args);
	}
}