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
package com.clearnlp.component.state;

import com.clearnlp.classification.feature.AbstractFeatureToken;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class CommonTaggingState extends AbstractState<String,String>
{
	protected int i_input;

//	====================================== INITIALIZATION ======================================
	
	public CommonTaggingState(DEPTree tree)
	{
		super(tree);
	}
 	
	@Override
	protected void init(DEPTree tree)
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