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
package edu.emory.clir.clearnlp.nlp.factory;

import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.CFlag;
import edu.emory.clir.clearnlp.component.evaluation.TagEval;
import edu.emory.clir.clearnlp.component.mode.pos.DefaultPOSTagger;
import edu.emory.clir.clearnlp.component.mode.pos.POSFeatureExtractor;
import edu.emory.clir.clearnlp.nlp.configuration.POSTrainConfiguration;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSFactory extends AbstractNLPFactory
{
	public POSFactory(String configurationFile)
	{
		super(new POSTrainConfiguration(IOUtils.createFileInputStream(configurationFile)));
	}
	
	public AbstractStatisticalComponent<?,?,?,?> createComponent(String[] featureFiles)
	{
		POSFeatureExtractor[] extractors = {new POSFeatureExtractor(IOUtils.createFileInputStream(featureFiles[0]))};
		return new DefaultPOSTagger((POSTrainConfiguration)t_configuration, extractors);
	}
	
	public AbstractStatisticalComponent<?,?,?,?> createComponent(AbstractStatisticalComponent<?,?,?,?> component, CFlag flag)
	{
		POSFeatureExtractor[] extractors = (POSFeatureExtractor[])component.getFeatureExtractors();
		
		switch (flag)
		{
		case TRAIN    : return new DefaultPOSTagger(extractors, component.getLexicons());
		case BOOTSTRAP: return new DefaultPOSTagger(extractors, component.getLexicons(), component.getModels());
		case EVALUATE : return new DefaultPOSTagger(extractors, component.getLexicons(), component.getModels(), new TagEval());
		default       : throw new IllegalArgumentException("Invalid flag: "+flag);
		}
	}
}
