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
package edu.emory.clir.clearnlp.component.mode.ner;

import java.io.ObjectInputStream;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureExtractor;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractNERecognizer extends AbstractStatisticalComponent<String, NERState, NEREval, CommonFeatureExtractor<NERState>, NERConfiguration>
{
	private NERLexicon ner_lexicon;
	public AbstractNERecognizer() {};
	
	/** Creates a named entity recognizer for collect. */
	public AbstractNERecognizer(NERConfiguration configuration)
	{
		super(configuration);
		ner_lexicon = new NERLexicon(configuration);
	}
	
	/** Creates a named entity recognizer for train. */
	public AbstractNERecognizer(CommonFeatureExtractor<NERState>[] extractors, Object lexicons)
	{
		super(null, extractors, lexicons, false, 1);
	}
	
	/** Creates a named entity recognizer for bootstrap or evaluate. */
	public AbstractNERecognizer(CommonFeatureExtractor<NERState>[] extractors, Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(null, extractors, lexicons, models, bootstrap);
	}
	
	/** Creates a named entity recognizer for decode. */
	public AbstractNERecognizer(ObjectInputStream in)
	{
		super(null, in);
	}
	
	/** Creates a named entity recognizer for decode. */
	public AbstractNERecognizer(byte[] models)
	{
		super(null, models);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object getLexicons()
	{
		if (isCollect()) ner_lexicon.populateDictionary();
		return null;
	}
	
	@Override
	public void setLexicons(Object lexicons)
	{
		ner_lexicon = (NERLexicon)lexicons;
	}
	
//	====================================== EVAL ======================================

	@Override
	protected void initEval()
	{
		c_eval = new NEREval();
	}
	
//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		NERState state = new NERState(tree, c_flag, GlobalLexica.getNamedEntityDictionary());
		
		if (isCollect())
		{
			ner_lexicon.collect(tree);
		}
		else
		{
			List<StringInstance> instances = process(state);
			
			if (isTrainOrBootstrap())
				s_models[0].addInstances(instances);
			else
			{
				state.postProcess();
				if (isEvaluate()) c_eval.countCorrect(tree, state.getOracle());
			}
		}
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(NERState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected String getAutoLabel(NERState state, StringFeatureVector vector)
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
	protected void onlineLexicons(DEPTree tree) {}
}