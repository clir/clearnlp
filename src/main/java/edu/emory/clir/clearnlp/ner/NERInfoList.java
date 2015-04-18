/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.ner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERInfoList implements Serializable
{
	private static final long serialVersionUID = -8359112558387337429L;
	private List<NERInfo> info_list;
	private String wikipedia_title;
	private int correct_count;
	
	public NERInfoList(String title)
	{
		info_list = new ArrayList<>();
		setCorrectCount(0);
		setWikipediaTitle(title);
	}
	
	public NERInfoList()
	{
		this(null);
	}
	
	public boolean isEmpty()
	{
		return info_list.isEmpty();
	}

	public List<NERInfo> getList()
	{
		return info_list;
	}
	
	public void merge(NERInfoList list)
	{
		for (NERInfo info : list.getList())
			pick(info.getNamedEntityTag(), info.getPickCount());
	}
	
	public void add(NERInfo info)
	{
		info_list.add(info);
		sort(info_list.size()-1);
	}
	
	private void sort(int index)
	{
		NERInfo curr, prev;
		
		for (int i=index; i>0; i--)
		{
			curr = info_list.get(i);
			prev = info_list.get(i-1);
			
			if (curr.compareTo(prev) > 0)
				DSUtils.swap(info_list, i, i-1);
			else
				break;
		}
	}

	public int getCorrectCount()
	{
		return correct_count;
	}
	
	public void setCorrectCount(int correctCount)
	{
		this.correct_count = correctCount;
	}
	
	public String wikipediaTitle()
	{
		return wikipedia_title;
	}
	
	public void setWikipediaTitle(String title)
	{
		wikipedia_title = title;
	}
	
	public void addCorrectCount(int count)
	{
		correct_count += count;
	}
	
	public boolean pick(String tag, int inc)
	{
		int i, size = info_list.size();
		NERInfo info;
		
		for (i=0; i<size; i++)
		{
			info = info_list.get(i);
			
			if (info.isNamedEntityTag(tag))
			{
				info.incrementPickCount(inc);
				sort(i); return true;
			}
		}
		
		info_list.add(new NERInfo(tag, 1));
		sort(info_list.size()-1);
		return false;
	}
	
	public String joinTags(String delim)
	{
		StringJoiner join = new StringJoiner(delim);
		int i, len = info_list.size();
		
		for (i=0; i<len; i++)
			join.add(info_list.get(i).getNamedEntityTag());
		
		return join.toString();
	}
	
	@Override
	public String toString()
	{
		return info_list.toString();
	}
}
