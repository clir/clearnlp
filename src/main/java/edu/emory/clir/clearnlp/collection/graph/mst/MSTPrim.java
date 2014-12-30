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
package edu.emory.clir.clearnlp.collection.graph.mst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import edu.emory.clir.clearnlp.collection.graph.Edge;
import edu.emory.clir.clearnlp.collection.graph.Graph;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MSTPrim
{
	public List<Edge> getMaximumSpanningTree(Graph graph)
	{
		PriorityQueue<Edge> queue = new PriorityQueue<>(Collections.reverseOrder());
		IntHashSet visited = new IntHashSet();
		List<Edge> tree = new ArrayList<>();
		Edge edge;
		
		add(queue, visited, graph, 0);
		
		while (!queue.isEmpty())
		{
			edge = queue.poll();
			
			if (!visited.contains(edge.getTarget()))
			{
				tree.add(edge);
				if (tree.size()+1 == graph.size()) break;
				add(queue, visited, graph, edge.getSource());
			}
		}
		
		return tree;
	}
	
	private void add(PriorityQueue<Edge> queue, IntHashSet visited, Graph graph, int source)
	{
		List<Edge> outgoingEdges = graph.getOutgoingEdges(source);
		int i, size = outgoingEdges.size();
		Edge edge;
		
		visited.add(source);
		
		for (i=0; i<size; i++)
		{
			edge = outgoingEdges.get(i);
			
			if (!visited.contains(edge.getTarget()))
				queue.add(edge);
		}
	}
}
