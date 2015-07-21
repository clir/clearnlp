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
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.dep.state.AbstractDEPState;
import edu.emory.clir.clearnlp.component.mode.dep.state.DEPStateBranch;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractDEPParser extends AbstractStatisticalComponent<DEPLabel, AbstractDEPState, DEPEval, DEPFeatureExtractor, DEPConfiguration> implements DEPTransition
{
	private int[][] label_indices;
	
	/** Creates a dependency parser for train. */
	public AbstractDEPParser(DEPConfiguration configuration, DEPFeatureExtractor[] extractors, Object lexicons)
	{
		super(configuration, extractors, lexicons, false, 1);
		init();
	}
	
	/** Creates a dependency parser for bootstrap or evaluate. */
	public AbstractDEPParser(DEPConfiguration configuration, DEPFeatureExtractor[] extractors, Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(configuration, extractors, lexicons, models, bootstrap);
		init();
	}
	
	/** Creates a dependency parser for decode. */
	public AbstractDEPParser(DEPConfiguration configuration, ObjectInputStream in)
	{
		super(configuration, in);
		init();
	}
	
	/** Creates a dependency parser for decode. */
	public AbstractDEPParser(DEPConfiguration configuration, byte[] models)
	{
		super(configuration, models);
		init();
	}
	
	private void init()
	{
		label_indices = AbstractDEPState.initLabelIndices(s_models[0].getLabels());
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object getLexicons() {return null;}
	
	@Override
	public void setLexicons(Object lexicons) {}
	
//	====================================== EVAL ======================================

	protected void initEval()
	{
		c_eval = new DEPEval(t_configuration.evaluatePunctuation());
	}
	
//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
		AbstractDEPState state = new DEPStateBranch(tree, c_flag, t_configuration);
		List<StringInstance> instances = process(state);
		
		if (state.startBranching())
		{
			while (state.nextBranch()) state.saveBest(process(state));
			List<StringInstance> tmp = state.setBest(); 
			if (tmp != null) instances.addAll(tmp);
		}
		
		if (isTrainOrBootstrap())
			s_models[0].addInstances(instances);
		else
		{
			processHeadless(state);
			if (isEvaluate()) c_eval.countCorrect(tree, state.getOracle());
		}
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(AbstractDEPState state)
	{
		return f_extractors[0].createStringFeatureVector(state);
	}
	
	@Override
	protected DEPLabel getAutoLabel(AbstractDEPState state, StringFeatureVector vector)
	{
		StringPrediction[] ps = getPredictions(state, vector);
		DEPLabel autoLabel = new DEPLabel(ps[0]);
		if (autoLabel.isArc(ARC_NO)) state.save2ndHead(ps);
		state.saveBranch(ps);
		return autoLabel;
	}
	
	protected StringPrediction[] getPredictions(AbstractDEPState state, StringFeatureVector vector)
	{
		int[] indices = state.getLabelIndices(label_indices);		
		StringPrediction[] ps = (indices != null) ? s_models[0].predictTop2(vector, indices) : s_models[0].predictTop2(vector);
		for (StringPrediction p : ps) p.setScore(1/(1+Math.exp(-p.getScore())));
		return ps;
	}
	
//	====================================== POST-PROCESS ======================================
	
	private void processHeadless(AbstractDEPState state)
	{
		ObjectIntPair<StringPrediction> max;
		int i, size = state.getTreeSize();
		DEPNode node;
		
		for (i=1; i<size; i++)
		{
			node = state.getNode(i);
			
			if (!node.hasHead() && !state.find2ndHead(node))
			{
				max = new ObjectIntPair<StringPrediction>(null, -1000);
				processHeadlessAll(state, node, max, label_indices[AbstractDEPState.RIGHT_ARC], -1);
				processHeadlessAll(state, node, max, label_indices[AbstractDEPState. LEFT_ARC] ,  1);
				
				if (max.o == null)
					node.setHead(state.getNode(0), t_configuration.getRootLabel());
				else
					node.setHead(state.getNode(max.i), new DEPLabel(max.o).getDeprel());
			}
		}
	}
	
	private void processHeadlessAll(AbstractDEPState state, DEPNode node, ObjectIntPair<StringPrediction> max, int[] indices, int dir)
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