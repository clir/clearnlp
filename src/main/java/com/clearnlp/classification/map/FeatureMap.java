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
package com.clearnlp.classification.map;

import java.io.Serializable;
import java.util.ArrayList;

import com.clearnlp.collection.map.IntObjectHashMap;
import com.clearnlp.collection.map.ObjectIntHashMap;
import com.clearnlp.collection.pair.ObjectIntPair;
import com.clearnlp.util.DSUtils;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class FeatureMap implements Serializable
{
	private static final long serialVersionUID = 1401781367198110209L;
	private ArrayList<ObjectIntHashMap<String>> l_map;
	private int n_features;
	
	public FeatureMap()
	{
		l_map = Lists.newArrayList();
		n_features = 1;
	}
	
	public FeatureMap(int beginIndex)
	{
		l_map = Lists.newArrayList();
		n_features = beginIndex;
	}
	
	public int expand(IntObjectHashMap<ObjectIntHashMap<String>> map, int cutoff)
	{
		expandList(map);
		return expandMap(map, cutoff);
	}
	
	/** Called by {@link #expand(IntObjectHashMap, int)}. */
	private void expandList(IntObjectHashMap<ObjectIntHashMap<String>> map)
	{
		int i, diff = map.getMaxKey() - l_map.size() + 1;
		
		for (i=0; i<diff; i++)
			l_map.add(new ObjectIntHashMap<String>());
		
		l_map.trimToSize();
	}
	
	private int expandMap(IntObjectHashMap<ObjectIntHashMap<String>> map, int cutoff)
	{
		ObjectIntHashMap<String> mnew;
		ObjectIntHashMap<String> morg;
		
		for (ObjectIntPair<ObjectIntHashMap<String>> pn : map)
		{
			mnew = pn.o;
			morg = l_map.get(pn.i);
			
			for (ObjectIntPair<String> ps : mnew)
			{
				if (!morg.containsKey(ps.o) && ps.i > cutoff)
					morg.put(ps.o, n_features++);
			}
		}
		
		return n_features;
	}

	/** @return the index of the specific feature given the specific type if exists; otherwise, {@code -1}. */
	public int getFeatureIndex(int type, String feature)
	{
		return DSUtils.isRange(l_map, type) ? l_map.get(type).get(feature) : -1;
	}
	
	public int size()
	{
		return n_features;
	}
	
	@Override
	public String toString()
	{
		return l_map.toString();
	}
}