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

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class FloatArrayList implements Serializable
{
	private static final long serialVersionUID = -5613054695850264301L;
	private com.carrotsearch.hppc.FloatArrayList g_list;

	public FloatArrayList()
	{
		g_list = new com.carrotsearch.hppc.FloatArrayList();
	}
	
	public FloatArrayList(int initialCapacity)
	{
		g_list = new com.carrotsearch.hppc.FloatArrayList(initialCapacity);
	}
	
	public FloatArrayList(float[] array)
	{
		init(array);
	}
	
	private void init(float[] array)
	{
		g_list = new com.carrotsearch.hppc.FloatArrayList(array.length);
		addAll(array);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		init((float[])in.readObject());
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void add(float item)
	{
		g_list.add(item);
	}
	
	public void addAll(float[] array)
	{
		int i, size = array.length;
		
		for (i=0; i<size; i++) add(array[i]);
		trimToSize();
	}
	
	public void insert(int index, float item)
	{
		g_list.insert(index, item);
	}
	
	public float get(int index)
	{
		return g_list.get(index);
	}
	
	public float set(int index, float item)
	{
		return g_list.set(index, item);
	}
	
	public float remove(int index)
	{
		return g_list.remove(index);
	}
	
	public float[] toArray()
	{
		return toArray(0, size());
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	public float[] toArray(int beginIndex, int endIndex)
	{
		float[] array = new float[endIndex - beginIndex];
		int i;
		
		for (i=0; beginIndex < endIndex; beginIndex++,i++)
			array[i] = get(beginIndex);
		
		return array;
	}
	
	public double[] toDoubleArray(int beginIndex, int endIndex)
	{
		double[] array = new double[endIndex - beginIndex];
		int i;
		
		for (i=0; beginIndex < endIndex; beginIndex++,i++)
			array[i] = get(beginIndex);
		
		return array;
	}
	
	public void trimToSize()
	{
		g_list.trimToSize();
	}
	
	public int size()
	{
		return g_list.size();
	}
	
	public boolean isEmpty()
	{
		return g_list.isEmpty();
	}
	
	public FloatArrayList clone()
	{
		int i, size = size();
		FloatArrayList list = new FloatArrayList(size);
		for (i=0; i<size; i++) list.add(get(i));
		return list;
	}
	
	@Override
	public String toString()
	{
		return g_list.toString();
	}
}