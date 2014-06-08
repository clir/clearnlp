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
package com.clearnlp.component.common;

import java.io.ObjectInputStream;

import com.clearnlp.classification.instance.StringInstance;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.prediction.StringPrediction;
import com.clearnlp.classification.vector.StringFeatureVector;
import com.clearnlp.component.AbstractStatisticalComponent;
import com.clearnlp.component.state.CommonTaggingState;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.feature.common.CommonFeatureExtractor;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DefaultNamedEntityTagger extends AbstractStatisticalComponent<CommonFeatureExtractor>
{
	private CommonFeatureExtractor f_extractor;
	private StringModel s_model;
	
	public DefaultNamedEntityTagger(CommonFeatureExtractor[] extractors)
	{
		super(extractors);
		f_extractor = f_extractors[0];
	}
	
	public DefaultNamedEntityTagger(CommonFeatureExtractor[] extractors, Object[] lexicons, boolean binary, int modelSize)
	{
		super(extractors, lexicons, binary, modelSize);
		init();
	}
	
	public DefaultNamedEntityTagger(ObjectInputStream in)
	{
		super(in);
		init();
	}
	
	private void init()
	{
		f_extractor = f_extractors[0];
		s_model     = s_models[0];
	}

	@Override
	public void process(DEPTree tree)
	{
		CommonTaggingState state = new CommonTaggingState(tree);
		StringFeatureVector vector;
		StringPrediction p;
		
		state.setGoldLabels(tree.getNamedEntityTags());
		
		while (state.isTerminate())
		{
			vector = f_extractor.createStringFeatureVector(state);
			s_model.addInstance(new StringInstance(state.getGoldLabel(), vector));
			p = s_model.predictBest(vector);
			state.getInput().setNamedEntityTag(p.getLabel());
			state.shift();
		}
	}

	@Override
	public Object[] getLexicons() {return null;}

	@Override
	public void setLexicons(Object[] lexicons) {}
}
