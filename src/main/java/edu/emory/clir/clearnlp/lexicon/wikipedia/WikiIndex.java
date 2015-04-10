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
package edu.emory.clir.clearnlp.lexicon.wikipedia;

import java.io.Serializable;


/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiIndex implements Serializable
{
	private static final long serialVersionUID = 68681222468484660L;
	private String entry_name;
	private long   begin_pointer;
	
	public WikiIndex(String entryName, long beginPointer)
	{
		setEntryName(entryName);
		setBeginPointer(beginPointer);
	}
	
	public String getEntryName()
	{
		return entry_name;
	}

	public void setEntryName(String entryName)
	{
		entry_name = entryName;
	}

	public long getBeginPointer()
	{
		return begin_pointer;
	}

	public void setBeginPointer(long beginPointer)
	{
		begin_pointer = beginPointer;
	}

	@Override
	public String toString()
	{
		return entry_name+":"+begin_pointer;
	}
}
