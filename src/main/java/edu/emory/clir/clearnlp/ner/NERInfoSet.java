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
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERInfoSet implements Serializable
{
	private static final long serialVersionUID = -1117096280703855387L;
	private Set<String> category_set;
	private int correct_count;
	
	public NERInfoSet()
	{
		category_set = new TreeSet<>();
		setCorrectCount(0);
	}
	
	public Set<String> getCategorySet()
	{
		return category_set;
	}
	
	public void addCategory(String category)
	{
		category_set.add(category);
	}
	
	public void addCategories(Collection<String> categories)
	{
		category_set.addAll(categories);
	}

	public int getCorrectCount()
	{
		return correct_count;
	}
	
	public void setCorrectCount(int correctCount)
	{
		this.correct_count = correctCount;
	}
	
	public void addCorrectCount(int count)
	{
		correct_count += count;
	}
	
	public String joinTags(String delim)
	{
		return Joiner.join(category_set, delim);
	}
}
