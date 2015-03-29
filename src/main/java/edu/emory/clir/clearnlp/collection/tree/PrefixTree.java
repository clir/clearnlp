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
package edu.emory.clir.clearnlp.collection.tree;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PrefixTree<K extends Comparable<K>,V> implements Serializable
{
	private static final long serialVersionUID = 6471355272521434323L;
	private PrefixNode n_root;
	
	public PrefixTree()
	{
		n_root = new PrefixNode();
	}
	
	public void add(K[] keys, V value)
	{
		PrefixNode next, curr = n_root;
		int i, len = keys.length;
		
		for (i=0; i<len; i++)
		{
			next = curr.get(keys[i]);
			
			if (next == null)
			{
				next = new PrefixNode();
				curr.put(keys[i], next);
			}
			
			curr = next;
		}
		
		curr.setValue(value);
	}
	
	public V getValue(K[] keys, int beginIndex, boolean minimum)
	{
		PrefixNode curr = n_root;
		int i, len = keys.length;
		V value = null;
		
		for (i=beginIndex; i<len; i++)
		{
			curr = curr.get(keys[i]);
			if (curr == null) break;
			
			if (curr.hasValue())
			{
				value = curr.getValue();
				if (minimum) break;
			}
		}
		
		return value;
	}
	
	private class PrefixNode extends HashMap<K,PrefixNode>
	{
		private static final long serialVersionUID = 1566684742873455351L;
		private V value;
		
		public PrefixNode()
		{
			value = null;
		}
		
		public V getValue()
		{
			return value;
		}
		
		public void setValue(V value)
		{
			this.value = value;
		}
		
		public boolean hasValue()
		{
			return value != null;
		}
	}
}
