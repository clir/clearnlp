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
package edu.emory.clir.clearnlp.dependency;

import java.util.HashMap;
import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.reader.TSVReader;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPFeat extends HashMap<String,String>
{
	private static final long serialVersionUID = 4093725541292286982L;
	/** The delimiter between feature values ({@code ","}). */
	static public final String DELIM_VALUES    = ",";
	/** The delimiter between features ({@code "|"}). */
	static public final String DELIM_FEATS     = "|";
	/** The delimiter between keys and values ({@code "="}). */
	static public final String DELIM_KEY_VALUE = "=";

	static private final Pattern P_FEATS = Pattern.compile("\\"+DELIM_FEATS);

	/** Constructs an empty feature map. */
	public DEPFeat()
	{
		super();
	}
	
	public DEPFeat(DEPFeat feats)
	{
		super(feats);
	}
	
	/**
	 * Constructs a feature map by decoding the specific features.
	 * @param feats the features to be added.
	 * See the {@code feats} parameter in {@link DEPFeat#add(String)}.
	 */
	public DEPFeat(String feats)
	{
		super();
		add(feats);
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
				put(key, value);				
			}
		}
	}

	@Override
	public String toString()
	{
		if (isEmpty())	return TSVReader.BLANK;
		
		StringBuilder build = new StringBuilder();
		
		for (Entry<String, String> entry : entrySet())
		{
			build.append(DELIM_FEATS);
			build.append(entry.getKey());
			build.append(DELIM_KEY_VALUE);
			build.append(entry.getValue());
		}
		
		return build.toString().substring(DELIM_FEATS.length());
	}
}