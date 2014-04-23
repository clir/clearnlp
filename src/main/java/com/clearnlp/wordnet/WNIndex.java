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
package com.clearnlp.wordnet;

import java.util.ArrayList;
import java.util.List;

import com.clearnlp.util.regex.Splitter;
import com.google.common.collect.Lists;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class WNIndex
{
	private String			s_lemma;
	private char			c_posTag;
	private List<WNSynset>	w_synsets;
	private byte			n_tagsenseCount;

	public WNIndex()
	{
		w_synsets = Lists.newArrayList();
	}
	
	/**
	 * @param line a line from the WordNet index file (e.g., index.verb).
	 * e.g., "study v 6 5 @ ~ * $ + 6 6 00644583 00607405 02166460 00599992 00607114 00704388"
	 */
	public WNIndex(WNDataMap map, String line)
	{
		String[] t = Splitter.splitSpace(line);
		int i, count, offset, idx = 0;
		
		setLemma(t[idx++]);
		setPOSTag(t[idx++].charAt(0));
		count = Integer.parseInt(t[idx++]);
		
		idx += Integer.parseInt(t[idx]) + 1;	// skip pointers
		idx++;	// skip sense count (redundant to synsetCount)
		setTagsenseCount(Byte.parseByte(t[idx++]));

		w_synsets = new ArrayList<WNSynset>(count); 
		
		for (i=0; i<count; i++)
		{
			offset = Integer.parseInt(t[idx++]);
			addSynset(map.getSynset(offset));
		}
	}

//	------------------------------------ Getters/Setters ------------------------------------
	
	public String getLemma()
	{
		return s_lemma;
	}
	
	public char getPOSTag()
	{
		return c_posTag;
	}
	
	public int getTagsenseCount()
	{
		return n_tagsenseCount;
	}
	
	public List<WNSynset> getSynsetList()
	{
		return w_synsets;
	}
	
	public WNSynset getSynset(int senseID)
	{
		return w_synsets.get(senseID);
	}
	
	public void setLemma(String lemma)
	{
		s_lemma = lemma;
	}
	
	public void setPOSTag(char tag)
	{
		c_posTag = tag;
	}
	
	public void setTagsenseCount(byte count)
	{
		n_tagsenseCount = count;
	}
	
	public void addSynset(WNSynset synset)
	{
		w_synsets.add(synset);
	}
}