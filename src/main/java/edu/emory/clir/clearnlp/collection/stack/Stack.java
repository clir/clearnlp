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
import java.util.ArrayList;

import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Stack<T> extends ArrayList<T> implements Serializable
{
	private static final long serialVersionUID = -8603527717926741739L;
	
	public Stack()
	{
		super();
	}
	
	public Stack(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	public Stack(Stack<T> stack)
	{
		super(stack);
	}
	
	public void push(T element)
	{
		add(element);
	}
	
	public T pop()
	{
		int n = size() - 1;
		return DSUtils.isRange(this, n) ? remove(n) : null;
	}
	
	public T peek()
	{
		return peek(0);
	}
	
	public T peek(int n)
	{
		n = size() - 1 - n;
		return DSUtils.isRange(this, n) ? get(n) : null;
	}
}