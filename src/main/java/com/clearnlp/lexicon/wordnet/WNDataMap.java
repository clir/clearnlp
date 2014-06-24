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

import com.clearnlp.collection.map.IntObjectHashMap;
import com.clearnlp.collection.pair.ObjectIntPair;
import com.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class WNDataMap
{
	private IntObjectHashMap<WNSynset> m_data;
	
	/**
	 * @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. 
	 * @throws IOException
	 */
	public WNDataMap(InputStream in) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		WNSynset synset;
		String line;
		
		m_data = new IntObjectHashMap<WNSynset>();
		
		while ((line = reader.readLine()) != null)
		{
			if (line.startsWith("  ")) continue;
			synset = new WNSynset(line);
			m_data.put(synset.getSynsetOffset(), synset);
		}
		
		reader.close();
	}
	
	void initRelations(WNMap map)
	{
		for (ObjectIntPair<WNSynset> p : m_data)
			p.o.initRelations(map);
	}
	
	public WNSynset getSynset(int offset)
	{
		return m_data.get(offset);
	}
}