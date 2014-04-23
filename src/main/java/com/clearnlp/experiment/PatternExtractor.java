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
package com.clearnlp.experiment;

import java.util.List;
import java.util.Set;

import com.clearnlp.collection.map.ObjectIntHashMap;
import com.clearnlp.constant.StringConst;
import com.clearnlp.constituent.CTNode;
import com.clearnlp.constituent.CTReader;
import com.clearnlp.constituent.CTTagEn;
import com.clearnlp.constituent.CTTree;
import com.clearnlp.dictionary.DTCurrency;
import com.clearnlp.dictionary.DTNumberEn;
import com.clearnlp.pos.POSTagEn;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.IOUtils;
import com.clearnlp.util.StringUtils;
import com.clearnlp.util.pair.ObjectIntPair;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class PatternExtractor implements POSTagEn
{
	private DTCurrency d_currency;
	private DTNumberEn d_number;
	
	public PatternExtractor(String inputFile)
	{
		d_currency = new DTCurrency();
		d_number   = new DTNumberEn();
		
		CTReader reader = new CTReader(IOUtils.createFileInputStream(inputFile));
		List<List<CTNode>> list = Lists.newArrayList();
		String ctag = CTTagEn.C_QP;
		CTTree tree;
		
		ObjectIntHashMap<String> map = new ObjectIntHashMap<String>();
		
		while ((tree = reader.nextTree()) != null)
			extractTerminals(list, tree.getRoot(), ctag);
		
		extractQuantifierPhrases(list);
		
		List<ObjectIntPair<String>> ps = map.toList();
		DSUtils.sortReverseOrder(ps);
		
		for (ObjectIntPair<String> p : ps)
			System.err.println(p.o+"\t"+p.i);
	}
	
	private void extractTerminals(List<List<CTNode>> list, CTNode node, String tag)
	{
		if (node.isConstituentTag(tag))
			list.add(node.getTerminalList());
		else
		{
			for (CTNode child : node.getChildrenList())
				extractTerminals(list, child, tag);
		}
	}
	
	private void extractQuantifierPhrases(List<List<CTNode>> list)
	{
		ObjectIntHashMap<String> map = new ObjectIntHashMap<String>();
		Set<String> set = Sets.newHashSet();
		
		for (List<CTNode> nodes : list)
			map.add(getFormsQP(nodes, set));
		
		List<ObjectIntPair<String>> ps = map.toList();
		DSUtils.sortReverseOrder(ps);
		
		for (ObjectIntPair<String> p : ps)
		{
			System.out.println(p.o+"\t"+p.i);
		}			
	}
	
	private String getFormsQP(List<CTNode> nodes, Set<String> set)
	{
		Set<CTNode> remove = Sets.newHashSet();
		CTNode prev, curr, next, prev2;
		int i, size = nodes.size();
		
		for (i=0; i<size; i++)
		{
			curr = nodes.get(i);
			if (!isNumber(curr)) continue;
			
			prev = DSUtils.get(nodes, i-1);
			next = DSUtils.get(nodes, i+1);
			
			if (prev != null)
			{
				if (isNumber(prev) || prev.isConstituentTag(POS_DOLLAR))
				{
					remove.add(prev);
				}
				else if (i-2 >= 0)
				{
					if (prev.isWordForm(StringConst.HYPHEN))
					{
						prev2 = nodes.get(i-2);
						
						if (isNumber(prev2))
						{
							remove.add(prev);
							remove.add(prev2);
						}						
					}
				}
			}
			
			if (next != null)
			{
				if (next.isWordForm(StringConst.PERCENT))
					remove.add(next);
			}
			
		}
		
		nodes.removeAll(remove);
		
		
		
		
//		curr = DSUtils.getLast(nodes);
//		if (count > 1 || nodes.size() < 3 || curr == null || !curr.isConstituentTag(POS_CD)) return "";
		
		
		
		
		StringBuilder build = new StringBuilder();
		int count = 0;
		String lower;
		
		for (CTNode node : nodes)
		{
			lower = StringUtils.toLowerCase(node.getWordForm());
			build.append(StringConst.SPACE);
			
			if (d_currency.isCurrency(lower))
				set.add(lower);
			
			if (isNumber(node))
			{
				build.append("NUM");
				count++;
			}
			else
				build.append(lower);
		}
		
		return count > 0 ? "" : build.substring(1);
	}
	
	private boolean isNumber(CTNode node)
	{
		String lower = StringUtils.toLowerCase(node.getWordForm());
		return node.isConstituentTag(POS_CD) || d_number.isCardinal(lower) || d_number.isOrdinal(lower) || lower.equals("half") || lower.equals("halves") || lower.equals("quarter") || lower.equals("quarters");
	}
	
	static public void main(String[] args)
	{
		new PatternExtractor(args[0]);
	}
}