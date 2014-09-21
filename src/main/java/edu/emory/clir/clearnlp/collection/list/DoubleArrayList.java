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
package edu.emory.clir.clearnlp.collection.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DoubleArrayList implements Serializable
{
	private static final long serialVersionUID = -5613054695850264301L;
	private com.carrotsearch.hppc.DoubleArrayList g_list;

	public DoubleArrayList()
	{
		g_list = new com.carrotsearch.hppc.DoubleArrayList();
	}
	
	public DoubleArrayList(int initialCapacity)
	{
		g_list = new com.carrotsearch.hppc.DoubleArrayList(initialCapacity);
	}
	
	public DoubleArrayList(double[] array)
	{
		init(array);
	}
	
	private void init(double[] array)
	{
		g_list = new com.carrotsearch.hppc.DoubleArrayList(array.length);
		addAll(array);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		init((double[])in.readObject());
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void add(double item)
	{
		g_list.add(item);
	}
	
	public void addAll(double[] array)
	{
		int i, size = array.length;
		
		for (i=0; i<size; i++) add(array[i]);
		trimToSize();
	}
	
	public void insert(int index, double item)
	{
		g_list.insert(index, item);
	}
	
	public double get(int index)
	{
		return g_list.get(index);
	}
	
	public double set(int index, double item)
	{
		return g_list.set(index, item);
	}
	
	public double remove(int index)
	{
		return g_list.remove(index);
	}
	
	public double[] toArray()
	{
		return toArray(0, size());
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	public double[] toArray(int beginIndex, int endIndex)
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
	
	public DoubleArrayList clone()
	{
		int i, size = size();
		DoubleArrayList list = new DoubleArrayList(size);
		for (i=0; i<size; i++) list.add(get(i));
		return list;
	}
	
	@Override
	public String toString()
	{
		return g_list.toString();
	}
}