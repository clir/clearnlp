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
package edu.emory.clir.clearnlp.component.mode.sequence;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractSequenceClassifier extends AbstractStatisticalComponent<String, SeqState, SeqEval, SeqFeatureExtractor>
{
	protected SeqTrainConfiguration t_configuration;
	
	/** Creates a sequence classifier for train. */
	public AbstractSequenceClassifier(SeqFeatureExtractor[] extractors, Object[] lexicons, SeqTrainConfiguration configuration)
	{
		super(extractors, lexicons, false, 1);
		init(configuration);
	}
	
	/** Creates a sequence classifier for bootstrap or evaluate. */
	public AbstractSequenceClassifier(SeqFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, boolean bootstrap, SeqTrainConfiguration configuration)
	{
		super(extractors, lexicons, models, bootstrap);
		init(configuration);
	}
	
	/** Creates a sequence classifier for decode. */
	public AbstractSequenceClassifier(ObjectInputStream in)
	{
		super(in);
	}
	
	/** Creates a sequence classifier for decode. */
	public AbstractSequenceClassifier(byte[] models)
	{
		super(models);
	}
	
	private void init(SeqTrainConfiguration configuration)
	{
		t_configuration = configuration; 
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object[] getLexicons()
	{
		return null;
	}
	
	@Override
	public void setLexicons(Object[] lexicons) {}
	
//	====================================== EVAL ======================================

	@Override
	protected void initEval()
	{
		c_eval = new SeqEval(t_configuration.isTokenBased());
	}
	
//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		SeqState state = new SeqState(tree, c_flag);
		
		List<StringInstance> instances = isTrainOrBootstrap() ? new ArrayList<>() : null;
		process(state, instances);
		
		if (!isDecode())
		{
			if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
			else if (isEvaluate())		c_eval.countCorrect(tree, state.getOracle());
		}
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(SeqState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected String getAutoLabel(SeqState state, StringFeatureVector vector)
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