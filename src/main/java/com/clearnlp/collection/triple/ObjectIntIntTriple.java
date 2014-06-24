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
package com.clearnlp.collection.triple;

import java.io.Serializable;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class ObjectIntIntTriple<T> implements Serializable
{
	private static final long serialVersionUID = -7014586350906455183L;

	public T   o;
	public int i1;
	public int i2;
	
	public ObjectIntIntTriple(T o, int i1, int i2)
	{
		set(o, i1, i2);
	}
	
	public void set(T o, int i1, int i2)
	{
		this.o  = o;
		this.i1 = i1;
		this.i2 = i2;
	}
}