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
package edu.emory.clir.clearnlp.component.mode.sentiment;

import java.util.List;

import edu.emory.clir.clearnlp.component.utils.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SAState extends AbstractState<String,String>
{
	protected List<DEPNode> d_nodes;
	protected int i_input;
	
//	====================================== INITIALIZATION ======================================
	
	public SAState(DEPTree tree, CFlag flag)
	{
		super(tree, flag);

		d_nodes = tree.getDepthFirstNodeList();
		i_input = 0;
	}
	
//	====================================== ORACLE/LABEL ======================================
	
	@Override
	protected void initOracle()
	{
		g_oracle = new String[t_size];
		
		for (int i=0; i<t_size; i++)
			g_oracle[i] = clearOracle(getNode(i));
	}
	
	@Override
	public void resetOracle()
	{
		for (int i=0; i<t_size; i++)
			setLabel(getNode(i), g_oracle[i]);
	}
	
	@Override
	public String getGoldLabel()
	{
		return g_oracle[i_input];
	}
	
	protected String clearOracle(DEPNode node)
	{
		return node.removeFeat(DEPLib.FEAT_SA);
	}

//	====================================== NODE ======================================
	
	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		int id = getInput().getID() + token.getOffset();
		return (0 < id) ? getNodeRelation(token, getNode(id)) : null;
	}
	
	public DEPNode getInput()
	{
		return d_nodes.get(i_input);
	}
	
//	====================================== TRANSITION ======================================
	
	@Override
	public void next(String label)
	{
		setLabel(getInput(), label);
		i_input++;
	}
	
	@Override
	public boolean isTerminate()
	{
		return i_input >= t_size;
	}
	
	protected void setLabel(DEPNode node, String label)
	{
		node.putFeat(DEPLib.FEAT_SA, label);
	}
	
//	====================================== FEATURES ======================================
	
	public boolean extractWordFormFeature(DEPNode node)
	{
		return true;
	}
}