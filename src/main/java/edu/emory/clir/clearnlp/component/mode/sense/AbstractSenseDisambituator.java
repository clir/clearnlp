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
package edu.emory.clir.clearnlp.component.mode.sense;

import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.pos.POSEval;
import edu.emory.clir.clearnlp.component.mode.pos.POSFeatureExtractor;
import edu.emory.clir.clearnlp.component.mode.pos.POSState;
import edu.emory.clir.clearnlp.component.mode.pos.POSTrainConfiguration;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractSenseDisambituator extends AbstractStatisticalComponent<String, POSState, POSEval, POSFeatureExtractor>
{
	static public final int LEXICON_AMBIGUITY_CLASS = 0;
	static public final int LEXICON_PROPER_NOUNS    = 1;
	
	private Map<String,String> m_ambiguity_classes;

	/** Creates a pos tagger for collect. */
	public AbstractSenseDisambituator(POSTrainConfiguration configuration)
	{
		super();
	}
	
	/** Creates a pos tagger for train. */
	public AbstractSenseDisambituator(POSFeatureExtractor[] extractors, Object lexicons)
	{
		super(extractors, lexicons, false, 1);
	}
	
	/** Creates a pos tagger for bootstrap or evaluate. */
	public AbstractSenseDisambituator(POSFeatureExtractor[] extractors, Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(extractors, lexicons, models, bootstrap);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractSenseDisambituator(ObjectInputStream in)
	{
		super(in);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractSenseDisambituator(byte[] models)
	{
		super(models);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object[] getLexicons()
	{
		return null;
	}
	
	@Override
	public void setLexicons(Object lexicons)
	{
		
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