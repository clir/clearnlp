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
package com.clearnlp.collection.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.carrotsearch.hppc.CharCharOpenHashMap;
import com.clearnlp.collection.pair.CharCharPair;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CharCharHashMap implements Serializable, Iterable<CharCharPair>
{
	private static final long serialVersionUID = -1072021691426162355L;
	static public char DEFAULT_VALUE = '\u0000';
	private CharCharOpenHashMap g_map;
	
	public CharCharHashMap()
	{
		g_map = new CharCharOpenHashMap();
	}
	
	public CharCharHashMap(int initialCapacity)
	{
		g_map = new CharCharOpenHashMap(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<CharCharPair> list = (List<CharCharPair>)in.readObject();
		g_map = new CharCharOpenHashMap(list.size());
		putAll(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (value, key) pairs. */
	public List<CharCharPair> toList()
	{
		List<CharCharPair> list = Lists.newArrayList();
		
		for (CharCharPair p : this)
			list.add(p);
		
		return list;
	}
	
	/** Puts a the list of (value, key) pairs to this map. */
	public void putAll(List<CharCharPair> list)
	{
		for (CharCharPair p : list)
			put(p.c1, p.c2);
	}
	
	public void put(char key, char value)
	{
		g_map.put(key, value);
	}
	
	public char get(char key)
	{
		return g_map.get(key);
	}
	
	public char remove(char key)
	{
		return g_map.remove(key);
	}
	
	public boolean containsKey(char key)
	{
		return g_map.containsKey(key);
	}
	
	public boolean isEmpty()
	{
		return g_map.isEmpty();
	}
	
	public int size()
	{
		return g_map.size();
	}
	
	@Override
	public String toString()
	{
		return g_map.toString();
	}
	
	@Override
	public Iterator<CharCharPair> iterator()
	{
		Iterator<CharCharPair> it = new Iterator<CharCharPair>()
		{
			private final int key_size = g_map.keys.length;
			private int current_index  = 0;
			
			@Override
			public boolean hasNext()
			{
				for (; current_index < key_size; current_index++)
				{
					if (g_map.allocated[current_index])
						return true;
				}
				
				return false;
			}
			
			@Override
			public CharCharPair next()
			{
				if (current_index < key_size)
				{
					CharCharPair p = new CharCharPair(g_map.keys[current_index], g_map.values[current_index]);
					current_index++;
					return p;
				}
				
				return null;
			}
			
			@Override
			public void remove() {}
		};
				
		return it;
	}
}