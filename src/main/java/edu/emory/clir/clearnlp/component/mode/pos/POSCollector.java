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
package edu.emory.clir.clearnlp.component.mode.pos;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.emory.clir.clearnlp.collection.map.IncMap2;
import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.component.ICollector;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.nlp.configuration.POSTrainConfiguration;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSCollector implements ICollector<POSState>, Serializable
{
	private static final long serialVersionUID = -1309316221225281613L;
	
	private IncMap2<String,String>		m_ambi;
	private ObjectIntHashMap<String>    m_lswf;
	private Set<String>					s_lswf;
	private int							n_trees;
	
	private double ac_threshold;	// ambiguity classes
	private int    db_cutoff;		// document boundary
	private int    df_cutoff;		// document frequency
	
	public POSCollector(POSTrainConfiguration config)
	{	
		m_ambi  = new IncMap2<>();
		m_lswf  = new ObjectIntHashMap<>();
		s_lswf  = Sets.newHashSet();
		n_trees = 0;
		
		setAmbiguityClassThreshold(config.getAmbiguityClassThreshold());
		setDocumentBoundaryCutoff (config.getDocumentBoundaryCutoff());
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
		
		for (i=1; i<size; i++)
		{
			node = state.getNode(i);
			
			m_ambi.add(node.getSimplifiedWordForm(), node.getPOSTag());
			s_lswf.add(node.getLowerSimplifiedWordForm());
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
			if (!lowerSimplifiedWordForms.contains(StringUtils.toLowerCase(key))) continue;
			ps = m_ambi.toList(key, ac_threshold);
			
			if (!ps.isEmpty())
			{
				DSUtils.sortReverseOrder(ps);
				map.put(key, join(ps, StringConst.UNDERSCORE));
			}
		}
		
		return map;
	}
	
	private String join(List<ObjectDoublePair<String>> ps, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (ObjectDoublePair<String> p : ps)
		{
			build.append(delim);
			build.append(p.o);
		}
		
		return build.substring(delim.length());
	}
}


