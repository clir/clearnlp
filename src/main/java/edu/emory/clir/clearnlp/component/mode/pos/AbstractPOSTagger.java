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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.evaluation.TagEval;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.configuration.POSTrainConfiguration;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractPOSTagger extends AbstractStatisticalComponent<String, POSState, TagEval, POSFeatureExtractor>
{
	private final int LEXICON_LOWER_SIMPLIFIED_WORD_FORM = 0;
	private final int LEXICON_AMBIGUITY_CLASS = 1;
	
	protected Set<String> s_lowerSimplifiedWordForms;
	protected Map<String,String> m_ambiguityClasses;
	protected POSCollector p_collector;

	/** Creates a pos tagger for collect. */
	public AbstractPOSTagger(POSTrainConfiguration config, POSFeatureExtractor[] extractors)
	{
		super(extractors);
		p_collector = new POSCollector(config);
	}
	
	/** Creates a pos tagger for train. */
	public AbstractPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons)
	{
		super(extractors, lexicons, false, 1);
	}
	
	/** Creates a pos tagger for bootstrap. */
	public AbstractPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models)
	{
		super(extractors, lexicons, models);
	}
	
	/** Creates a pos tagger for bootstrap or evaluate. */
	public AbstractPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, TagEval eval)
	{
		super(extractors, lexicons, models, eval);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractPOSTagger(ObjectInputStream in)
	{
		super(in);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object[] getLexicons()
	{
		if (s_lowerSimplifiedWordForms == null)
			finalizeLexicons();
		
		Object[] lexicons = new Object[2];
		
		lexicons[LEXICON_LOWER_SIMPLIFIED_WORD_FORM] = s_lowerSimplifiedWordForms;
		lexicons[LEXICON_AMBIGUITY_CLASS] = m_ambiguityClasses;
		
		return lexicons;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setLexicons(Object[] lexicons)
	{
		s_lowerSimplifiedWordForms = (Set<String>)lexicons[LEXICON_LOWER_SIMPLIFIED_WORD_FORM];
		m_ambiguityClasses = (Map<String,String>)lexicons[LEXICON_AMBIGUITY_CLASS];
	}
	
	private void finalizeLexicons()
	{
		s_lowerSimplifiedWordForms = p_collector.finalizeLowerSimplifiedWordForms();
		m_ambiguityClasses = p_collector.finalizeAmbiguityClasses(s_lowerSimplifiedWordForms);
	}

//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		POSState state = new POSState(tree, isDecode());
		
		if (isCollect())
		{
			p_collector.collect(state);
		}
		else
		{
			List<StringInstance> instances = process(state);
			if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
			else if (isEvaluate())		c_eval.countCorrect(tree, state.getGoldLabels());
		}
	}
	
	private List<StringInstance> process(POSState state)
	{
		List<StringInstance> instances = isTrainOrBootstrap() ? new ArrayList<StringInstance>() : null;
		
		while (!state.isTerminate())
		{
			process(state, instances);
			state.shift();
		}
		
		return instances;
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(POSState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected String getAutoLabel(StringFeatureVector vector)
	{
		return s_models[0].predictBest(vector).getLabel();
	}
}