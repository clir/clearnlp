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
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.dep.state.DEPState;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractDEPParser extends AbstractStatisticalComponent<DEPLabel, DEPState, DEPEval, DEPFeatureExtractor> implements DEPTransition
{
	private int[][] label_indices;
	private int beam_size;
	
//	protected int[] is_desc;
//	protected int[] is_root;
//	protected int[] no_head;
	
	/** Creates a dependency parser for train. */
	public AbstractDEPParser(DEPFeatureExtractor[] extractors, Object[] lexicons)
	{
		super(extractors, lexicons, false, 1);
		init(1);
	}
	
	/** Creates a dependency parser for bootstrap or evaluate. */
	public AbstractDEPParser(DEPFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, boolean bootstrap, int beamSize)
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
		List<StringInstance> instances = isTrainOrBootstrap() ? new ArrayList<>() : null;
		DEPState state = new DEPState(tree, c_flag, beam_size);
		process(state, instances);
		
		if (state.startBranching())
		{
			while (state.nextBranch()) process(state, instances);
			state.mergeBranches();	
		}
		
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