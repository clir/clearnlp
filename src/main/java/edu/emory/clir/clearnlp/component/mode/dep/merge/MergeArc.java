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

import edu.emory.clir.clearnlp.collection.map.ObjectDoubleHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MergeArc implements Comparable<MergeArc>
{
	private ObjectDoubleHashMap<String> m_labels;
	private ObjectDoublePair<String> best_label;
	private DEPNode d_node;
	private DEPNode d_head;
	private double  d_score;
	
	public MergeArc(DEPNode node, DEPNode head, String label, double score)
	{
		d_node  = node;
		d_head  = head;
		d_score = score;
		
		best_label = new ObjectDoublePair<String>(label, score);
		m_labels = new ObjectDoubleHashMap<>();
		m_labels.put(label, score);
	}
	
	public void addLabel(String label, double score)
	{
		double d = m_labels.add(label, score);
		if (d > best_label.d || best_label.o == null) best_label.set(label, d);
	}
	
	public DEPNode getNode()
	{
		return d_node;
	}

	public DEPNode getHead()
	{
		return d_head;
	}
	
	public String getBestLabel()
	{
		return best_label.o;
	}

	public double getScore()
	{
		return d_score;
	}
	
	public boolean setHead()
	{
		if (!d_node.hasHead() && !containsCycle())
		{
			d_node.setHead(d_head, getBestLabel());
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
