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
package com.clearnlp.classification.configuration;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultTrainConfiguration extends AbstractTrainConfiguration
{
	private int i_labelCutoff;
	private int i_featureCutoff;
	private int i_numberOfThreads;
	
	public DefaultTrainConfiguration(byte vectorType, boolean binary, int labelCutoff, int featureCutoff, int numberOfThreads)
	{
		super(vectorType, binary);
		setLabelCutoff(labelCutoff);
		setFeatureCutoff(featureCutoff);
		setNumberOfThreads(numberOfThreads);
	}

	public int getLabelCutoff()
	{
		return i_labelCutoff;
	}
	
	public int getFeatureCutoff()
	{
		return i_featureCutoff;
	}
	
	public int getNumberOfThreads()
	{
		return i_numberOfThreads;
	}

	public void setLabelCutoff(int labelCutoff)
	{
		i_labelCutoff = labelCutoff;
	}

	public void setFeatureCutoff(int featureCutoff)
	{
		i_featureCutoff = featureCutoff;
	}

	public void setNumberOfThreads(int numberOfThreads)
	{
		i_numberOfThreads = numberOfThreads;
	}
}