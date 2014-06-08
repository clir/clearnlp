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

import com.carrotsearch.hppc.IntLookupContainer;
import com.carrotsearch.hppc.IntOpenHashSet;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class IntHashSet implements Serializable
{
	private static final long serialVersionUID = 8220093021280571821L;
	private IntOpenHashSet g_set;
	
	public IntHashSet()
	{
		g_set = new IntOpenHashSet();
	}
	
	public IntHashSet(int initialCapacity)
	{
		g_set = new IntOpenHashSet(initialCapacity);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		int[] array = (int[])in.readObject();
		g_set = new IntOpenHashSet(array.length);
		addAll(array);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void addAll(int[] array)
	{
		for (int item : array)
			add(item);
	}
	
	public int[] toArray()
	{
		return g_set.toArray();
	}
	
	public void add(int item)
	{
		g_set.add(item);
	}
	
	public boolean remove(int item)
	{
		return g_set.remove(item);
	}
	
	public int removeAll(IntHashSet set)
	{
		return g_set.removeAll(set.g_set);
	}
	
	public boolean contains(int item)
	{
		return g_set.contains(item);
	}
	
	public int size()
	{
		return g_set.size();
	}
	
	public IntLookupContainer getIntLookupContainer()
	{
		return g_set;
	}
	
	public IntHashSet clone()
	{
		IntHashSet set = new IntHashSet();
		set.g_set = g_set.clone();
		return set;
	}
	
	@Override
	public String toString()
	{
		return g_set.toString();
	}
}