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

import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SenseState extends AbstractState<String,String>
{
	Map<String,String> mononymous_senses;
	private String feat_key;
	private int i_input;
	
//	====================================== INITIALIZATION ======================================
	
	public SenseState(DEPTree tree, CFlag flag, Map<String,String> mononymousSenses, String featKey)
	{
		super(tree, flag);
		init(mononymousSenses, featKey);
	}
	
	private void init(Map<String,String> monoSenses, String featKey)
	{
		mononymous_senses = monoSenses;
		feat_key = featKey;
		setInput(0);
	}
	
//	====================================== ORACLE/LABEL ======================================
	
	@Override
	protected void initOracle()
	{
		g_oracle = d_tree.getFeatureTags(feat_key);
		d_tree.clearFeatureTags(feat_key);
	}

	@Override
	public void resetOracle()
	{
		d_tree.setFeatureTags(feat_key, g_oracle);
	}

	@Override
	public String getGoldLabel()
	{
		return g_oracle[i_input];
	}
	
//	====================================== NODE ======================================

	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		int id = i_input + token.getOffset();
		return (0 < id) ? getNodeRelation(token, getNode(id)) : null;
	}
	
	public DEPNode getInput()
	{
		return getNode(i_input);
	}
	
	public void setInput(int id)
	{
		i_input = id;
	}
	
//	====================================== TRANSITION ======================================
	
	@Override
	public void next(String label)
	{
		getInput().putFeat(feat_key, label);
	}
	
	public void shift()
	{
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
	
//	====================================== FEATURES ======================================

	@Override
	public boolean extractWordFormFeature(DEPNode node)
	{
		return true;
	}

	public String getSense(DEPNode node)
	{
		return node.getFeat(feat_key);
	}	
}