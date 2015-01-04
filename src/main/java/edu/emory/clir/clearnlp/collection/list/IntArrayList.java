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

import com.carrotsearch.hppc.IntCollection;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntArrayList extends com.carrotsearch.hppc.IntArrayList implements Serializable
{
	private static final long serialVersionUID = -5613054695850264301L;

	public IntArrayList()
	{
		super();
	}
	
	public IntArrayList(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	public IntArrayList(IntCollection col)
	{
		super(col);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		addAll((int[])in.readObject());
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void addAll(int[] array)
	{
		int i, size = array.length;
		
		for (i=0; i<size; i++) add(array[i]);
		trimToSize();
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
}