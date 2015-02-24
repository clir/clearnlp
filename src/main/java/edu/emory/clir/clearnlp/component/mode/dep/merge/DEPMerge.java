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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPMerge
{
	private Map<DEPNode,Map<DEPNode,MergeArc>> m_heads;
	private DEPTree d_tree;

	public DEPMerge(DEPTree tree)
	{
		m_heads = new HashMap<>(tree.size()-1);
		d_tree  = tree;
	}
	
	public void addEdge(DEPNode node, DEPNode head, String label, double score)
	{
//		int key = node.getID() * d_tree.size() + head.getID();
		
		Map<DEPNode,MergeArc> map = m_heads.computeIfAbsent(node, k-> new HashMap<>());
		MergeArc arc = map.get(head);
		
		if (arc == null)
			map.put(head, new MergeArc(node, head, label, score));
		else
			arc.addLabel(label, score);
	}
	
	public void merge()
	{
		Deque<MergeArc> priorArcs = new ArrayDeque<>();
		List<MergeArc> allArcs = getSortedHeadList();
		MergeArc priorArc;
		
		d_tree.clearDependencies();
		
		while (true)
		{
			if (allArcs.isEmpty()) return;
			priorArc = mergeHeads(allArcs, priorArcs);
			if (priorArc == null) return;
			configureArcs(allArcs, priorArcs, priorArc);
		}
	}
	
	private List<MergeArc> getSortedHeadList()
	{
		List<MergeArc> list = new ArrayList<>();
		
		for (Map<DEPNode,MergeArc> heads : m_heads.values())
			list.addAll(heads.values());
		
		Collections.sort(list, Collections.reverseOrder());
		return list;
	}
	
	private boolean isConnected(int count)
	{
		return count == d_tree.size();
	}
	
	private MergeArc mergeHeads(List<MergeArc> allArcs, Deque<MergeArc> priorArcs)
	{
		int max = attachHeads(allArcs, priorArcs);
		if (isConnected(max)) return null;
		
		PriorityQueue<MergeArc> headless = getHeadless();
		if (headless.isEmpty()) return null;
		MergeArc best = null;
		int count;
		
		while (!headless.isEmpty())	
		{
			priorArcs.addLast(headless.poll());
			reset(priorArcs);
			count = attachHeads(allArcs, priorArcs);
			
			if (max < count)
			{
				if (isConnected(count)) return null;
				max  = count;
				best = priorArcs.removeLast();
			}
			else
				priorArcs.removeLast();
		}
		
		return best;
	}
	
	private int attachHeads(List<MergeArc> allArcs, Deque<MergeArc> priorArcs)
	{
		int count = 1 + priorArcs.size();
		int i, size = allArcs.size();
		
		for (i=0; i<size; i++)
		{
			if (allArcs.get(i).setHead())
			{
				count++;
				if (isConnected(count)) return count;
			}
		}
		
//		System.out.println(edge+" "+list+" "+count+"\n"+d_tree.toStringDEP()+"\n");
		return count;
	}
	
	private void reset(Deque<MergeArc> priorArcs)
	{
		d_tree.clearDependencies();
		
		for (MergeArc arc : priorArcs)
			arc.setHead();
	}
	
	private PriorityQueue<MergeArc> getHeadless()
	{
		PriorityQueue<MergeArc> q = new PriorityQueue<>(Collections.reverseOrder());
		Map<DEPNode,MergeArc> heads;
		
		for (DEPNode node : d_tree)
		{
			if (!node.hasHead())
			{
				heads = m_heads.get(node);
				if (heads != null) q.addAll(heads.values());
			}
		}

		return q;
	}
	
	private void configureArcs(List<MergeArc> allArcs, Deque<MergeArc> priorArcs, MergeArc priorArc)
	{
		Iterator<MergeArc> iterator = allArcs.iterator();
		MergeArc arc;
		
		priorArcs.addLast(priorArc);
		reset(priorArcs);
		
		while (iterator.hasNext())
		{
			arc = iterator.next();
			
			if (arc.getNode() == priorArc.getNode() || arc.containsCycle())
			{
				iterator.remove();
				remove(arc);
			}
		}
	}
	
	private void remove(MergeArc arc)
	{
		Map<DEPNode,MergeArc> map = m_heads.get(arc.getNode());
		
		if (map != null)
		{
			map.remove(arc.getHead());
			if (map.isEmpty()) m_heads.remove(arc.getNode());
		}
	}
}
