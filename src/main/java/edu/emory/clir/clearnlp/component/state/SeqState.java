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
package edu.emory.clir.clearnlp.component.state;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class SeqState extends AbstractState<String>
{
	protected int i_input;

//	====================================== INITIALIZATION ======================================
	
	public SeqState(DEPTree tree, boolean decode)
	{
		super(tree, decode);
		init(tree);
	}
	
	private void init(DEPTree tree)
	{
		i_input = 1;
	}
	
//	====================================== LABEL ======================================

	@Override
	public String getGoldLabel()
	{
		return g_labels[i_input];
	}
	
//	====================================== TREE ======================================
	
	public DEPNode getNode(AbstractFeatureToken<?> token)
	{
		return getNode(token, i_input, 1, t_size);
	}
	
//	====================================== INPUT ======================================
	
	public DEPNode getInput()
	{
		return getNode(i_input);
	}
	
	public void setInput(int id)
	{
		i_input = id;
	}
	
//	====================================== TRANSITION ======================================

	/** Shifts the current point to the next node to process. */
	public void shift()
	{
		i_input++;
	}
	
	/** @return {@code true} if the tagging should be terminated. */
	public boolean isTerminate()
	{
		return i_input >= t_size;
	}
}