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
package edu.emory.clir.clearnlp.bin.helper;

import edu.emory.clir.clearnlp.classification.configuration.AbstractTrainerConfiguration;
import edu.emory.clir.clearnlp.classification.model.AbstractModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractClassifyOneVsAll extends AbstractClassify
{
	public AbstractClassifyOneVsAll(String[] args)
	{
		new ArgsReader(args, this);
		
		AbstractTrainerConfiguration trainConfiguration = createTrainConfiguration();
		AbstractModel<?,?> model = null;
		
		if (s_trainFile != null)
		{
			model = train(trainConfiguration, s_trainFile);
			if (s_modelFile != null) saveModel(model, s_modelFile);
		}
		else if (s_modelFile != null)
		{
			model = loadModel(s_modelFile, i_vectorType);
		}
		
		if (s_testFile != null)
			evaluate(model, s_testFile);
	}
	
	/** @return a trained model using the specific training file. */
	public AbstractModel<?,?> train(AbstractTrainerConfiguration trainConfiguration, String trainFile)
	{
		AbstractModel<?,?> model = createModel(trainConfiguration.getVectorType(), trainConfiguration.isBinary());
		readInstances(model, trainFile);
		
		AbstractTrainer trainer = getTrainer(trainConfiguration, model);
		trainer.train();
		
		return model;
	}
}