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
public class DefaultState
{
	protected DEPTree d_tree;
	protected int     t_size;
	
	public DefaultState(DEPTree tree)
	{
		setTree(tree);
	}
	
//	====================================== GETTERS ======================================
	
	public DEPTree getTree()
	{
		return d_tree;
	}
	
	public int getTreeSize()
	{
		return t_size;
	}

//	====================================== SETTERS ======================================

	public void setTree(DEPTree tree)
	{
		d_tree = tree;
		t_size = tree.size();		
	}
	
//	====================================== NODES ======================================

	public DEPNode getNode(int id)
	{
		return d_tree.get(id);
	}
	
//	protected DEPNode getNode(DEPFeatureToken token, int cIdx, int bIdx, int eIdx)
//	{
//		if (token.offset == 0)
//			return d_tree.get(cIdx);
//
//		cIdx += token.offset;
//		
//		if (bIdx < cIdx && cIdx < eIdx)
//			return getNode(cIdx);
//		
//		return null;
//	}
}