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
package edu.emory.clir.clearnlp.collection.ngram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.MathUtils;

public class Unigram<T> implements Serializable
{
	private static final long serialVersionUID = 2431106431004828434L;
	private ObjectIntHashMap<T> g_map;
	private int i_total;
	private T   t_best;

	public Unigram()
	{
		g_map   = new ObjectIntHashMap<>();
		t_best  = null;
		i_total = 0;
	}
	
	public void add(T key)
	{
		add(key, 1);
	}
	
	public void add(T key, int inc)
	{
		int c = g_map.add(key, inc);
		i_total += inc;
		
		if (t_best == null || get(t_best) < c)
			t_best = key;
	}
	
	public int get(T key)
	{
		return g_map.get(key);
	}
	
	public ObjectDoublePair<T> getBest()
	{
		 return (t_best != null) ? new ObjectDoublePair<T>(t_best, MathUtils.divide(get(t_best), i_total)) : null;
	}
	
	public boolean contains(T key)
	{
		return g_map.containsKey(key);
	}
	
	public double getProbability(T key)
	{
		return MathUtils.divide(get(key), i_total);
	}
	
	public List<ObjectIntPair<T>> toList(int cutoff)
	{
		List<ObjectIntPair<T>> list = new ArrayList<>();
		
		for (ObjectIntPair<T> p : g_map)
		{
			if (p.i > cutoff)
				list.add(p);
		}
		
		return list;
	}
	
	public List<ObjectDoublePair<T>> toList(double threshold)
	{
		List<ObjectDoublePair<T>> list = new ArrayList<>();
		double d;
		
		for (ObjectIntPair<T> p : g_map)
		{
			d = MathUtils.divide(p.i, i_total);
			if (d > threshold) list.add(new ObjectDoublePair<T>(p.o, d));
		}
		
		return list;
	}
	
	public Set<T> keySet()
	{
		return g_map.keySet(0);
	}
	
	/** @return a set of keys whose values are greater than the specific cutoff. */
	public Set<T> keySet(int cutoff)
	{
		return g_map.keySet(cutoff);
	}
	
	public Set<T> keySet(double threshold)
	{
		Set<T> set = new HashSet<>();
		double d;
		
		for (ObjectIntPair<T> p : g_map)
		{
			d = MathUtils.divide(p.i, i_total);
			if (d > threshold) set.add(p.o);
		}
		
		return set;
	}
}
