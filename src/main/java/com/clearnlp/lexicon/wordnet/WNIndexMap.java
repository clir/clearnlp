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
package com.clearnlp.lexicon.wordnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.clearnlp.util.IOUtils;
import com.google.common.collect.Maps;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WNIndexMap
{
	Map<String,WNIndex> m_index;
	
	/**
	 * @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. 
	 * @throws IOException
	 */
	public WNIndexMap(InputStream in, WNDataMap map) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		WNIndex index;
		String line;
		
		m_index = Maps.newHashMap();
		
		while ((line = reader.readLine()) != null)
		{
			if (line.startsWith("  ")) continue;
			index = new WNIndex(map, line);
			m_index.put(index.getLemma(), index);
		}
		
		reader.close();
	}
	
	public WNIndex getIndex(String lemma)
	{
		return m_index.get(lemma);
	}
}