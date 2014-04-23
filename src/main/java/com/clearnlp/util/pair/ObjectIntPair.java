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
package com.clearnlp.util.pair;

import java.io.Serializable;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class ObjectIntPair<T> implements Serializable, Comparable<ObjectIntPair<T>>
{
	private static final long serialVersionUID = -5228607179375724504L;
	
	public T   o;
	public int i;
	
	public ObjectIntPair(T o, int i)
	{
		set(o, i);
	}
	
	public void set(T o, int i)
	{
		this.o = o;
		this.i = i;
	}

	@Override
	public int compareTo(ObjectIntPair<T> p)
	{
		return i - p.i;
	}
}