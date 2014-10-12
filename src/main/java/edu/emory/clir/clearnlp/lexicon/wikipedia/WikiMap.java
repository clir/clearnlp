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
package edu.emory.clir.clearnlp.lexicon.wikipedia;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiMap implements Serializable
{
	private static final long serialVersionUID = 1455324906977350703L;
	private static final String NEW_PAGE = "<New Page";
	private static final String NEW_PARAGRAPH = "<New Paragraph";
	
	private Map<String,WikiPage> m_wiki;
	
	public WikiMap()
	{
		m_wiki = Maps.newHashMap();
	}
	
	public WikiPage getPage(String title)
	{
		return m_wiki.get(title);
	}
	
	/** Internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. */
	public void addPages(InputStream in)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			WikiPage page = getFirstPage(reader);
			put(page);
			
			while ((page = nextPage(reader, page)) != null)
				put(page);
			
			in.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	private void put(WikiPage page)
	{
		m_wiki.put(page.getTitle(), page);	
	}
	
	private WikiPage getFirstPage(BufferedReader reader) throws Exception
	{
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			line = line.trim();
			
			if (line.startsWith(NEW_PAGE))
				return createPage(line);
		}
		
		return null;
	}
	
	private WikiPage createPage(String line)
	{
		String[] s = Splitter.splitTabs(line);
		String title  = s[1].trim();
		String url    = s[3].trim();
		return new WikiPage(title, url.substring(0, url.length()-1));
	}
	
	private WikiPage nextPage(BufferedReader reader, WikiPage page) throws Exception
	{
		WikiParagraph paragraph = null;
		String line;
		
		while ((line = reader.readLine()) != null)
		{		
			line = line.trim();
			
			if (line.startsWith(NEW_PARAGRAPH))
			{
				paragraph = new WikiParagraph();
				page.addParagraph(paragraph);
			}
			else if (line.startsWith(NEW_PAGE))
				return createPage(line);
			else
				paragraph.addSentence(line);
		}
		
		return null;
	}
}
