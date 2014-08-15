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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clearnlp.collection.pair.ObjectDoublePair;
import com.clearnlp.collection.pair.ObjectIntPair;
import com.google.common.collect.Maps;

public class Increment2DMap<T1,T2> implements Serializable
{
	private static final long serialVersionUID = 4856975632981517711L;
	private Map<T1,Increment1DMap<T2>> g_map;
	
	public Increment2DMap()
	{
		g_map = Maps.newHashMap();
	}
	
	public void add(T1 key1, T2 key2)
	{
		add(key1, key2, 1);
	}
	
	public void add(T1 key1, T2 key2, int inc)
	{
		Increment1DMap<T2> map;
		
		if (g_map.containsKey(key1))
		{
			map = g_map.get(key1);
		}
		else
		{
			map = new Increment1DMap<T2>();
			g_map.put(key1, map);
		}
		
		map.add(key2, inc);
	}
	
	public Set<T1> getKeySet1()
	{
		return g_map.keySet();
	}
	
	public List<ObjectIntPair<T2>> toList(T1 key1, int cutoff)
	{
		Increment1DMap<T2> map = g_map.get(key1);
		return (map != null) ? map.toList(cutoff) : null;
	}
	
	public List<ObjectDoublePair<T2>> toList(T1 key1, double threshold)
	{
		Increment1DMap<T2> map = g_map.get(key1);
		return (map != null) ? map.toList(threshold) : null;
	}
}
