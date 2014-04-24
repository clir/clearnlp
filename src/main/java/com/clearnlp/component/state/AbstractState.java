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
abstract public class AbstractState
{
	protected DEPTree d_tree;
	
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
}

//case a : break;
//case f1: break;
//case f2: break;
//case prefix: break;
//case suffix: break;
//case subcat: break;
//case path: break;
//case argn: break;

//@SuppressWarnings("incomplete-switch")
//private String getPathAux(DEPNode top, DEPNode bottom, DEPFieldType type, String delim, boolean includeTop)
//{
//	StringBuilder build = new StringBuilder();
//	DEPNode head = bottom;
//	int dist = 0;
//	
//	do
//	{
//		switch (type)
//		{
//		case p: build.append(delim); build.append(head.getPOSTag()); break;
//		case d: build.append(delim); build.append(head.getLabel());  break;
//		case t: dist++; break;
//		}
//		
//		head = head.getHead();
//	}
//	while (head != top && head != null);
//	
//	switch (type)
//	{
//	case p:
//		if (includeTop)
//		{
//			build.append(delim);
//			build.append(top.getPOSTag());	
//		}	break;
//	case t:
//		build.append(delim);
//		build.append(dist);
//		break;
//	}
//	
//	return build.length() == 0 ? null : build.toString();
//}