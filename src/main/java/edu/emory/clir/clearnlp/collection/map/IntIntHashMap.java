/**
 * Copyright 2014, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *	 http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.NoSuchElementException;

import com.carrotsearch.hppc.cursors.IntIntCursor;

import edu.emory.clir.clearnlp.collection.pair.IntIntPair;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntIntHashMap implements Serializable, Iterable<IntIntPair>
{
	private static final long serialVersionUID = 5327212904932776361L;
	private com.carrotsearch.hppc.IntIntHashMap g_map;
	
	public IntIntHashMap()
	{
		g_map = new com.carrotsearch.hppc.IntIntHashMap();
	}
	
	public IntIntHashMap(int initialCapacity)
	{
		g_map = new com.carrotsearch.hppc.IntIntHashMap(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<IntIntPair> list = (List<IntIntPair>)in.readObject();
		g_map = new com.carrotsearch.hppc.IntIntHashMap(list.size());
		putAll(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (value, key) pairs. */
	public List<IntIntPair> toList()
	{
		List<IntIntPair> list = new ArrayList<>();
		
		for (IntIntPair p : this)
			list.add(p);
		
		return list;
	}
	
	/** Puts a the list of (value, key) pairs to this map. */
	public void putAll(List<IntIntPair> list)
	{
		for (IntIntPair p : list)
			put(p.i1, p.i2);
	}
	
	public void put(int key, int value)
	{
		g_map.put(key, value);
	}
	
	public int add(int key)
	{
		return add(key, 1);
	}
	
	public int add(int key, int inc)
	{
		int value = get(key) + inc;
		put(key, value);
		return value;
	}
	
	public int get(int key)
	{
		return g_map.get(key);
	}
	
	public int remove(int key)
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
	public Iterator<IntIntPair> iterator()
	{
		Iterator<IntIntPair> it = new Iterator<IntIntPair>()
		{
			private final Iterator<IntIntCursor> g_iter =  g_map.iterator();
			
			@Override
			public boolean hasNext()
			{
				return g_iter.hasNext();
			}
			
			@Override
			public IntIntPair next()
			{
				IntIntPair p = null;
				try{
					IntIntCursor cursor = g_iter.next();
					p = new IntIntPair(cursor.value, cursor.key);
				}
				catch (NoSuchElementException e)
				{
					
				}
				return p;
			}
			
			@Override
			public void remove() {}
		};
				
		return it;
	}
}