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
package edu.emory.clir.clearnlp.classification.map;

import java.io.Serializable;
import java.util.List;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class LabelMap implements Serializable
{
	private static final long serialVersionUID = -1553968137533402523L;
	private ObjectIntHashMap<String> m_labels;
	private String[] l_labels;
	
	public LabelMap()
	{
		reset();
	}
	
	public void reset()
	{
		m_labels = new ObjectIntHashMap<String>();
		l_labels = new String[0];
	}
	
	public int expand(ObjectIntHashMap<String> map, int cutoff)
	{
		List<String> list = DSUtils.toArrayList(l_labels);
		
		for (ObjectIntPair<String> p : map)
		{
			if (!m_labels.containsKey(p.o) && p.i > cutoff)
			{
				list.add(p.o);
				m_labels.put(p.o, list.size());
			}
		}
		
		l_labels = new String[list.size()];
		list.toArray(l_labels);
		return l_labels.length;
	}
	
	public int getLabelIndex(String label)
	{
		return m_labels.get(label) - 1;
	}
	
	public String[] getLabels()
	{
		return l_labels;
	}
	
	public String getLabel(int index)
	{
		return l_labels[index];
	}
	
	public int size()
	{
		return l_labels.length;
	}
	
	@Override
	public String toString()
	{
		return m_labels.toString();
	}
}