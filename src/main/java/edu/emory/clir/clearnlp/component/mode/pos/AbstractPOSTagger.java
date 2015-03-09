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
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractPOSTagger extends AbstractStatisticalComponent<String, POSState, POSEval, POSFeatureExtractor>
{
	static public final int INDEX_AMBIGUITY_CLASS = 0;
	static public final int INDEX_PROPER_NOUNS    = 1;
	
	private Map<String,String> m_ambiguity_classes;
	private Set<String> s_proper_nouns;

	/** Creates a pos tagger for collect. */
	public AbstractPOSTagger(POSTrainConfiguration configuration)
	{
		super(new POSCollector(configuration));
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
		s_proper_nouns = null;
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
		if (m_ambiguity_classes == null)
			m_ambiguity_classes = ((POSCollector)l_collector).finalizeAmbiguityClasses();
		
		if (s_proper_nouns == null)
			s_proper_nouns = ((POSCollector)l_collector).finalizeProperNouns();
		
		Object[] lexicons = new Object[2];
		
		lexicons[INDEX_AMBIGUITY_CLASS] = m_ambiguity_classes;
		lexicons[INDEX_PROPER_NOUNS]    = s_proper_nouns;
		
		return lexicons;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setLexicons(Object[] lexicons)
	{
		m_ambiguity_classes = (Map<String,String>)lexicons[INDEX_AMBIGUITY_CLASS];
		s_proper_nouns      = (Set<String>)lexicons[INDEX_PROPER_NOUNS];
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
		POSState state = new POSState(tree, c_flag, m_ambiguity_classes, s_proper_nouns);
		
		if (isCollect())
		{
			l_collector.collect(state);
		}
		else
		{
			List<StringInstance> instances = isTrainOrBootstrap() ? new ArrayList<>() : null;
			process(state, instances);
			
			if (!isDecode())
			{
				if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
				else if (isEvaluate())		c_eval.countCorrect(tree, state.getOracle());
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
		String label = ps[0].getLabel();
		state.save2ndLabel(ps);
		return label;
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
			String ambiguityClass = m_ambiguity_classes.get(simplifiedForm);
			String pos = node.getPOSTag();
			
			if (ambiguityClass == null)
				m_ambiguity_classes.put(simplifiedForm, pos);
			else if (!ambiguityClass.startsWith(StringConst.UNDERSCORE+pos) && !ambiguityClass.startsWith(pos+StringConst.UNDERSCORE) && !ambiguityClass.startsWith(StringConst.UNDERSCORE+pos+StringConst.UNDERSCORE))
				m_ambiguity_classes.put(simplifiedForm, pos+StringConst.UNDERSCORE+ambiguityClass);
		}		
	}
}