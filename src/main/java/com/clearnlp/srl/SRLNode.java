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
package com.clearnlp.srl;

import java.util.Collection;
import java.util.List;

import com.clearnlp.util.Joiner;
import com.clearnlp.util.arc.AbstractArc;
import com.clearnlp.util.constant.StringConst;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class SRLNode<NodeType>
{
	private List<AbstractArc<NodeType>> l_predicates;
	private List<AbstractArc<NodeType>> l_arguments;
	private String                      s_senseID;
	
	public SRLNode()
	{
		l_predicates = Lists.newArrayList();
		l_arguments  = Lists.newArrayList();
	}
	
	public List<AbstractArc<NodeType>> getPredicateList()
	{
		return l_predicates;
	}
	
	public List<AbstractArc<NodeType>> getArgumentList()
	{
		return l_arguments;
	}
	
	public AbstractArc<NodeType> getArgument(int index)
	{
		return l_arguments.get(index);
	}
	
	public int getArgumentSize()
	{
		return l_arguments.size();
	}
	
	/** @return the sense ID (e.g., roleset ID) if exists; otherwise, {@code null}. */
	public String getSenseID()
	{
		return s_senseID;
	}
	
	public void addPredicate(AbstractArc<NodeType> arc)
	{
		l_predicates.add(arc);
	}
	
	public void addArgument(AbstractArc<NodeType> arc)
	{
		l_arguments.add(arc);
	}
	
	public boolean isPredicateOf(NodeType node)
	{
		return isOf(node, l_arguments);
	}
	
	public boolean isArgumentOf(NodeType node)
	{
		return isOf(node, l_predicates);
	}
	
	private boolean isOf(NodeType node, Collection<AbstractArc<NodeType>> col)
	{
		for (AbstractArc<NodeType> arc : col)
		{
			if (arc.isNode(node))
				return true;
		}
		
		return false;
	}
	
	public void setSenseID(String senseID)
	{
		s_senseID = senseID;
	}
	
	public boolean isPredicate()
	{
		return l_arguments != null && !l_arguments.isEmpty();
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		if (isPredicate())
		{
			build.append("Sense = ");
			build.append(s_senseID);
			
			build.append(", A = ");
			build.append(StringConst.LCB);
			build.append(Joiner.join(l_arguments, StringConst.COMMA));
			build.append(StringConst.RCB);
		}
		else if (!l_predicates.isEmpty())
		{
			build.append("P = ");
			build.append(StringConst.LCB);
			build.append(Joiner.join(l_predicates, StringConst.COMMA, true));
			build.append(StringConst.RCB);
			build.append(StringConst.SPACE);
		}
		
		return build.toString().trim();
	}
}