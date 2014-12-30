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
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractDEPParser extends AbstractStatisticalComponent<DEPLabel, DEPState, DEPEval, DEPFeatureExtractor> implements DEPTransition
{
	protected int[] is_desc;
	protected int[] is_root;
	protected int[] no_head;
	
	/** Creates a dependency parser for train. */
	public AbstractDEPParser(DEPFeatureExtractor[] extractors, Object[] lexicons)
	{
		super(extractors, lexicons, false, 1);
		init();
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
		DEPLabel label;
		
		IntArrayList isDesc = new IntArrayList();
		IntArrayList isRoot = new IntArrayList();
		IntArrayList noHead = new IntArrayList();
		
		for (i=0; i<size; i++)
		{
			label = new DEPLabel(labels[i]);
			
			if (label.isArc(T_NO))
				isDesc.add(i);
			
			if (label.isList(T_SHIFT))
				isRoot.add(i);
			
			if (!(label.isArc(T_NO) && label.isList(T_REDUCE)))
				noHead.add(i);
		}
		
		is_desc = isDesc.toArray(); Arrays.sort(is_desc);
		is_root = isRoot.toArray(); Arrays.sort(is_root);
		no_head = noHead.toArray(); Arrays.sort(no_head);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object[] getLexicons() {return null;}
	
	@Override
	public void setLexicons(Object[] lexicons) {}
	
//	====================================== EVAL ======================================

	protected void initEval()
	{
		c_eval = new DEPEval(false);
	}

//	====================================== PROCESS ======================================
	
	@Override
	public DEPState process(DEPTree tree)
	{
		DEPState state = new DEPState(tree, c_flag);
		List<StringInstance> instances = process(state);
		
		if (!isDecode())
		{
			if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
			else if (isEvaluate())		c_eval.countCorrect(tree, state.getOracle());
		}
		
		return state;
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(DEPState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected DEPLabel getAutoLabel(DEPState state, StringFeatureVector vector)
	{
		StringPrediction[] ps = getPredictions(state, vector);
		DEPLabel label = new DEPLabel(ps[0].getLabel());
		state.addSecondHead(ps, label);
		return label;
	}
	
	protected StringPrediction[] getPredictions(DEPState state, StringFeatureVector vector)
	{
		int[] indices = getLabelIndices(state);		
		return (indices != null) ? s_models[0].predictTop2(vector, indices) : s_models[0].predictTop2(vector);
	}
	
	protected int[] getLabelIndices(DEPState state)
	{
		DEPNode stack = state.getStack();
		DEPNode input = state.getInput();
		
		if (stack.getID() == DEPLib.ROOT_ID)
			return is_root;
		else if (stack.isDependentOf(input) || input.isDependentOf(stack))
			return is_desc;
		else if (!stack.hasHead())
			return no_head;
		else
			return null;
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