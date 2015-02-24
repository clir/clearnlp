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
package edu.emory.clir.clearnlp.collection.set;

import java.util.Arrays;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DisjointSet
{
	private int[] s_root;
	
	public DisjointSet(int size)
	{
		s_root = new int[size];
		Arrays.fill(s_root, -1);
	}
	
	public int union(int id1, int id2)
	{
		int r1 = find(id1);
		int r2 = find(id2);
		if (r1 == r2) return r1;
		
		if (s_root[r1] < s_root[r2])
		{
			s_root[r1] += s_root[r2];
			s_root[r2] = r1;
			return r1;
		}
		else
		{
			s_root[r2] += s_root[r1];
			s_root[r1] = r2;
			return r2;
		}
	}
	
	public int find(int id)
	{
		return (s_root[id] < 0) ? id : (s_root[id] = find(s_root[id]));
	}
	
	public boolean inSameSet(int id1, int id2)
	{
		return find(id1) == find(id2);
	}
	
	public String toString()
	{
		return Arrays.toString(s_root);
	}
}
