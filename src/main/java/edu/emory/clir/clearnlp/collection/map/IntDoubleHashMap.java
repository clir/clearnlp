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
package edu.emory.clir.clearnlp.collection.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.carrotsearch.hppc.IntDoubleOpenHashMap;

import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntDoubleHashMap implements Serializable, Iterable<DoubleIntPair>
{
	private static final long serialVersionUID = 5327212904932776361L;
	private IntDoubleOpenHashMap g_map;
	
	public IntDoubleHashMap()
	{
		g_map = new IntDoubleOpenHashMap();
	}
	
	public IntDoubleHashMap(int initialCapacity)
	{
		g_map = new IntDoubleOpenHashMap(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<DoubleIntPair> list = (List<DoubleIntPair>)in.readObject();
		g_map = new IntDoubleOpenHashMap(list.size());
		putAll(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (value, key) pairs. */
	public List<DoubleIntPair> toList()
	{
		List<DoubleIntPair> list = new ArrayList<>();
		
		for (DoubleIntPair p : this)
			list.add(p);
		
		return list;
	}
	
	/** Puts a the list of (value, key) pairs to this map. */
	public void putAll(List<DoubleIntPair> list)
	{
		for (DoubleIntPair p : list)
			put(p.i, p.d);
	}
	
	public void put(int key, double value)
	{
		g_map.put(key, value);
	}
	
	public double get(int key)
	{
		return g_map.get(key);
	}
	
	public double remove(int key)
	{
		return g_map.remove(key);
	}
	
	public boolean containsKey(int key)
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
	public Iterator<DoubleIntPair> iterator()
	{
		Iterator<DoubleIntPair> it = new Iterator<DoubleIntPair>()
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
			public DoubleIntPair next()
			{
				if (current_index < key_size)
				{
					DoubleIntPair p = new DoubleIntPair(g_map.values[current_index], g_map.keys[current_index]);
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