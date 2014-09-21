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
package com.clearnlp.nlp.factory;

import com.clearnlp.component.AbstractStatisticalComponent;
import com.clearnlp.component.CFlag;
import com.clearnlp.nlp.configuration.AbstractTrainConfiguration;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractNLPFactory
{
	protected AbstractTrainConfiguration t_configuration;
	
	public AbstractNLPFactory(AbstractTrainConfiguration configuration)
	{
		t_configuration = configuration;
	}
	
	public abstract AbstractStatisticalComponent<?,?,?,?> createComponent(String[] featureFiles);
	public abstract AbstractStatisticalComponent<?,?,?,?> createComponent(AbstractStatisticalComponent<?,?,?,?> component, CFlag flag);

	public AbstractTrainConfiguration getTrainConfiguration()
	{
		return t_configuration;
	}
}
