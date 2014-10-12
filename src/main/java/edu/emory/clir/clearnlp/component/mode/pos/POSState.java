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
package edu.emory.clir.clearnlp.component.mode.pos;

import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.component.CFlag;
import edu.emory.clir.clearnlp.component.state.SeqState;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.StringUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSState extends SeqState
{
	private Set<String> s_lowerSimplifiedWordForms;
	private Map<String,String> m_ambiguityClasses;
	private String[] a_lowerSimplifiedWordForms;
	
//	====================================== INITIALIZATION ======================================
	
	public POSState(DEPTree tree, CFlag flag, Set<String> lowerSimplifiedWordForms, Map<String,String> ambiguityClasses)
	{
		super(tree, flag);
		s_lowerSimplifiedWordForms = lowerSimplifiedWordForms;
		m_ambiguityClasses = ambiguityClasses;
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
		a_lowerSimplifiedWordForms = new String[t_size];
		
		int i; for (i=1; i<t_size; i++)
			a_lowerSimplifiedWordForms[i] = StringUtils.toLowerCase(getNode(i).getSimplifiedForm());
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
		return a_lowerSimplifiedWordForms[node.getID()];
	}
	
	public String getLowerSimplifiedWordForm(int nodeID)
	{
		return a_lowerSimplifiedWordForms[nodeID];
	}
	
	public String getAmbiguityClass(DEPNode node)
	{
		return m_ambiguityClasses.get(node.getSimplifiedForm());
	}
	
	public boolean includeForm(int nodeID)
	{
		return s_lowerSimplifiedWordForms.contains(a_lowerSimplifiedWordForms[nodeID]); 
	}
	
	public boolean includeForm(DEPNode node)
	{
		return includeForm(node.getID());
	}
}