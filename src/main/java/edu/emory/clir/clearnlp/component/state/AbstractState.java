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

import edu.emory.clir.clearnlp.component.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractState<LabelType>
{
	protected LabelType[] g_labels;
	protected DEPTree     d_tree;
	protected int         t_size;

//	====================================== INITIALIZATION ======================================
	
	public AbstractState(DEPTree tree, CFlag flag)
	{
		d_tree = tree;
		t_size = tree.size();
		if (flag != CFlag.COLLECT && flag != CFlag.DECODE) initGoldLabels();
	}
	
	abstract protected void initGoldLabels();

//	====================================== LABEL ======================================

	public LabelType[] getGoldLabels()
	{
		return g_labels;
	}
	
	/** @return the gold-standard label for the current state. */
	abstract public LabelType getGoldLabel();
	
	/** @return the gold-standard label for the current state. */
	abstract public void setAutoLabel(LabelType label);
	
//	====================================== TREE ======================================
	
	/** @return the dependency node specified by the feature token. */
	abstract public DEPNode getNode(AbstractFeatureToken<?> token);
	
	public DEPTree getTree()
	{
		return d_tree;
	}
	
	public int getTreeSize()
	{
		return t_size;
	}

	public DEPNode getNode(int nodeID)
	{
		return d_tree.get(nodeID);
	}
	
	/**
	 * @param beginID  the leftmost  ID (inclusive).
	 * @param endID the rightmost ID (exclusive).
	 */
	protected DEPNode getNode(AbstractFeatureToken<?> token, int nodeID, int beginID, int endID)
	{
		nodeID += token.getOffset();
		
		if (beginID <= nodeID && nodeID < endID)
			return getNodeAux(token, nodeID);
		
		return null;
	}
	
	/** Called by {@link #getNode(AbstractFeatureToken, DEPTree, int, int, int)}. */
	private DEPNode getNodeAux(AbstractFeatureToken<?> token, int nodeID)
	{
		DEPNode node = getNode(nodeID);
		
		if (token.hasRelation())
		{
			switch (token.getRelation())
			{
			case h   : return node.getHead();
			case lmd : return node.getLeftMostDependent();
			case rmd : return node.getRightMostDependent();
			case lnd : return node.getLeftNearestDependent();
			case rnd : return node.getRightNearestDependent();
			case lns : return node.getLeftNearestSibling();
			case rns : return node.getRightNearestSibling();
			
			case h2  : return node.getGrandHead();
			case lmd2: return node.getLeftMostDependent(1);
			case rmd2: return node.getRightMostDependent(1);
			case lnd2: return node.getLeftNearestDependent(1);
			case rnd2: return node.getRightNearestDependent(1);
			case lns2: return node.getLeftNearestSibling(1);
			case rns2: return node.getRightNearestSibling(1);
			}
		}
		
		return node;
	}
	
//	====================================== BOOLEAN ======================================
	
	public boolean isFirstNode(DEPNode node)
	{
		return node.getID() == 1;
	}
	
	public boolean isLastNode(DEPNode node)
	{
		return node.getID() + 1 == t_size;
	}
}
