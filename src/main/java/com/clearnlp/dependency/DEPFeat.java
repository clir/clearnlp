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
package com.clearnlp.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.clearnlp.reader.TSVReader;
import com.google.common.collect.Maps;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPFeat
{
	/** The delimiter between feature values ({@code ","}). */
	static public final String DELIM_VALUES    = ",";
	/** The delimiter between features ({@code "|"}). */
	static public final String DELIM_FEATS     = "|";
	/** The delimiter between keys and values ({@code "="}). */
	static public final String DELIM_KEY_VALUE = "=";
	
	static private final Pattern P_FEATS = Pattern.compile("\\"+DELIM_FEATS);
	private Map<String,String> m_feats;

	/** Constructs an empty feature map. */
	public DEPFeat()
	{
		m_feats = Maps.newHashMap();
	}
	
	public DEPFeat(DEPFeat feats)
	{
		m_feats = Maps.newHashMap(feats.m_feats);
	}
	
	/**
	 * Constructs a feature map by decoding the specific features.
	 * @param feats the features to be added.
	 * See the {@code feats} parameter in {@link DEPFeat#add(String)}.
	 */
	public DEPFeat(String feats)
	{
		m_feats = Maps.newHashMap();
		add(feats);
	}
	
	public String get(String key)
	{
		return m_feats.get(key);
	}
	
	public String put(String key, String value)
	{
		return m_feats.put(key, value);
	}
	
	public String remove(String key)
	{
		return m_feats.remove(key);
	}
	
	public boolean contains(String key)
	{
		return m_feats.containsKey(key);
	}
		
	/**
	 * Adds the specific features to this map.
	 * @param feats {@code "_"} or {@code feat(|feat)*}.<br>
	 * {@code "_"}: indicates no feature.<br>
	 * {@code feat ::= key=value} (e.g., {@code pos=VBD}).
	 */
	public void add(String feats)
	{
		if (feats.equals(TSVReader.BLANK))
			return;
		
		String key, value;
		int    idx;
		
		for (String feat : P_FEATS.split(feats))
		{
			idx = feat.indexOf(DELIM_KEY_VALUE);
			
			if (idx > 0)
			{
				key   = feat.substring(0, idx);
				value = feat.substring(idx+1);
				m_feats.put(key, value);				
			}
		}
	}

	@Override
	public String toString()
	{
		if (m_feats.isEmpty())	return TSVReader.BLANK;
		
		StringBuilder build = new StringBuilder();
		List<String>  keys  = new ArrayList<String>(m_feats.keySet());
		
		Collections.sort(keys);
		for (String key : keys)
		{
			build.append(DELIM_FEATS);
			build.append(key);
			build.append(DELIM_KEY_VALUE);
			build.append(m_feats.get(key));
		}
		
		return build.toString().substring(DELIM_FEATS.length());
	}
}