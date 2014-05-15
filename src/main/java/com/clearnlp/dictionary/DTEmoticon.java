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
package com.clearnlp.dictionary;

import java.io.InputStream;
import java.util.Set;

import com.clearnlp.util.DSUtils;
import com.clearnlp.util.IOUtils;
import com.clearnlp.util.StringUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DTEmoticon
{
	private final String[] POST = {".","?","!","'","\"",",",":",";"};
	private Set<String> g_set;
	
	public DTEmoticon()
	{
		init(IOUtils.getInputStreamsFromClasspath(DTPath.PATH_EMOTICON));
	}
	
	/** Calls {@link #init(InputStream)}. */
	public DTEmoticon(InputStream in)
	{
		init(in);
	}
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. */
	public void init(InputStream in)
	{
		g_set = DSUtils.createStringHashSet(in, true);
	}
	
	public boolean isEmoticon(String s)
	{
		if (g_set.contains(s))
			return true;
		
		if (StringUtils.endsWithAny(s, POST))
			return g_set.contains(s.substring(0, s.length()-1));
		
		return false;
	}
}
