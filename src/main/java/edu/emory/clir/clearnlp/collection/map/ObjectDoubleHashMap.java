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
import java.util.Iterator;
import java.util.List;

import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;
import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ObjectDoubleHashMap<T> implements Serializable, Iterable<ObjectDoublePair<T>>
{
	private static final long serialVersionUID = -869739556140492570L;
	private ObjectDoubleOpenHashMap<T> g_map;
	
	public ObjectDoubleHashMap()
	{
		g_map = new ObjectDoubleOpenHashMap<T>();
	}
	
	public ObjectDoubleHashMap(int initialCapacity)
	{
		g_map = new ObjectDoubleOpenHashMap<T>(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<ObjectDoublePair<T>> list = (List<ObjectDoublePair<T>>)in.readObject();
		g_map = new ObjectDoubleOpenHashMap<T>(list.size());
		put(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (key, value) pairs. */
	public List<ObjectDoublePair<T>> toList()
	{
		List<ObjectDoublePair<T>> list = Lists.newArrayList();
		
		for (ObjectDoublePair<T> p : this)
			list.add(p);
		
		return list;
	}
	
	/** Puts a the list of (key, value) pairs to this map. */
	public void put(List<ObjectDoublePair<T>> list)
	{
		for (ObjectDoublePair<T> p : list)
			put((T)p.o, p.d);
	}
	
	public double add(T key, double inc)
	{
		double value = get(key) + inc;
		put(key, value);
		return value;
	}
	
	public void put(T key, double value)
	{
		g_map.put(key, value);
	}
	
	public double get(T key)
	{
		return g_map.get(key);
	}
	
	public ObjectDoublePair<T> getMaxEntry()
	{
		ObjectDoublePair<T> max = null;
		
		for (ObjectDoublePair<T> p : this)
		{
			if (max == null || p.compareTo(max) > 0)
				max = p;
		}
		
		return max;
	}
	
	public double remove(T key)
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
	public Iterator<ObjectDoublePair<T>> iterator()
	{
		Iterator<ObjectDoublePair<T>> it = new Iterator<ObjectDoublePair<T>>()
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
			public ObjectDoublePair<T> next()
			{
				if (current_index < key_size)
				{
					ObjectDoublePair<T> p = new ObjectDoublePair<T>(g_map.keys[current_index], g_map.values[current_index]);
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