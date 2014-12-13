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
public class FloatArrayList extends com.carrotsearch.hppc.FloatArrayList implements Serializable
{
	private static final long serialVersionUID = -5613054695850264301L;

	public FloatArrayList()
	{
		super();
	}
	
	public FloatArrayList(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		addAll((float[])in.readObject());
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void addAll(float[] array)
	{
		int i, size = array.length;
		
		for (i=0; i<size; i++) add(array[i]);
		trimToSize();
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
	
	public FloatArrayList clone()
	{
		int i, size = size();
		FloatArrayList list = new FloatArrayList(size);
		for (i=0; i<size; i++) list.add(get(i));
		return list;
	}
}