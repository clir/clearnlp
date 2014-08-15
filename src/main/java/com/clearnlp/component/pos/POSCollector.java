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
package com.clearnlp.component.pos;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clearnlp.collection.map.Increment2DMap;
import com.clearnlp.collection.map.ObjectIntHashMap;
import com.clearnlp.collection.pair.ObjectDoublePair;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.Joiner;
import com.clearnlp.util.StringUtils;
import com.clearnlp.util.constant.StringConst;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class POSCollector implements Serializable
{
	private static final long serialVersionUID = -1309316221225281613L;
	
	private Increment2DMap<String,String> m_ambi;
	private ObjectIntHashMap<String>      m_lswf;
	private Set<String>                   s_lswf;
	private int                           n_trees;
	
	private double ac_threshold;	// ambiguity classes
	private int    db_cutoff;		// document boundary
	private int    df_cutoff;		// document frequency
	
	public POSCollector(POSConfig config)
	{
		m_ambi  = new Increment2DMap<>();
		m_lswf  = new ObjectIntHashMap<>();
		s_lswf  = Sets.newHashSet();
		n_trees = 0;
		
		setAmbiguityClassThreshold(config.getAmbiguityClassThreshold());
		setDocumentBoundaryCutoff(config.getDocumentBoundaryCutoff());
		setDocumentFrequencyCutoff(config.getDocumentFrequencyCutoff());
	}
	
	public void setAmbiguityClassThreshold(double threshold)
	{
		ac_threshold = threshold;
	}
	
	public void setDocumentBoundaryCutoff(int cutoff)
	{
		db_cutoff = cutoff;
	}
	
	public void setDocumentFrequencyCutoff(int cutoff)
	{
		df_cutoff = cutoff;
	}
	
	public void collect(POSState state)
	{
		int i, size = state.getTreeSize();
		DEPNode node;
		String  lswf;
		
		for (i=1; i<size; i++)
		{
			lswf = state.getLowerSimplifiedWordForm(i);
			node = state.getNode(i);
			
			m_ambi.add(node.getSimplifiedForm(), node.getPOSTag());
			s_lswf.add(lswf);
		}
		
		if (++n_trees >= db_cutoff)
			addToMap();
	}
	
	private void addToMap()
	{
		m_lswf.addAll(s_lswf);
		s_lswf  = Sets.newHashSet();
		n_trees = 0;
	}
	
	public Set<String> finalizeLowerSimplifiedWordForms()
	{
		if (!s_lswf.isEmpty()) addToMap();
		return m_lswf.keySet(df_cutoff);
	}
	
	public Map<String,String> finalizeAmbiguityClasses(Set<String> lowerSimplifiedWordForms)
	{
		Map<String,String> map = Maps.newHashMap();
		List<ObjectDoublePair<String>> ps;
		
		for (String key : m_ambi.getKeySet1())
		{
			if (!lowerSimplifiedWordForms.contains(StringUtils.toLowerCase(key))) break;
			ps = m_ambi.toList(key, ac_threshold);
			
			if (!ps.isEmpty())
			{
				DSUtils.sortReverseOrder(ps);
				map.put(key, Joiner.join(ps, StringConst.UNDERSCORE));
			}
		}
		
		return map;
	}
}