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
package com.clearnlp.collection.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.clearnlp.collection.set.IntHashSet;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntArrayList implements Serializable
{
	private static final long serialVersionUID = -5613054695850264301L;
	private com.carrotsearch.hppc.IntArrayList g_list;

	public IntArrayList()
	{
		g_list = new com.carrotsearch.hppc.IntArrayList();
	}
	
	public IntArrayList(int initialCapacity)
	{
		g_list = new com.carrotsearch.hppc.IntArrayList(initialCapacity);
	}
	
	public void init(int[] array)
	{
		g_list = new com.carrotsearch.hppc.IntArrayList(array.length);
		addAll(array);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		init((int[])in.readObject());
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void add(int item)
	{
		g_list.add(item);
	}
	
	public void addAll(int[] array)
	{
		int i, size = array.length;
		
		for (i=0; i<size; i++) add(array[i]);
		trimToSize();
	}
	
	public void insert(int index, int item)
	{
		g_list.insert(index, item);
	}
	
	public int get(int index)
	{
		return g_list.get(index);
	}
	
	public int set(int index, int item)
	{
		return g_list.set(index, item);
	}
	
	public int remove(int index)
	{
		return g_list.remove(index);
	}
	
	public void removeAll(IntHashSet set)
	{
		g_list.removeAll(set.getIntLookupContainer());
	}
	
	public int[] toArray()
	{
		return toArray(0, size());
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	public int[] toArray(int beginIndex, int endIndex)
	{
		int[] array = new int[endIndex - beginIndex];
		int i;
		
		for (i=0; beginIndex < endIndex; beginIndex++,i++)
			array[i] = get(beginIndex);
		
		return array;
	}
	
	public void trimToSize()
	{
		g_list.trimToSize();
	}
	
	public boolean isEmpty()
	{
		return g_list.isEmpty();
	}
	
	public int size()
	{
		return g_list.size();
	}
	
	public int max()
	{
		if (isEmpty())
			throw new IllegalStateException("The list is empty.");
		
		int i, max = get(0), size = size();
		
		for (i=1; i<size; i++)
			max = Math.max(get(i), max);
		
		return max;
	}
	
	public int min()
	{
		if (isEmpty())
			throw new IllegalStateException("The list is empty.");
		
		int i, min = get(0), size = size();
		
		for (i=1; i<size; i++)
			min = Math.min(get(i), min);
		
		return min;
	}
	
	public IntArrayList clone()
	{
		int i, size = size();
		IntArrayList list = new IntArrayList(size);
		for (i=0; i<size; i++) list.add(get(i));
		return list;
	}
	
	@Override
	public String toString()
	{
		return g_list.toString();
	}
}