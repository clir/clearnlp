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

import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class TagState extends AbstractState
{
	private String[] g_labels;
	private int      i_input;

	public TagState(DEPTree tree)
	{
		super(tree);
		init (tree);
	}
 	
//	====================================== INITIALIZATION ======================================
	
	private void init(DEPTree tree)
	{
		i_input = 1;
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
	
//	====================================== LABELS ======================================

	@Override
	public String getGoldLabel()
	{
		return g_labels[i_input];
	}
	
	@Override
	public Object[] getGoldLabels()
	{
		return g_labels;
	}
	
	public void setGoldLabels(String[] labels)
	{
		g_labels = labels;
	}
	
//	====================================== BOOLEANS ======================================
	
	/** @return {@code true} if the current node is the first node in the tree. */
	public boolean isInputFirstNode()
	{
		return i_input == 1;
	}
	
	/** @return {@code true} if the current node is the last node in the tree. */
	public boolean isInputLastNode()
	{
		return false;
//		return i_input + 1 == t_size;
	}
	
//	====================================== TRANSITION ======================================

	/** Moves the current point to the next node to process. */
	public void moveForward()
	{
		i_input++;
	}
	
	/** Moves the current point to the previous node to process. */
	public void moveBackward()
	{
		i_input--;
	}
	
	/** @return {@code true} if the tagging should be terminated. */
	public boolean isTerminate()
	{
		return false;
//		return i_input >= t_size;
	}
	
//	====================================== NODES ======================================
	
//	public DEPNode getNode(FtrToken token)
//	{
//		return getNode(token, i_input, 0, t_size);
//	}
}