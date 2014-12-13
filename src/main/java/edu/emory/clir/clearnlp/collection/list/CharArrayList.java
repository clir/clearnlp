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
public class CharArrayList extends com.carrotsearch.hppc.CharArrayList implements Serializable
{
	private static final long serialVersionUID = -5613054695850264301L;

	public CharArrayList()
	{
		super();
	}
	
	public CharArrayList(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		addAll((char[])in.readObject());
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toArray());
	}
	
	public void addAll(char[] array)
	{
		int i, size = array.length;
		
		for (i=0; i<size; i++) add(array[i]);
		trimToSize();
	}
	
	public char[] toArray()
	{
		return toArray(0, size());
	}
	
	/**
	 * @param beginIndex inclusive
	 * @param endIndex exclusive
	 */
	public char[] toArray(int beginIndex, int endIndex)
	{
		char[] array = new char[endIndex - beginIndex];
		int i;
		
		for (i=0; beginIndex < endIndex; beginIndex++,i++)
			array[i] = get(beginIndex);
		
		return array;
	}
	
	public CharArrayList clone()
	{
		int i, size = size();
		CharArrayList list = new CharArrayList(size);
		for (i=0; i<size; i++) list.add(get(i));
		return list;
	}
}