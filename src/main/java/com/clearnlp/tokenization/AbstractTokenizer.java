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
package com.clearnlp.tokenization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.clearnlp.util.IOUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 1.1.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractTokenizer
{
	protected Set<String> s_protectedForms;
	protected boolean b_twit = false;
	
	public AbstractTokenizer()
	{
		s_protectedForms = Sets.newHashSet();
	}
	
	/**
	 * @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}.
	 * @return a list of token in the specific reader.
	 */
	public List<TOKNode> getTokenList(InputStream in)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		ArrayList<TOKNode> tokens = Lists.newArrayList();
		String line;
		
		try
		{
			while ((line = reader.readLine()) != null)
				tokens.addAll(getTokenList(line));
		}
		catch (IOException e) {e.printStackTrace();}
		
		tokens.trimToSize();
		return tokens;
	}
	
	/** @return a list of tokens from the specific string. */
	abstract public List<TOKNode> getTokenList(String rawText);
	
	public void setTwitter(boolean isTwitter)
	{
		b_twit = isTwitter;
	}
	
	public void addProtectedForm(String form)
	{
		s_protectedForms.add(form);
	}
	
	public void removeProtectedForm(String form)
	{
		s_protectedForms.remove(form);
	}
}

