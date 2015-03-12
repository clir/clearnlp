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
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.dep.state.DEPState;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractDEPParser extends AbstractStatisticalComponent<DEPLabel, DEPState, DEPEval, DEPFeatureExtractor> implements DEPTransition
{
	private int[][] label_indices;
	private int beam_size;
	
	/** Creates a dependency parser for train. */
	public AbstractDEPParser(DEPFeatureExtractor[] extractors, Object lexicons)
	{
		super(extractors, lexicons, false, 1);
		init(1);
	}
	
	/** Creates a dependency parser for bootstrap or evaluate. */
	public AbstractDEPParser(DEPFeatureExtractor[] extractors, Object lexicons, StringModel[] models, boolean bootstrap, int beamSize)
	{
		super(extractors, lexicons, models, bootstrap);
		init(beamSize);
	}
	
	/** Creates a dependency parser for decode. */
	public AbstractDEPParser(ObjectInputStream in, int beamSize)
	{
		super(in);
		init(beamSize);
	}
	
	/** Creates a dependency parser for decode. */
	public AbstractDEPParser(byte[] models, int beamSize)
	{
		super(models);
		init(beamSize);
	}
	
	private void init(int beamSize)
	{
		label_indices = new DEPState().initLabelIndices(s_models[0].getLabels());
		setBeamSize(beamSize);
	}
	
	public int getBeamSize()
	{
		return beam_size;
	}
	
	public void setBeamSize(int size)
	{
		beam_size = size;
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object getLexicons() {return null;}
	
	@Override
	public void setLexicons(Object lexicons) {}
	
//	====================================== EVAL ======================================

	protected void initEval()
	{
		c_eval = new DEPEval();
	}

//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		List<StringInstance> instances = isTrainOrBootstrap() ? new ArrayList<>() : null;
		DEPState state = new DEPState(tree, c_flag, beam_size);
		process(state, instances);
		
		if (state.startBranching())
		{
			while (state.nextBranch()) process(state, instances);
			state.mergeBranches();	
		}
		
		processHeadless(state);
		
		if (isTrainOrBootstrap())	s_models[0].addInstances(instances);
		else if (isEvaluate())		c_eval.countCorrect(tree, state.getOracle());
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
		DEPLabel autoLabel = new DEPLabel(ps[0]);
		state.saveBranch(ps, autoLabel);
		return autoLabel;
	}
	
	protected StringPrediction[] getPredictions(DEPState state, StringFeatureVector vector)
	{
		int[] indices = state.getLabelIndices(label_indices);		
		return (indices != null) ? s_models[0].predictTop2(vector, indices) : s_models[0].predictTop2(vector);
	}
	
//	====================================== POST-PROCESS ======================================
	
	private void processHeadless(DEPState state)
	{
		ObjectIntPair<StringPrediction> max;
		int i, size = state.getTreeSize();
		DEPNode node;
		
		for (i=1; i<size; i++)
		{
			node = state.getNode(i);
			
			if (!node.hasHead())
			{
				max = new ObjectIntPair<StringPrediction>(null, -1000);
				processHeadless(state, node, max, label_indices[DEPState.RIGHT_ARC], -1);
				processHeadless(state, node, max, label_indices[DEPState.LEFT_ARC] ,  1);
				
				if (max.o == null)
					node.setHead(state.getNode(0), DEPLibEn.DEP_ROOT);
				else
					node.setHead(state.getNode(max.i), new DEPLabel(max.o).getDeprel());
			}
		}
	}
	
	private void processHeadless(DEPState state, DEPNode node, ObjectIntPair<StringPrediction> max, int[] indices, int dir)
	{
		int i, currID = node.getID(), size = state.getTreeSize();
		StringFeatureVector vector;
		StringPrediction p;
		DEPNode head;
		
		for (i=currID+dir; 0 <= i&&i < size; i+=dir)
		{
			head = state.getNode(i);

			if (!head.isDescendantOf(node))
			{
				if (dir < 0)	state.reset(i, currID);
				else			state.reset(currID, i);
				vector = createStringFeatureVector(state);
				p = s_models[0].predictBest(vector, indices);
				if (max.o == null || max.o.compareTo(p) < 0) max.set(p, i);	
			}
		}
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