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
package com.clearnlp.collection.set;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.carrotsearch.hppc.CharLookupContainer;
import com.carrotsearch.hppc.CharOpenHashSet;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CharHashSet implements Serializable
{
	private static final long serialVersionUID = -3796053685010557911L;
	private CharOpenHashSet g_set;
	
	public CharHashSet()
	{
		g_set = new CharOpenHashSet();
	}
	
	public CharHashSet(char... characters)
	{
		g_set = new CharOpenHashSet();
		
		for (char c : characters)
			g_set.add(c);
	}
	
	public CharHashSet(int initialCapacity)
	{
		g_set = new CharOpenHashSet(initialCapacity);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		char[] array = (char[])in.readObject();
		g_set = new CharOpenHashSet(array.length);
		addAll(array);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void addAll(char[] array)
	{
		for (char item : array)
			add(item);
	}
	
	public char[] toArray()
	{
		return g_set.toArray();
	}
	
	public void add(char item)
	{
		g_set.add(item);
	}
	
	public boolean remove(char item)
	{
		return g_set.remove(item);
	}
	
	public int removeAll(CharHashSet set)
	{
		return g_set.removeAll(set.g_set);
	}
	
	public boolean contains(char item)
	{
		return g_set.contains(item);
	}
	
	public int size()
	{
		return g_set.size();
	}
	
	public CharLookupContainer getCharLookupContainer()
	{
		return g_set;
	}
	
	@Override
	public String toString()
	{
		return g_set.toString();
	}
}