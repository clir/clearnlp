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
package edu.emory.clir.clearnlp.component.mode.future;

import java.io.InputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.trainer.AbstractNLPTrainer;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FCTrainer extends AbstractNLPTrainer
{
	public FCTrainer(InputStream configuration)
	{
		super(configuration);
	}
	
	@Override
	protected AbstractConfiguration createConfiguration(InputStream configuration)
	{
		return new FCConfiguration(configuration);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForCollect()
	{
		return null;
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForTrain(Object lexicons)
	{
		return new DefaultFutureClassifier(lexicons);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForBootstrap(Object lexicons, StringModel[] models)
	{
		return new DefaultFutureClassifier(lexicons, models, true);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForEvaluate(Object lexicons, StringModel[] models)
	{
		return new DefaultFutureClassifier(lexicons, models, false);
	}
	
	@Override
	protected AbstractStatisticalComponent<?,?,?,?> createComponentForDecode(byte[] models)
	{
		return new DefaultFutureClassifier(models);
	}
}
