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
package com.clearnlp.classification.vector;

import java.util.ArrayList;

import com.clearnlp.collection.list.IntArrayList;
import com.google.common.collect.Lists;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringFeatureVector extends AbstractFeatureVector
{
	private IntArrayList      i_types;
	private ArrayList<String> s_values;
	
	public StringFeatureVector()
	{
		super(false);
		init();
	}
	
	public StringFeatureVector(boolean hasWeight)
	{
		super(hasWeight);
		init();
	}
	
	private void init()
	{
		i_types  = new IntArrayList();
		s_values = Lists.newArrayList();
	}
	
	/**
	 * @param type  the feature type.
	 * @param value the feature value.
	 */
	public void addFeature(int type, String value)
	{
		if (hasWeight())
			addFeature(type, value, 1);
		else
		{
			i_types .add(type);
			s_values.add(value);
		}
	}
	
	/**
	 * @param type the feature type.
	 * @param value the feature value.
	 * @param weight the feature weight.
	 */
	public void addFeature(int type, String value, double weight)
	{
		i_types  .add(type);
		s_values .add(value);
		d_weights.add(weight);
	}
	
	/** @return the index'th feature type. */
	public int getType(int index)
	{
		return i_types.get(index);
	}
	
	/** @return the index'th feature value. */
	public String getValue(int index)
	{
		return s_values.get(index);
	}
	
	@Override
	public int size()
	{
		return i_types.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return i_types.isEmpty();
	}
	
	@Override
	public void trimToSize()
	{
		i_types .trimToSize();
		s_values.trimToSize();
		if (hasWeight()) d_weights.trimToSize();
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		int i, size = size();
		
		for (i=0; i<size; i++)
		{
			build.append(DELIM_FEATURE);
			build.append(getType(i));
			build.append(DELIM_WEIGHT);
			build.append(getValue(i));
			
			if (hasWeight())
			{
				build.append(DELIM_WEIGHT);
				build.append(getWeight(i));
			}
		}
		
		return build.toString().substring(DELIM_FEATURE.length());
	}
}