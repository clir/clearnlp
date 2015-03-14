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
package edu.emory.clir.clearnlp.component.mode.pos;

import java.io.ObjectInputStream;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractPOSTagger extends AbstractStatisticalComponent<String, POSState, POSEval, POSFeatureExtractor>
{
	private POSLexicon pos_lexicon;
	
	/** Creates a pos tagger for collect. */
	public AbstractPOSTagger(POSConfiguration configuration)
	{
		super(configuration);
		t_configuration = configuration;
		pos_lexicon = new POSLexicon(configuration.getProperNounTagset());
	}
	
	/** Creates a pos tagger for train. */
	public AbstractPOSTagger(POSFeatureExtractor[] extractors, Object lexicons)
	{
		super(null, extractors, lexicons, false, 1);
	}
	
	/** Creates a pos tagger for bootstrap or evaluate. */
	public AbstractPOSTagger(POSFeatureExtractor[] extractors, Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(null, extractors, lexicons, models, bootstrap);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractPOSTagger(ObjectInputStream in)
	{
		super(null, in);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractPOSTagger(byte[] models)
	{
		super(null, models);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object getLexicons()
	{
		if (t_configuration != null)
			pos_lexicon.finalizeAmbiguityClassFeatures(((POSConfiguration)t_configuration).getAmbiguityClassThreshold());
		
		return pos_lexicon;
	}
	
	@Override
	public void setLexicons(Object lexicons)
	{
		pos_lexicon = (POSLexicon)lexicons;
	}
	
//	====================================== EVAL ======================================

	@Override
	protected void initEval()
	{
		c_eval = new POSEval();
	}
	
//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		POSState state = new POSState(tree, c_flag, pos_lexicon);
		
		if (isCollect())
		{
			pos_lexicon.collect(state);
		}
		else
		{
			List<StringInstance> instances = process(state);
			
			if (isTrainOrBootstrap())
				s_models[0].addInstances(instances);
			else 
			{
				if (isEvaluate()) c_eval.countCorrect(tree, state.getOracle());
				postProcess(state);
			}
		}
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(POSState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected String getAutoLabel(POSState state, StringFeatureVector vector)
	{
		StringPrediction[] ps = s_models[0].predictTop2(vector);
		state.save2ndLabel(ps);
		return ps[0].getLabel();
	}
	
	abstract void postProcess(POSState state);
	
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