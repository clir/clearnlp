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
package com.clearnlp.dictionary.universal;

import java.io.InputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.clearnlp.collection.tree.AffixTree;
import com.clearnlp.dictionary.DTPath;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DTEmoticon
{
	private final Pattern EMOTICON = Pattern.compile("[\\!\\|;:#%][-]*[\\(\\)\\[\\]\\{\\}\\|<>]+");
	private Set<String> s_emoticon;
	private AffixTree   t_prefix;
	private AffixTree   t_suffix;
	
	public DTEmoticon()
	{
		init(IOUtils.getInputStreamsFromClasspath(DTPath.EMOTICONS));
	}
	
	public DTEmoticon(InputStream in)
	{
		init(in);
	}
	
	public void init(InputStream in)
	{
		s_emoticon = DSUtils.createStringHashSet(in, true, false);
		t_prefix = new AffixTree(true);		t_prefix.addAll(s_emoticon);
		t_suffix = new AffixTree(false);	t_suffix.addAll(s_emoticon);
	}
	
	public int[] getEmoticonRange(String s)
	{
		if (s_emoticon.contains(s))
			return new int[]{0, s.length()};
		
		Matcher m = EMOTICON.matcher(s);
		
		if (m.find())
			return new int[]{m.start(), m.end()};
		
		int idx;
		
		if ((idx = t_prefix.getAffixIndex(s, false)) >= 0)
			return new int[]{0, idx+1};
		
		if ((idx = t_suffix.getAffixIndex(s, false)) >= 0)
			return new int[]{idx, s.length()};
		
		return null;
	}
}
