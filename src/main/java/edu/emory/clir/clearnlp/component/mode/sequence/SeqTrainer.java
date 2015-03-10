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
package edu.emory.clir.clearnlp.component.mode.sequence;

import java.io.InputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.nlp.configuration.AbstractTrainConfiguration;
import edu.emory.clir.clearnlp.nlp.trainer.AbstractNLPTrainer;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SeqTrainer extends AbstractNLPTrainer
{
	protected SeqFeatureExtractor[] f_extractors;
	
	public SeqTrainer(InputStream configuration)
	{
		super(configuration);
	}
	
	public SeqTrainer(InputStream configuration, InputStream[] features)
	{
		super(configuration);
		f_extractors = new SeqFeatureExtractor[]{new SeqFeatureExtractor(features[0])};
	}
	
	@Override
	protected AbstractTrainConfiguration createConfiguration(InputStream configuration)
	{
		return new SeqTrainConfiguration(configuration);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForCollect()
	{
		return null;
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForTrain(Object[] lexicons)
	{
		return new DefaultSequenceClassifier(f_extractors, lexicons, (SeqTrainConfiguration)t_configuration);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForBootstrap(Object[] lexicons, StringModel[] models)
	{
		return new DefaultSequenceClassifier(f_extractors, lexicons, models, true, (SeqTrainConfiguration)t_configuration);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForEvaluate(Object[] lexicons, StringModel[] models)
	{
		return new DefaultSequenceClassifier(f_extractors, lexicons, models, false, (SeqTrainConfiguration)t_configuration);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForDecode(byte[] models)
	{
		return new DefaultSequenceClassifier(models);
	}
}
