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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.list.Stack;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StackTest
{
	@Test
	public void test()
	{
		Stack<String> stack = new Stack<String>("1");
		stack.push("2");
		stack.push("3");
		
		assertEquals("3", stack.peek());
		assertEquals("2", stack.peek(1));
		
		assertEquals("3", stack.pop());
		Stack<String> clone = stack.clone();
		
		assertEquals("2", stack.pop());
		assertEquals("1", stack.pop());
		
		assertEquals("2", clone.pop());
		assertEquals("1", clone.pop());
	}
}