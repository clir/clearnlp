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
package edu.emory.clir.clearnlp.collection.pair;

import java.io.Serializable;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Pair<T1, T2> implements Serializable
{
	private static final long serialVersionUID = 8447270640444415417L;
	
	public T1 o1;
	public T2 o2;
	
	public Pair()
	{
		set(null, null);
	}
	
	public Pair(T1 o1, T2 o2)
	{
		set(o1, o2);
	}
	
	public void set(T1 o1, T2 o2)
	{
		this.o1 = o1;
		this.o2 = o2;
	}
	
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append("(");
		build.append(o1.toString());
		build.append(",");
		build.append(o2.toString());
		build.append(")");
	
		return build.toString();
	}
}