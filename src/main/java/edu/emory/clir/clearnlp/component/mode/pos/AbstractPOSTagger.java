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
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.configuration.POSTrainConfiguration;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractPOSTagger extends AbstractStatisticalComponent<String, POSState, POSEval, POSFeatureExtractor>
{
	static public final int LEXICON_LOWER_SIMPLIFIED_WORD_FORM = 0;
	static public final int LEXICON_AMBIGUITY_CLASS = 1;
	
	private Set<String> s_lowerSimplifiedWordForms;
	private Map<String,String> m_ambiguityClasses;
	private POSCollector p_collector;

	/** Creates a pos tagger for collect. */
	public AbstractPOSTagger(POSTrainConfiguration configuration)
	{
		super();
		p_collector = new POSCollector(configuration);
	}
	
	/** Creates a pos tagger for train. */
	public AbstractPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons)
	{
		super(extractors, lexicons, false, 1);
	}
	
	/** Creates a pos tagger for bootstrap or evaluate. */
	public AbstractPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, boolean bootstrap)
	{
		super(extractors, lexicons, models, bootstrap);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractPOSTagger(ObjectInputStream in)
	{
		super(in);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractPOSTagger(byte[] models)
	{
		super(models);
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
	
//	====================================== EVAL ======================================

	protected void initEval()
	{
		c_eval = new POSEval();
	}

//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		POSState state = new POSState(tree, c_flag, s_lowerSimplifiedWordForms, m_ambiguityClasses);
		
		if (isCollect())
		{
			p_collector.collect(state);
		}
		else
		{
			List<StringInstance> instances = process(state);
			
			if (!isDecode())
			{
				if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
				else if (isEvaluate())		c_eval.countCorrect(tree, state.getOracle());
				state.resetOracle();
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
		for (DEPNode node : tree)
		{
			String simplifiedForm = node.getSimplifiedWordForm();
			String ambiguityClass = m_ambiguityClasses.get(simplifiedForm);
			String pos = node.getPOSTag();
			
			if (ambiguityClass == null)
				m_ambiguityClasses.put(simplifiedForm, pos);
			else if (!ambiguityClass.startsWith(StringConst.UNDERSCORE+pos) && !ambiguityClass.startsWith(pos+StringConst.UNDERSCORE) && !ambiguityClass.startsWith(StringConst.UNDERSCORE+pos+StringConst.UNDERSCORE))
				m_ambiguityClasses.put(simplifiedForm, pos+StringConst.UNDERSCORE+ambiguityClass);
			
			s_lowerSimplifiedWordForms.add(StringUtils.toLowerCase(simplifiedForm));
		}		
	}
}