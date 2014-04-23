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
package com.clearnlp.util.arc;

import com.clearnlp.dependency.DEPNode;
import com.clearnlp.propbank.PBLib;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class SRLArc extends AbstractArc<DEPNode>
{
	private String s_numberedArgumentTag;
	
	public SRLArc(DEPNode node, String label)
	{
		set(node, label, null);
	}
	
	public SRLArc(DEPNode node, String label, String numberedArgumentTag)
	{
		set(node, label, numberedArgumentTag);
	}
	
	public SRLArc(SRLArc arc)
	{
		set(arc.getNode(), arc.getLabel(), arc.getNumberedArgumentTag());
	}
	
	public String getNumberedArgumentTag()
	{
		return s_numberedArgumentTag;
	}
	
	public void setNumberedArgumentTag(String tag)
	{
		s_numberedArgumentTag = tag;
	}
	
	public void set(DEPNode node, String label, String numberedArgumentTag)
	{
		set(node, label);
		setNumberedArgumentTag(numberedArgumentTag);
	}
	
	public String toString(boolean includeNumberedArgumentTag)
	{
		StringBuilder build = new StringBuilder();
		
		build.append(n_node.getID());
		build.append(DELIM);
		build.append(s_label);
		
		if (includeNumberedArgumentTag && s_numberedArgumentTag != null)
		{
			build.append(PBLib.DELIM_FUNCTION_TAG);
			build.append(s_numberedArgumentTag);
		}
		
		return build.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(n_node.getID());
		build.append(DELIM);
		build.append(s_label);
		
		return build.toString();
	}

	@Override
	public int compareTo(AbstractArc<DEPNode> arc)
	{
		return s_label.compareTo(arc.getLabel());
	}
}