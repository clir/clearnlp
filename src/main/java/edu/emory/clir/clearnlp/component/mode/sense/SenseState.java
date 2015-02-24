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

import java.util.Map;

import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.component.CFlag;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SenseState extends AbstractState<String,String>
{
	Map<String,String> mono_senses;
	private int i_input;
	
//	====================================== INITIALIZATION ======================================
	
	public SenseState(DEPTree tree, CFlag flag, Map<String,String> monoSenses)
	{
		super(tree, flag);
		init(monoSenses);
	}
	
	private void init(Map<String,String> monoSenses)
	{
		mono_senses = monoSenses;
	}
	
//	====================================== ORACLE/LABEL ======================================
	

//	====================================== TRANSITION ======================================
	
	protected void setLabel(DEPNode node, String label)
	{
		node.setPOSTag(label);
	}
	
	public void save2ndLabel(StringPrediction[] ps)
	{
		StringPrediction fst = ps[0];
		StringPrediction snd = ps[1];
		
		if (fst.getScore() - snd.getScore() < 1)
			getInput().putFeat(DEPLib.FEAT_POS2, snd.getLabel());
	}
	
//	====================================== FEATURES ======================================

	public String getSense(DEPNode node)
	{
		return node.getFeat("pb");
	}
	
	@Override
	public boolean extractWordFormFeature(DEPNode node)
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#initOracle()
	 */
	@Override
	protected void initOracle()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#resetOracle()
	 */
	@Override
	public void resetOracle()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#getGoldLabel()
	 */
	@Override
	public String getGoldLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#getNode(edu.emory.clir.clearnlp.feature.AbstractFeatureToken)
	 */
	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public DEPNode getInput()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#next(java.lang.Object)
	 */
	@Override
	public void next(String label)
	{
		setLabel(getInput(), label);
		String sense;
		
		for (++i_input; i_input<t_size; i_input++)
		{
			sense = getSense(getInput());
			if (sense != null) break;
		}
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#isTerminate()
	 */
	@Override
	public boolean isTerminate()
	{
		// TODO Auto-generated method stub
		return false;
	}
}