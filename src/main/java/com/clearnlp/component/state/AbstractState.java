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
import com.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractState<GoldType,LabelType>
{
	protected GoldType[] g_labels;
	protected DEPTree    d_tree;
	protected int        t_size;

//	====================================== INITIALIZATION ======================================
	
	public AbstractState(DEPTree tree)
	{
		initTree(tree);
		init(tree);
	}

	/** Initializes this processing state. */
	abstract protected void init(DEPTree tree);
	
	/** Called by {@link #AbstractState(DEPTree)}. */
	private void initTree(DEPTree tree)
	{
		d_tree = tree;
		t_size = tree.size();
	}

//	====================================== LABEL ======================================

	/** @return the gold-standard label for the current state. */
	abstract public LabelType getGoldLabel();
	
	/** @return gold-standard labels for all tokens in {@link #d_tree}. */
	public GoldType[] getGoldLabels()
	{
		return g_labels;
	}
	
	/** Sets the gold-standard labels for all tokens. */
	public void setGoldLabels(GoldType[] labels)
	{
		g_labels = labels;
	}
	
//	====================================== TREE ======================================
	
	/** @return the dependency node specified by the feature token. */
	abstract public DEPNode getNode(AbstractFeatureToken<?> token);
	
	public DEPTree getTree()
	{
		return d_tree;
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
