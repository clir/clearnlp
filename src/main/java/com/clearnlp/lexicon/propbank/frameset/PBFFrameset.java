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
package com.clearnlp.lexicon.propbank.frameset;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clearnlp.util.constant.StringConst;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class PBFFrameset implements Serializable
{
	private static final long serialVersionUID = 8504121075121864972L;
	
	private Map<String,PBFPredicate> m_predicates;
	private String s_lemma;

	/** @param lemma the base lemma (e.g., "run", but not "run_out"). */
	public PBFFrameset(Element eFrameset, String lemma)
	{
		init(eFrameset, lemma);
	}
	
	public void init(Element eFrameset, String lemma)
	{
		m_predicates = Maps.newHashMap();
		
		setLemma(lemma);
		initPredicates(eFrameset.getElementsByTagName(PBFXml.E_PREDICATE));
	}
	
	private void initPredicates(NodeList list)
	{
		int i, size = list.getLength();
		
		for (i=0; i<size; i++)
			initPredicate((Element)list.item(i));
	}
	
	private void initPredicate(Element element)
	{
		addPredicate(new PBFPredicate(element));
	}
	
	/** @param lemma the specific lemma of the predicate (e.g., "run_out"). */
	public PBFPredicate getPredicate(String lemma)
	{
		return m_predicates.get(lemma);
	}
	
	public PBFRoleset getRoleset(String rolesetID)
	{
		PBFRoleset roleset;
		
		for (PBFPredicate predicate : m_predicates.values())
		{
			roleset = predicate.getRoleset(rolesetID);
			
			if (roleset != null)
				return roleset;
		}
		
		return null;
	}
	
	public Collection<PBFPredicate> getPredicates()
	{
		return m_predicates.values();
	}
	
	/** @return the base lemma (e.g., "run", but not "run_out"). */
	public String getLemma()
	{
		return s_lemma;
	}
	
	/** @param lemma the base lemma (e.g., "run", but not "run_out"). */
	public void setLemma(String lemma)
	{
		s_lemma = lemma;
	}
	
	public List<PBFRoleset> getRolesetListFromVerbNet(String vncls, boolean polysemousOnly)
	{
		List<PBFRoleset> list = Lists.newArrayList();
		
		for (PBFPredicate predicate : m_predicates.values())
			list.addAll(predicate.getRolesetListFromVerbNet(vncls, polysemousOnly));
		
		return list;
	}
	
	public void addPredicate(PBFPredicate predicate)
	{
		if (m_predicates.put(predicate.getLemma(), predicate) != null)
			System.err.printf("Duplicated predicate: %s\n", predicate.getLemma());
	}
	
	public String toString()
	{
		List<PBFPredicate> list = Lists.newArrayList(getPredicates());
		StringBuilder build = new StringBuilder();
		Collections.sort(list);
		
		for (PBFPredicate predicate : list)
		{
			build.append(StringConst.NEW_LINE);
			build.append(predicate.toString());
		}
		
		return build.toString().trim();
	}
}