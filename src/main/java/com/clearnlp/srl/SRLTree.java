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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.clearnlp.constant.StringConst;
import com.clearnlp.constituent.CTLibEn;
import com.clearnlp.dependency.DEPLib;
import com.clearnlp.dependency.DEPLibEn;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.pos.POSLibEn;
import com.clearnlp.propbank.PBLib;
import com.clearnlp.util.arc.SRLArc;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class SRLTree
{
	private DEPNode      d_predicate;
	private List<SRLArc> l_arguments;
	
	public SRLTree(DEPNode predicate)
	{
		d_predicate = predicate;
		l_arguments = new ArrayList<SRLArc>();
	}
	
//	====================================== Getters ======================================
	
	public DEPNode getPredicate()
	{
		return d_predicate;
	}
	
	public String getRolesetID()
	{
		return d_predicate.getFeat(DEPLib.FEAT_PB);
	}
	
	public Set<String> getBaseLabelSet()
	{
		Set<String> labels = Sets.newHashSet();
		
		for (SRLArc arc : l_arguments)
			labels.add(PBLib.getBaseLabel(arc.getLabel()));
		
		return labels;
	}
	
	public SRLArc getFirstArgument(String label)
	{
		for (SRLArc arc : l_arguments)
		{
			if (arc.isLabel(label))
				return arc;
		}
		
		return null;
	}
	
	public List<SRLArc> getArgumentArcList()
	{
		return l_arguments;
	}
	
	public List<SRLArc> getArgumentArcList(Pattern pattern)
	{
		List<SRLArc> args = Lists.newArrayList();
		
		for (SRLArc arc : l_arguments)
		{
			if (arc.isLabel(pattern))
				args.add(arc);
		}
		
		return args;
	}
	
	public List<DEPNode> getArgumentNodeList(Pattern pattern)
	{
		List<DEPNode> args = Lists.newArrayList();
		
		for (SRLArc arc : l_arguments)
		{
			if (arc.isLabel(pattern))
				args.add(arc.getNode());
		}
		
		return args;
	}
	
	public String getKeyEn(Pattern ignore, String delim)
	{
		StringBuilder build = new StringBuilder();
		Collections.sort(l_arguments);
		DEPNode node;
		String value;
		
		build.append("V:");
		build.append(getRolesetID());
		
		for (SRLArc arg : l_arguments)
		{
			if (!ignore.matcher(arg.getLabel()).find())
			{
				node = arg.getNode();
				
				build.append(";");
				build.append(arg.getLabel());
				build.append(":");
				
				if (POSLibEn.isNoun(node.getPOSTag()))
					value = DEPLibEn.getSubLemmasForNP(node, delim);
				else if (node.isPOSTag(CTLibEn.POS_IN))
					value = DEPLibEn.getSubLemmasForPP(node, delim);
				else
					value = node.getLemma();
				
				build.append(value);
			}
		}
		
		return build.toString();
	}

//	====================================== Setters ======================================
	
	public void addArgument(SRLArc arc)
	{
		l_arguments.add(arc);
	}

//	====================================== Booleans ======================================
	
	/** @param label the specific label is internally converted by {@link PBLib#getBaseLabel(String)}. */
	public boolean containsLabel(String label)
	{
		label = PBLib.getBaseLabel(label);
		
		for (SRLArc arc : l_arguments)
		{
			if (label.equals(PBLib.getBaseLabel(arc.getLabel())))
				return true;
		}
		
		return false;
	}
	
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(getRolesetID());
		
		for (SRLArc arc : l_arguments)
		{
			build.append(StringConst.SPACE);
			build.append(arc.toString());
		}
		
		return build.toString();
	}
}