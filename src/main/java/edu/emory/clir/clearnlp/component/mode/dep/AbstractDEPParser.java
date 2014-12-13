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
package edu.emory.clir.clearnlp.component.mode.dep;

import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractDEPParser extends AbstractStatisticalComponent<DEPLabel, DEPState, DEPEval, DEPFeatureExtractor> implements DEPTransition
{
	private int[] not_leftArc;
	private int[] not_rightArc;
	
	/** Creates a dependency parser for train. */
	public AbstractDEPParser(DEPFeatureExtractor[] extractors, Object[] lexicons)
	{
		super(extractors, lexicons, false, 1);
	}
	
	/** Creates a dependency parser for bootstrap or evaluate. */
	public AbstractDEPParser(DEPFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, boolean bootstrap)
	{
		super(extractors, lexicons, models, bootstrap);
		init();
	}
	
	/** Creates a dependency parser for decode. */
	public AbstractDEPParser(ObjectInputStream in)
	{
		super(in);
		init();
	}
	
	/** Creates a dependency parser for decode. */
	public AbstractDEPParser(byte[] models)
	{
		super(models);
		init();
	}
	
	private void init()
	{
		String[] labels = s_models[0].getLabels();
		int i, size = labels.length;
		String label;
		
		IntArrayList nl = new IntArrayList();
		IntArrayList nr = new IntArrayList();
		
		for (i=0; i<size; i++)
		{
			label = labels[i];
			
			if (!label.startsWith(T_LEFT))
				nl.add(i);
			if (!label.startsWith(T_RIGHT))
				nr.add(i);
		}
		
		not_leftArc  = nl.toArray(); Arrays.sort(not_leftArc);
		not_rightArc = nr.toArray(); Arrays.sort(not_rightArc);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object[] getLexicons() {return null;}
	
	@Override
	public void setLexicons(Object[] lexicons) {}
	
//	====================================== EVAL ======================================

	protected void initEval()
	{
		c_eval = new DEPEval();
	}

//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		DEPState state = new DEPState(tree, c_flag);
		List<StringInstance> instances = process(state);
		
		if (!isDecode())
		{
			if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
			else if (isEvaluate())		c_eval.countCorrect(tree, state.getOracle());
			state.resetOracle();
		}
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(DEPState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected DEPLabel getAutoLabel(DEPState state, StringFeatureVector vector)
	{
		StringPrediction[] p = getPredictions(state, vector);
		DEPLabel label = new DEPLabel(p[0].getLabel());
		
		if (label.isArc(T_NO))
		{
		
		}
		
		return label;
	}
	
	protected StringPrediction[] getPredictions(DEPState state, StringFeatureVector vector)
	{
		DEPNode stack = state.getStack();
		DEPNode input = state.getInput();
		
		if (stack.getID() == 0 || input.isDescendantOf(stack))
			return s_models[0].predictAll(vector, not_leftArc);
		else if (stack.isDescendantOf(input))
			return s_models[0].predictAll(vector, not_rightArc);
		else
			return s_models[0].predictAll(vector);
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