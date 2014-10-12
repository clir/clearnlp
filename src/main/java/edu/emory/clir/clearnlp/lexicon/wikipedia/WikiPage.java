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

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiPage implements Serializable
{
	private static final long serialVersionUID = -4615946922954348026L;
	private List<WikiParagraph> l_paragraphs;
	private String s_title;
	private String s_url;
	
	public WikiPage(String title, String url)
	{
		l_paragraphs = Lists.newArrayList();
		setTitle(title);
		setURL(url);
	}
	
	public List<WikiParagraph> getParagraphs()
	{
		return l_paragraphs;
	}
	
	public void addParagraph(WikiParagraph paragraph)
	{
		l_paragraphs.add(paragraph);
	}
	
	public String getTitle()
	{
		return s_title;
	}
	
	public void setTitle(String title)
	{
		s_title = title;
	}
	
	public String getURL()
	{
		return s_url;
	}
	
	public void setURL(String url)
	{
		s_url = url;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		for (WikiParagraph paragraph : l_paragraphs)
		{
			build.append(StringConst.NEW_LINE);
			build.append(paragraph.toString());
			build.append(StringConst.NEW_LINE);
		}
		
		return build.toString().trim();
	}
}
