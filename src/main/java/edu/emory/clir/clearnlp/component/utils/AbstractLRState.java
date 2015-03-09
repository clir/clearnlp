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
package edu.emory.clir.clearnlp.component.utils;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractLRState extends AbstractState<String,String>
{
	protected int i_input;
	
//	====================================== INITIALIZATION ======================================
	
	public AbstractLRState(DEPTree tree, CFlag flag)
	{
		super(tree, flag);
		setInput(1);
	}
	
//	====================================== ORACLE/LABEL ======================================
	
	@Override
	protected void initOracle()
	{
		g_oracle = new String[t_size];
		
		for (int i=1; i<t_size; i++)
			g_oracle[i] = clearOracle(getNode(i));
	}
	
	@Override
	public void resetOracle()
	{
		for (int i=1; i<t_size; i++)
			setLabel(getNode(i), g_oracle[i]);
	}
	
	@Override
	public String getGoldLabel()
	{
		return g_oracle[i_input];
	}
	
	protected abstract String clearOracle(DEPNode node);
	
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
		setLabel(getInput(), label);
		i_input++;
	}
	
	@Override
	public boolean isTerminate()
	{
		return i_input >= t_size;
	}
	
	protected abstract void setLabel(DEPNode node, String label); 
}