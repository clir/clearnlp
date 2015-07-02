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
package edu.emory.clir.clearnlp.component.mode.srl;

import java.io.InputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.trainer.AbstractNLPTrainer;

/**
 * @since 3.2.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLTrainer extends AbstractNLPTrainer
{
	protected SRLFeatureExtractor[] f_extractors;
	
	public SRLTrainer(InputStream configuration)
	{
		super(configuration);
	}
	
	public SRLTrainer(InputStream configuration, InputStream[] features)
	{
		super(configuration);
		f_extractors = new SRLFeatureExtractor[]{new SRLFeatureExtractor(features[0])};
	}
	
	@Override
	protected AbstractConfiguration createConfiguration(InputStream configuration)
	{
		return new SRLConfiguration(configuration);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForCollect()
	{
		return null;
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForTrain(Object lexicons)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishSRLabeler(f_extractors, lexicons);
		default: return null;
		}
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForBootstrap(Object lexicons, StringModel[] models)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishSRLabeler(f_extractors, lexicons, models, true);
		default: return null;
		}
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForEvaluate(Object lexicons, StringModel[] models)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishSRLabeler(f_extractors, lexicons, models, false);
		default: return null;	
		}
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForDecode(byte[] models)
	{
		switch (t_configuration.getLanguage())
		{
		case ENGLISH: return new EnglishSRLabeler(models);
		default: return null;
		}
	}
}
