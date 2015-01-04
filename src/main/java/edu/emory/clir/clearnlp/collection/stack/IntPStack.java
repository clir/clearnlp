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
package edu.emory.clir.clearnlp.collection.stack;

import java.io.Serializable;

import com.carrotsearch.hppc.IntCollection;

import edu.emory.clir.clearnlp.collection.list.IntArrayList;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntPStack extends IntArrayList implements Serializable
{
	private static final long serialVersionUID = -8603527717926741739L;
	
	public IntPStack()
	{
		super();
	}
	
	public IntPStack(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	public IntPStack(IntCollection stack)
	{
		super(stack);
	}
	
	public void push(int element)
	{
		add(element);
	}
	
	/** @return -1 if the stack is empty. */
	public int pop()
	{
		int n = size() - 1;
		return (n < 0) ? -1 : remove(n);
	}
	
	/** @return -1 if the stack is empty. */
	public int peek()
	{
		return peek(0);
	}
	
	/** @return -1 if the index is out of range. */
	public int peek(int n)
	{
		n = size() - 1 - n;
		return (n < 0) ? -1 : get(n);
	}
}