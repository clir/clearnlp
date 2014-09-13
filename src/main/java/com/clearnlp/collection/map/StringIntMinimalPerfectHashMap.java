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
package com.clearnlp.collection.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntStack;
import com.clearnlp.collection.pair.ObjectIntPair;
import com.clearnlp.util.HashUtils;
import com.clearnlp.util.MathUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringIntMinimalPerfectHashMap
{
	private ObjectIntHashMap<String> m_key;
	private int   n_index;
	private int[] g_hashes;
	private int[] g_values;
	
	public StringIntMinimalPerfectHashMap()
	{
		m_key = new ObjectIntHashMap<>();
		n_index = 0;
	}
	
	public void addkey(String key)
	{
		if (!m_key.containsKey(key))
			m_key.put(key, n_index++);
	}
	
	public void initHashFunction()
	{
		int vsize = (int)MathUtils.nextPrimeNumber((int)(1.25 * m_key.size()));
		int hsize = vsize / 5;
		
		StringList[] patterns = getEmptyList(hsize);
		int[] hashes = new int[hsize];
		int[] values = new int[vsize];
		int i, b, d, len, item, slot;
		IntArrayList slots;
		StringList pattern;
		IntStack freelist;
		
		// initialize
		Arrays.fill(values, -1);
		
		for (ObjectIntPair<String> p : m_key)
			patterns[hash(p.o, 0, hsize)].add(p.o);
		
		// sort patterns in descending order
		Arrays.sort(patterns, Collections.reverseOrder());
		
		// |pattern| > 1
		for (b=0; b<hsize; b++)
		{
			pattern = patterns[b];
			len = pattern.size();
			if (len <= 1) break;
			
			slots = new IntArrayList();
			item  = 0;
			d = 1;
			
			// rotate patterns and search for suitable displacement
			while (item < len)
			{
				slot = hash(pattern.get(item), d, vsize);
				
				if (values[slot] != -1 || slots.contains(slot))
				{
					slots = new IntArrayList();
					item  = 0;
					d++;
				}
				else
				{
					slots.add(slot);
					item++;
				}
			}
			
			hashes[hash(pattern.get(0), 0, hsize)] = d;
			
			for (i=0; i<len; i++)
				values[slots.get(i)] = m_key.get(pattern.get(i));
		}
		
		// process patterns with one key and use a negative value of d
		freelist = new IntStack();
		
		for (i=0; i<vsize; i++)
		{
			if (values[i] == -1)
				freelist.add(i);
		}
		
		// |pattern| == 1
		for (; b<hsize; b++)
		{
			pattern = patterns[b];
			len = pattern.size();
			if (len == 0) break;
			
			slot = freelist.pop();
			hashes[hash(pattern.get(0), 0, hsize)] = -slot-1;
			values[slot] = m_key.get(pattern.get(0));
		}
		
		g_hashes = hashes;
		g_values = values;
	}
	
	public int lookup(String key)
	{
		int d = g_hashes[hash(key, 0, g_hashes.length)];
		d = (d < 0) ? -d-1 : hash(key, d, g_values.length);
		return g_values[d];
	}
	
	/** Called by {@link #initHashFunction()}. */
	private int hash(String key, int basis, int size)
	{
		int h = (basis == 0) ? HashUtils.fnv1aHash32(key) : HashUtils.fnv1aHash32(key, basis);
		return MathUtils.divisor(h, size);
	}
	
	/** Called by {@link #initHashFunction()}. */
	private class StringList extends ArrayList<String> implements Comparable<StringList>
	{
		private static final long serialVersionUID = -6992653145004684254L;

		@Override
		public int compareTo(StringList list)
		{
			return size() - list.size();
		}
	}
	
	/** Called by {@link #initHashFunction()}. */
	private StringList[] getEmptyList(int size)
	{
		StringList[] list = new StringList[size];
		int i;
		
		for (i=0; i<size; i++)
			list[i] = new StringList();
		
		return list;
	}
}
