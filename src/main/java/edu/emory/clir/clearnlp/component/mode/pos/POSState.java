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

import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.component.utils.AbstractLRState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSState extends AbstractLRState
{
	private Map<String,String> m_ambiguity_classes;
	private Set<String>        s_proper_nouns;
	
//	====================================== INITIALIZATION ======================================
	
	public POSState(DEPTree tree, CFlag flag, Map<String,String> ambiguityClasses, Set<String> properNouns)
	{
		super(tree, flag);
		init(ambiguityClasses, properNouns);
	}
	
	private void init(Map<String,String> ambiguityClasses, Set<String> properNouns)
	{
		m_ambiguity_classes = ambiguityClasses;
		s_proper_nouns      = properNouns;
	}
	
//	====================================== ORACLE/LABEL ======================================
	
	@Override
	protected String clearOracle(DEPNode node)
	{
		return node.clearPOSTag();
	}

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
	
	public String getAmbiguityClass(DEPNode node)
	{
		return m_ambiguity_classes.get(node.getSimplifiedWordForm());
	}
	
	public boolean extractWordFormFeature(DEPNode node)
	{
		return c_flag == CFlag.DECODE || c_flag == CFlag.EVALUATE || !s_proper_nouns.contains(node.getSimplifiedWordForm());
	}
}