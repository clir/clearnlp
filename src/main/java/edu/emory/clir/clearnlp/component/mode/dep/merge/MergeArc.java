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
package edu.emory.clir.clearnlp.component.mode.dep.merge;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MergeArc implements Comparable<MergeArc>
{
	private DEPNode d_node;
	private DEPNode d_head;
	private String  s_label;
	private double  d_score;
	
	public MergeArc(DEPNode node, DEPNode head, String label, double score)
	{
		d_node  = node;
		d_head  = head;
		s_label = label;
		d_score = score;
	}
	
	public DEPNode getNode()
	{
		return d_node;
	}

	public DEPNode getHead()
	{
		return d_head;
	}
	
	public String getLabel()
	{
		return s_label;
	}

	public double getScore()
	{
		return d_score;
	}
	
	public boolean setHead()
	{
		if (!d_node.hasHead() && !containsCycle())
		{
			d_node.setHead(d_head, s_label);
			return true;
		}
		
		return false;
	}
	
	public boolean containsCycle()
	{
		return d_head.isDescendantOf(d_node);
	}
	
	@Override
	public String toString()
	{
		return d_node.getID()+"<-"+d_head.getID();
	}
	
	@Override
	public int compareTo(MergeArc o)
	{
		return MathUtils.signum(d_score - o.d_score);
	}
}
