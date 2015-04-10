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

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERInfo implements Serializable, Comparable<NERInfo>
{
	private static final long serialVersionUID = 2794431149567102097L;
	private String named_entity_tag;
	private int pick_count;

	public NERInfo(String namedEntityTag)
	{
		setNamedEntityTag(namedEntityTag);
	}
	
	public NERInfo(String namedEntityTag, int pickCount)
	{
		setNamedEntityTag(namedEntityTag);
		setPickCount(pickCount);
	}
	
	public String getNamedEntityTag()
	{
		return named_entity_tag;
	}
	
	public void setNamedEntityTag(String tag)
	{
		named_entity_tag = tag;
	}
	
	public boolean isNamedEntityTag(String tag)
	{
		return named_entity_tag.equals(tag);
	}
	
	public int getPickCount()
	{
		return pick_count;
	}

	public void setPickCount(int count)
	{
		pick_count = count;
	}
	
	public void incrementPickCount(int inc)
	{
		pick_count += inc;
	}

	@Override
	public int compareTo(NERInfo o)
	{
		return pick_count - o.pick_count;
	}
	
	@Override
	public String toString()
	{
		return named_entity_tag+":"+pick_count;
	}
}
