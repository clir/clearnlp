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
package edu.emory.clir.clearnlp.component.mode.sense;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.emory.clir.clearnlp.dependency.DEPNode;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RoleLexicon implements Serializable
{
	private static final long serialVersionUID = -1309316221225281613L;
	private Map<String,Set<String>> m_lemmas;
	
	public RoleLexicon()
	{	
		m_lemmas = new HashMap<>();
	}
	
	public void collect(RoleState state)
	{
		int i, size = state.getTreeSize();
		Set<String> set;
		DEPNode node;
		
		for (i=1; i<size; i++)
		{
			node = state.getNode(i);
//			m_lemmas.computeIfAbsent(node.getLemma(), k -> new HashSet<>()).add(state.getSense(node));
			
			
			set = m_lemmas.get(node.getLemma());
			
			if (set == null)
			{
				set = new HashSet<>();
				m_lemmas.put(node.getLemma(), set);
			}
			
//			set.add(state.getSense(node));
		}
	}
	
	public Set<String> finalizeLemmas()
	{
		Set<String> lemmas = new HashSet<>();
		
		for (Entry<String,Set<String>> entry : m_lemmas.entrySet())
		{
			if (entry.getValue().size() == 1)
				lemmas.add(entry.getKey());
		}
		
		return lemmas;
	}
	
	public boolean isVerbPredicate(DEPNode node)
	{
		return true;
	}
}


