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

import edu.emory.clir.clearnlp.collection.list.IntArrayList;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntStack extends IntArrayList implements Serializable
{
	private static final long serialVersionUID = -8603527717926741739L;
	private int i_lastIndex;
	
	public IntStack()
	{
		i_lastIndex = -1;
	}
	
	public IntStack(int initialCapacity)
	{
		super(initialCapacity);
		i_lastIndex = -1;
	}
	
	public IntStack(IntStack stack)
	{
		super(stack.size());
		addAll(stack);
		i_lastIndex = stack.i_lastIndex;
	}
	
	public void push(int element)
	{
		add(element);
		i_lastIndex++;
	}
	
	public int pop()
	{
		return remove(i_lastIndex--);
	}
	
	public int peek()
	{
		return get(i_lastIndex);
	}
	
	public int peek(int n)
	{
		return get(i_lastIndex - n);
	}
}