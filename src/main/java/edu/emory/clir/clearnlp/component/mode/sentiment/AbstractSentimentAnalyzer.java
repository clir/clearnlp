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
package edu.emory.clir.clearnlp.component.mode.sentiment;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.dep.DEPTransition;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractSentimentAnalyzer extends AbstractStatisticalComponent<String, SAState, SAEval, SAFeatureExtractor> implements DEPTransition
{
	/** Creates a sentiment analyzer for train. */
	public AbstractSentimentAnalyzer(SAFeatureExtractor[] extractors, Object[] lexicons)
	{
		super(extractors, lexicons, false, 1);
	}
	
	/** Creates a sentiment analyzer for bootstrap or evaluate. */
	public AbstractSentimentAnalyzer(SAFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, boolean bootstrap)
	{
		super(extractors, lexicons, models, bootstrap);
	}
	
	/** Creates a sentiment analyzer for decode. */
	public AbstractSentimentAnalyzer(ObjectInputStream in)
	{
		super(in);
	}
	
	/** Creates a sentiment analyzer for decode. */
	public AbstractSentimentAnalyzer(byte[] models)
	{
		super(models);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object[] getLexicons() {return null;}
	
	@Override
	public void setLexicons(Object[] lexicons) {}
	
//	====================================== EVAL ======================================

	protected void initEval()
	{
		c_eval = new SAEval();
	}

//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		List<StringInstance> instances = isTrainOrBootstrap() ? new ArrayList<>() : null;
		SAState state = new SAState(tree, c_flag);
		process(state, instances);
		
		if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
		else if (isEvaluate())		c_eval.countCorrect(tree, state.getOracle());
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(SAState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected String getAutoLabel(SAState state, StringFeatureVector vector)
	{
		return s_models[0].predictBest(vector).getLabel();
	}
	
//	====================================== ONLINE TRAIN ======================================
	
	@Override
	public void onlineTrain(List<DEPTree> trees)
	{
		onlineTrainSingleAdaGrad(trees);
	}
	
	@Override
	protected void onlineLexicons(DEPTree tree)
	{
	}
}