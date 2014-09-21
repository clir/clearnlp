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

import com.clearnlp.classification.configuration.AbstractTrainerConfiguration;
import com.clearnlp.classification.configuration.AdaGradTrainerConfiguration;
import com.clearnlp.classification.model.AbstractModel;
import com.clearnlp.classification.model.SparseModel;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.trainer.AbstractTrainer;
import com.clearnlp.classification.trainer.AdaGradLR;
import com.clearnlp.classification.trainer.AdaGradSVM;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AdaGradClassify extends AbstractClassifyOnline
{
	@Option(name="-a", usage="the learning rate (default: 0.1)", required=false, metaVar="<double>")
	private double d_alpha = 0.01;
	@Option(name="-r", usage="the tolerance of termination criterion (default: 0.1)", required=false, metaVar="<double>")
	private double d_rho   = 0.1;
	@Option(name="-average", usage="if set, average weights (default: false)", required=false, metaVar="<boolean>")
	protected boolean b_average = false;
	@Option(name="-logistic", usage="if set, logistic regression (default: false)", required=false, metaVar="<boolean>")
	protected boolean b_logistic = false;

	public AdaGradClassify(String[] args)
	{
		super(args);
	}

	@Override
	protected AbstractTrainerConfiguration createTrainConfiguration()
	{
		return new AdaGradTrainerConfiguration(i_vectorType, b_binary, i_labelCutoff, i_featureCutoff, i_numberOfThreads, b_average, d_alpha, d_rho);
	}

	@Override
	protected AbstractTrainer getTrainer(AbstractTrainerConfiguration trainConfiguration, AbstractModel<?, ?> model)
	{
		AdaGradTrainerConfiguration c = (AdaGradTrainerConfiguration)trainConfiguration;
		
		if (isSparseModel(model))
		{
			if (b_logistic)	return new AdaGradLR((SparseModel)model, c.isAverage(), c.getLearningRate(), c.getRidge());
			else			return new AdaGradSVM   ((SparseModel)model, c.isAverage(), c.getLearningRate(), c.getRidge());
		}
		else
		{
			if (b_average)	return new AdaGradLR((StringModel)model, c.getLabelCutoff(), c.getFeatureCutoff(), c.isAverage(), c.getLearningRate(), c.getRidge());
			else			return new AdaGradSVM   ((StringModel)model, c.getLabelCutoff(), c.getFeatureCutoff(), c.isAverage(), c.getLearningRate(), c.getRidge());
		}
	}
	
	static public void main(String[] args)
	{
		new AdaGradClassify(args);
	}
}