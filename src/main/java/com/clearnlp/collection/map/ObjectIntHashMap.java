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

import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.clearnlp.util.pair.ObjectIntPair;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class ObjectIntHashMap<T> implements Serializable, Iterable<ObjectIntPair<T>>
{
	private static final long serialVersionUID = -869739556140492570L;
	private ObjectIntOpenHashMap<T> g_map;
	
	public ObjectIntHashMap()
	{
		g_map = new ObjectIntOpenHashMap<T>();
	}
	
	public ObjectIntHashMap(int initialCapacity)
	{
		g_map = new ObjectIntOpenHashMap<T>(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<ObjectIntPair<T>> list = (List<ObjectIntPair<T>>)in.readObject();
		g_map = new ObjectIntOpenHashMap<T>(list.size());
		put(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (key, value) pairs. */
	public List<ObjectIntPair<T>> toList()
	{
		List<ObjectIntPair<T>> list = Lists.newArrayList();
		
		for (ObjectIntPair<T> p : this)
			list.add(p);
		
		return list;
	}
	
	/** Puts a the list of (key, value) pairs to this map. */
	public void put(List<ObjectIntPair<T>> list)
	{
		for (ObjectIntPair<T> p : list)
			put((T)p.o, p.i);
	}
	
	public int add(T key)
	{
		int value = get(key) + 1;
		put(key, value);
		return value;
	}
	
	public void put(T key, int value)
	{
		g_map.put(key, value);
	}
	
	public int get(T key)
	{
		return g_map.get(key);
	}
	
	public int remove(T key)
	{
		return g_map.remove(key);
	}
	
	public boolean containsKey(T key)
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
					ObjectIntPair<T> p = new ObjectIntPair<T>(g_map.keys[current_index], g_map.values[current_index]);
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