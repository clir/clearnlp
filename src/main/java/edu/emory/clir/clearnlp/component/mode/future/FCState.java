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
package edu.emory.clir.clearnlp.component.mode.future;

import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FCState extends AbstractState<String,String>
{
	protected final String feat_key = DEPLib.FEAT_FUTURE;
	private boolean terminate;
	
//	====================================== INITIALIZATION ======================================
	
	public FCState(DEPTree tree, CFlag flag)
	{
		super(tree, flag);
		terminate = false;
	}
	
//	====================================== ORACLE/LABEL ======================================
	
	@Override
	protected void initOracle()
	{
		g_oracle = new String[]{getNode(FCEval.INFO_NODE).removeFeat(feat_key)};
	}
	
	@Override
	public void resetOracle()
	{
		setLabel(g_oracle[0]);
	}
	
	@Override
	public String getGoldLabel()
	{
		return g_oracle[0];
	}
	
//	====================================== NODE ======================================
	
	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		return null;
	}

//	====================================== TRANSITION ======================================
	
	@Override
	public void next(String label)
	{
		setLabel(label);
		terminate = true;
	}
	
	@Override
	public boolean isTerminate()
	{
		return terminate;
	}
	
	public void setLabel(String label)
	{
		getNode(FCEval.INFO_NODE).putFeat(feat_key, label);
	}
}