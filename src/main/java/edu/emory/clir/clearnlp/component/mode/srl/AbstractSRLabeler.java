/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.component.mode.srl;

import java.io.ObjectInputStream;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.srl.state.AbstractSRLState;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractSRLabeler extends AbstractStatisticalComponent<String, AbstractSRLState, SRLEval, SRLFeatureExtractor>
{
	/** Creates a semantic role labeler for train. */
	public AbstractSRLabeler(SRLFeatureExtractor[] extractors, Object lexicons)
	{
		super(null, extractors, lexicons, false, 2);
	}
	
	/** Creates a semantic role labeler for bootstrap or evaluate. */
	public AbstractSRLabeler(SRLFeatureExtractor[] extractors, Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(null, extractors, lexicons, models, bootstrap);
	}
	
	/** Creates a semantic role labeler for decode. */
	public AbstractSRLabeler(ObjectInputStream in)
	{
		super(null, in);
	}
	
	/** Creates a semantic role labeler for decode. */
	public AbstractSRLabeler(byte[] models)
	{
		super(null, models);
	}
	
//	====================================== LEXICONS ======================================

	@Override
	public Object getLexicons() {return null;}

	@Override
	public void setLexicons(Object lexicons) {}
	
//	====================================== EVAL ======================================

	@Override
	protected void initEval()
	{
		c_eval = new SRLEval();
	}

//	====================================== PROCESS ======================================

	@Override
	public void process(DEPTree tree)
	{
		AbstractSRLState state = getState(tree);
		List<StringInstance> instances = process(state);
		
		if (isTrainOrBootstrap())
			addInstances(state, instances);
		else if (isEvaluate())
			c_eval.countCorrect(tree, state.getOracle());
	}
	
	protected abstract AbstractSRLState getState(DEPTree tree);
	
	private void addInstances(AbstractSRLState state, List<StringInstance> instances)
	{
		int idx;
		
		for (StringInstance instance : instances)
		{
			idx = Integer.parseInt(instance.getLabel().substring(0, 1));
			s_models[idx].addInstances(instances);
		}
	}
	
	@Override
	protected StringFeatureVector createStringFeatureVector(AbstractSRLState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}

	@Override
	protected String getAutoLabel(AbstractSRLState state, StringFeatureVector vector)
	{
		StringPrediction p = s_models[state.getModelIndex()].predictBest(vector);
		return p.getLabel();
	}

//	====================================== ONLINE TRAIN ======================================

	@Override
	public void onlineTrain(List<DEPTree> trees)
	{
	}

	@Override
	protected void onlineLexicons(DEPTree tree)
	{
	}
}
