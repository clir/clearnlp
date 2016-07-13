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

import com.carrotsearch.hppc.cursors.CharObjectCursor;

import edu.emory.clir.clearnlp.collection.pair.ObjectCharPair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CharObjectHashMap<T> implements Serializable, Iterable<ObjectCharPair<T>>
{
	private static final long serialVersionUID = -1072021691426162355L;
	private com.carrotsearch.hppc.CharObjectHashMap<T> g_map;
	
	public CharObjectHashMap()
	{
		g_map = new com.carrotsearch.hppc.CharObjectHashMap<T>();
	}
	
	public CharObjectHashMap(int initialCapacity)
	{
		g_map = new com.carrotsearch.hppc.CharObjectHashMap<T>(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<ObjectCharPair<T>> list = (List<ObjectCharPair<T>>)in.readObject();
		g_map = new com.carrotsearch.hppc.CharObjectHashMap<T>(list.size());
		putAll(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (value, key) pairs. */
	public List<ObjectCharPair<T>> toList()
	{
		List<ObjectCharPair<T>> list = new ArrayList<>();
		
		for (ObjectCharPair<T> p : this)
			list.add(p);
		
		return list;
	}
	
	/** Puts a the list of (value, key) pairs to this map. */
	public void putAll(List<ObjectCharPair<T>> list)
	{
		for (ObjectCharPair<T> p : list)
			put(p.c, (T)p.o);
	}
	
	public void put(char key, T value)
	{
		g_map.put(key, value);
	}
	
	public T get(char key)
	{
		return g_map.get(key);
	}
	
	public T remove(char key)
	{
		return g_map.remove(key);
	}
	
	public void clear()
	{
		for (ObjectCharPair<T> p : toList())
			remove(p.c);
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
	public Iterator<ObjectCharPair<T>> iterator()
	{
		Iterator<ObjectCharPair<T>> it = new Iterator<ObjectCharPair<T>>()
		{
			private final Iterator<CharObjectCursor<T>> g_iter =  g_map.iterator();
			
			@Override
			public boolean hasNext()
			{
				return g_iter.hasNext();
			}
			
			@Override
			public ObjectCharPair<T> next()
			{
				ObjectCharPair<T> p = null;
				try{
					CharObjectCursor<T> cursor = g_iter.next();
					p = new ObjectCharPair<T>(cursor.value, cursor.key);
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