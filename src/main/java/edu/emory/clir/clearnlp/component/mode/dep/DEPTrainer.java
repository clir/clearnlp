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
package edu.emory.clir.clearnlp.component.mode.dep;

import java.io.InputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.trainer.AbstractNLPTrainer;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPTrainer extends AbstractNLPTrainer
{
	protected DEPFeatureExtractor[] f_extractors;
	
	public DEPTrainer(InputStream configuration)
	{
		super(configuration);
	}
	
	public DEPTrainer(InputStream configuration, InputStream[] features)
	{
		super(configuration);
		f_extractors = new DEPFeatureExtractor[]{new DEPFeatureExtractor(features[0])};
	}
	
	@Override
	protected AbstractConfiguration createConfiguration(InputStream configuration)
	{
		return new DEPConfiguration(configuration);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?,?> createComponentForCollect()
	{
		return null;
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?,?> createComponentForTrain(Object lexicons)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishDEPParser((DEPConfiguration)t_configuration, f_extractors, lexicons);
		default     : return new DefaultDEPParser((DEPConfiguration)t_configuration, f_extractors, lexicons);
		}
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?,?> createComponentForBootstrap(Object lexicons, StringModel[] models)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishDEPParser((DEPConfiguration)t_configuration, f_extractors, lexicons, models, true);
		default     : return new DefaultDEPParser((DEPConfiguration)t_configuration, f_extractors, lexicons, models, true);
		}
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?,?> createComponentForEvaluate(Object lexicons, StringModel[] models)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishDEPParser((DEPConfiguration)t_configuration, f_extractors, lexicons, models, false);
		default     : return new DefaultDEPParser((DEPConfiguration)t_configuration, f_extractors, lexicons, models, false);	
		}
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?,?> createComponentForDecode(byte[] models)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishDEPParser((DEPConfiguration)t_configuration, models);
		default     : return new DefaultDEPParser((DEPConfiguration)t_configuration, models);
		}
	}
}
