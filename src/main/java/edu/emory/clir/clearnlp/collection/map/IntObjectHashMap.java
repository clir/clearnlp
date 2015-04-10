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

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.ObjectContainer;

import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntObjectHashMap<T> implements Serializable, Iterable<ObjectIntPair<T>>
{
	private static final long serialVersionUID = 5327212904932776361L;
	private IntObjectOpenHashMap<T> g_map;
	
	public IntObjectHashMap()
	{
		g_map = new IntObjectOpenHashMap<T>();
	}
	
	public IntObjectHashMap(int initialCapacity)
	{
		g_map = new IntObjectOpenHashMap<T>(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<ObjectIntPair<T>> list = (List<ObjectIntPair<T>>)in.readObject();
		g_map = new IntObjectOpenHashMap<T>(list.size());
		putAll(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (value, key) pairs. */
	public List<ObjectIntPair<T>> toList()
	{
		List<ObjectIntPair<T>> list = new ArrayList<>();
		
		for (ObjectIntPair<T> p : this)
			list.add(p);
		
		return list;
	}
	
	public ObjectContainer<T> values()
	{
		return g_map.values();
	}
	
	/** Puts a the list of (value, key) pairs to this map. */
	public void putAll(List<ObjectIntPair<T>> list)
	{
		for (ObjectIntPair<T> p : list)
			put(p.i, (T)p.o);
	}
	
	public void put(int key, T value)
	{
		g_map.put(key, value);
	}
	
	public T get(int key)
	{
		return g_map.get(key);
	}
	
	public T remove(int key)
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
	
	public int getMaxKey()
	{
		int max = -1;
		
		for (ObjectIntPair<T> p : this)
			max = Math.max(max, p.i);
		
		return max;
	}
	
	@Override
	public String toString()
	{
		return g_map.toString();
	}
	
	@Override
	public Iterator<ObjectIntPair<T>> iterator()
	{
		Iterator<ObjectIntPair<T>> it = new Iterator<ObjectIntPair<T>>()
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
			public ObjectIntPair<T> next()
			{
				if (current_index < key_size)
				{
					ObjectIntPair<T> p = new ObjectIntPair<T>(g_map.values[current_index], g_map.keys[current_index]);
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