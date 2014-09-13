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

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Stack<T>
{
	private List<T> t_list;
	private int i_lastIndex;
	
	public Stack()
	{
		t_list = Lists.newArrayList();
		i_lastIndex = -1;
	}
	
	public Stack(@SuppressWarnings("unchecked")T... elements)
	{
		t_list = Lists.newArrayList();
		
		for (T element : elements)
			t_list.add(element);
		
		i_lastIndex = elements.length - 1;
	}
	
	public void push(T element)
	{
		t_list.add(element);
		i_lastIndex++;
	}
	
	public T pop()
	{
		return t_list.remove(i_lastIndex--);
	}
	
	public T peek()
	{
		return t_list.get(i_lastIndex);
	}
	
	public T peek(int n)
	{
		n = i_lastIndex - n;
		return (0 <= n && n < size()) ? t_list.get(n) : null;
	}
	
	public int size()
	{
		return t_list.size();
	}
	
	public Stack<T> clone()
	{
		Stack<T> stack = new Stack<T>();
		
		stack.t_list = Lists.newArrayList(t_list);
		stack.i_lastIndex = i_lastIndex;
		
		return stack;
	}
}