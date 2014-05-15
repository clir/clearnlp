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
package com.clearnlp.util.regex;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.clearnlp.util.pair.ObjectIntPair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class PatternReplacer
{
	private final List<ObjectIntPair<Pattern>> l_patterns;
	private final Map<String,String> m_protectors;
	
	public PatternReplacer(List<ObjectIntPair<Pattern>> patterns, Map<String,String> protector)
	{
		l_patterns   = patterns;
		m_protectors = protector;
	}
	
	public String replace(String s) 
	{
		for (ObjectIntPair<Pattern> p : l_patterns)
			s = replace(s, p.o, p.i);
		
		return s;
	}
	
	/** Called by {@link #replace(String)}. */
	private String replace(String s, Pattern pattern, int index)
	{
		Matcher m = pattern.matcher(s);
		StringBuffer sb = null;
		
		while (m.find())
		{
			if (sb == null) sb = new StringBuffer();
			appendReplacement(sb, m, index);
		}
		
		if (sb == null)
			return s;
		else
		{
			m.appendTail(sb);
			return sb.toString();
		}
	}
	
	/** Called by {@link #replace(String, Pattern, int)}. */
	private void appendReplacement(StringBuffer sb, Matcher m, int index)
	{
		int i, size = m.groupCount();
		
		for (i=0; i<index; i++)
			m.appendReplacement(sb, m.group(i));
		
		m.appendReplacement(sb, m_protectors.get(m.group(index)));
		
		for (i=index+1; i<=size; i++)
			m.appendReplacement(sb, m.group(i));
	}
}