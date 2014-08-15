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
package com.clearnlp.component.pos;

import com.clearnlp.component.state.SeqState;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.util.StringUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class POSState extends SeqState
{
	private String[] s_lowerSimplifiedWordForms;
	
//	====================================== INITIALIZATION ======================================
	
	public POSState(DEPTree tree, boolean decode)
	{
		super(tree, decode);
		initLowerSimplifiedWordForms(tree);
	}

	@Override
	protected void initGoldLabels()
	{
		g_labels = d_tree.getPOSTags();
 		d_tree.clearPOSTags();
	}

	private void initLowerSimplifiedWordForms(DEPTree tree)
	{
		s_lowerSimplifiedWordForms = new String[t_size];
		
		int i; for (i=1; i<t_size; i++)
			s_lowerSimplifiedWordForms[i] = StringUtils.toLowerCase(getNode(i).getSimplifiedForm());
	}
	
//	====================================== LABEL ======================================
	
	@Override
	public void setAutoLabel(String label)
	{
		getInput().setPOSTag(label);
	}
	
//	====================================== GETTER ======================================
	
	public String getLowerSimplifiedWordForm(DEPNode node)
	{
		return getLowerSimplifiedWordForm(node.getID());
	}
	
	public String getLowerSimplifiedWordForm(int nodeID)
	{
		return s_lowerSimplifiedWordForms[nodeID];
	}
}