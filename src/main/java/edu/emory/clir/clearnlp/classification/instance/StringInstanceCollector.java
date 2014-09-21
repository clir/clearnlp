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
package edu.emory.clir.clearnlp.classification.instance;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringInstanceCollector extends AbstractInstanceCollector<StringInstance, StringFeatureVector>
{
	private IntObjectHashMap<ObjectIntHashMap<String>> m_features;
	
	@Override
	public void init()
	{
		initDefault();
		m_features = new IntObjectHashMap<ObjectIntHashMap<String>>();
	}

	@Override
	protected void addFeatures(StringFeatureVector vector)
	{
		int i, type, size = vector.size();
		ObjectIntHashMap<String> map;
		String value;
		
		for (i=0; i<size; i++)
		{
			type  = vector.getType(i);
			value = vector.getValue(i);
			map   = m_features.get(type);
			
			if (map == null)
			{
				map = new ObjectIntHashMap<String>();
				m_features.put(type, map);
			}
			
			map.add(value);
		}
	}
	
	public IntObjectHashMap<ObjectIntHashMap<String>> getFeatureMap()
	{
		return m_features;
	}
}