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
import com.clearnlp.feature.FeatureToken;
import com.clearnlp.feature.dependency.DEPFieldType;
import com.clearnlp.feature.dependency.DEPRelationType;
import com.clearnlp.feature.dependency.DEPSourceType;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractState
{
	protected DEPTree d_tree;
	protected int     t_size;
	
	public AbstractState(DEPTree tree)
	{
		setTree(tree);
	}
	
	abstract public Object   getGoldLabel();
	abstract public Object[] getGoldLabels();
	
//	====================================== TREE ======================================
	
	public DEPTree getTree()
	{
		return d_tree;
	}
	
	public int getTreeSize()
	{
		return d_tree.size();
	}

	public void setTree(DEPTree tree)
	{
		d_tree = tree;
	}
	
//	====================================== NODE ======================================

	public DEPNode getNode(int id)
	{
		return d_tree.get(id);
	}
	
	/**
	 * @param leftBound the leftmost ID (exclusive).
	 * @param rightBound the rightmost ID (exclusive).
	 */
	protected DEPNode getNode(FeatureToken<DEPSourceType,DEPRelationType,DEPFieldType> token, int nodeID, int leftBound, int rightBound)
	{
		DEPNode node = getNodeAux(token, nodeID, leftBound, rightBound);
		if (node == null)	return null;
		
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
			
			case h2   : return node.getGrandHead();
			case lmd2 : return node.getLeftMostDependent(1);
			case rmd2 : return node.getRightMostDependent(1);
			case lnd2 : return node.getLeftNearestDependent(1);
			case rnd2 : return node.getRightNearestDependent(1);
			case lns2 : return node.getLeftNearestSibling(1);
			case rns2 : return node.getRightNearestSibling(1);
			}
		}
		
		return node;
	}
	
	/** Called by {@link #getNode(FeatureToken, int, int, int)} */
	private DEPNode getNodeAux(FeatureToken<DEPSourceType,DEPRelationType,DEPFieldType> token, int nodeID, int leftBound, int rightBound)
	{
		if (token.getOffset() == 0)
			return d_tree.get(nodeID);

		nodeID += token.getOffset();
		
		if (leftBound < nodeID && nodeID < rightBound)
			return getNode(nodeID);
		
		return null;
	}
}