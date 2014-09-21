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
package edu.emory.clir.clearnlp.lexicon.wordnet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.triple.ObjectIntIntTriple;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WNMap
{
	private WNDataMap n_data;
	private WNDataMap v_data;
	private WNDataMap a_data;
	private WNDataMap r_data;
	
	private WNIndexMap n_index;
	private WNIndexMap v_index;
	private WNIndexMap a_index;
	private WNIndexMap r_index;
	
	public WNMap() {}
	
	public WNMap(String wordnetDirectoryPath)
	{
		String nPath, vPath, aPath, rPath;
		
		try
		{
			nPath = wordnetDirectoryPath+"/data.noun";
			vPath = wordnetDirectoryPath+"/data.verb";
			aPath = wordnetDirectoryPath+"/data.adj";
			rPath = wordnetDirectoryPath+"/data.adv";
			
			initDataMaps(new FileInputStream(nPath), new FileInputStream(vPath), new FileInputStream(aPath), new FileInputStream(rPath));
			
			nPath = wordnetDirectoryPath+"/index.noun";
			vPath = wordnetDirectoryPath+"/index.verb";
			aPath = wordnetDirectoryPath+"/index.adj";
			rPath = wordnetDirectoryPath+"/index.adv";
			
			initIndexMaps(new FileInputStream(nPath), new FileInputStream(vPath), new FileInputStream(aPath), new FileInputStream(rPath));
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
//	------------------------------------ Initializers ------------------------------------
	
	public void initDataMaps(InputStream nIn, InputStream vIn, InputStream aIn, InputStream rIn) throws Exception
	{
		n_data = new WNDataMap(nIn);
		v_data = new WNDataMap(vIn);
		a_data = new WNDataMap(aIn);
		r_data = new WNDataMap(rIn);
		
		n_data.initRelations(this);
		v_data.initRelations(this);
		a_data.initRelations(this);
		r_data.initRelations(this);
	}
	
	public void initIndexMaps(InputStream nIn, InputStream vIn, InputStream aIn, InputStream rIn) throws Exception
	{
		n_index = new WNIndexMap(nIn, n_data);
		v_index = new WNIndexMap(vIn, v_data);
		a_index = new WNIndexMap(aIn, a_data);
		r_index = new WNIndexMap(rIn, r_data);
	}
	
	private WNDataMap getDataMap(char posTag)
	{
		switch (posTag)
		{
		case WNPOSTag.NOUN     : return n_data;
		case WNPOSTag.VERB     : return v_data;
		case WNPOSTag.ADJECTIVE: return a_data;
		case WNPOSTag.ADVERB   : return r_data;
		}
		
		throw new IllegalArgumentException(posTag+" is not a valid POS tag.");
	}
	
	private WNIndexMap getIndexMap(char posTag)
	{
		switch (posTag)
		{
		case WNPOSTag.NOUN     : return n_index;
		case WNPOSTag.VERB     : return v_index;
		case WNPOSTag.ADJECTIVE: return a_index;
		case WNPOSTag.ADVERB   : return r_index;
		}
		
		throw new IllegalArgumentException(posTag+" is not a valid POS tag.");
	}
	
	WNSynset getSynset(char posTag, int offset)
	{
		return getDataMap(posTag).getSynset(offset);
	}
	
	WNIndex getIndex(char posTag, String lemma)
	{
		return getIndexMap(posTag).getIndex(lemma);
	}
	
//	------------------------------------ Relations ------------------------------------

	public boolean isSynonym(char posTag, String lemma1, String lemma2)
	{
		WNIndex index1 = getIndex(posTag, lemma1);
		WNIndex index2 = getIndex(posTag, lemma2);
		
		return (index1 == null || index2 == null) ? false : isSynonym(index1, index2);
	}
	
	public boolean isSynonym(WNIndex index1, WNIndex index2)
	{
		List<WNSynset> ls = index1.getSynsetList();
		List<WNSynset> lb = index2.getSynsetList();
		
		if (lb.size() < ls.size())
		{
			List<WNSynset> t = ls;
			ls = lb;
			lb = t;
		}
		
		Set<WNSynset> set = Sets.newHashSet(lb);
		
		for (WNSynset synset : ls)
		{
			if (set.contains(synset))
				return true;
		}
		
		return false;	
	}
	
	public ObjectIntHashMap<WNSynset> getHypernymMap(char posTag, String lemma)
	{
		WNIndex index = getIndex(posTag, lemma);
		return (index == null) ? new ObjectIntHashMap<WNSynset>() : getHypernymMap(index);
	}
	
	public ObjectIntHashMap<WNSynset> getHypernymMap(WNIndex index)
	{
		ObjectIntHashMap<WNSynset> map = new ObjectIntHashMap<WNSynset>();
		
		for (WNSynset synset : index.getSynsetList())
		{
			map.put(synset, 0);
			getHypernymMapAux(map, synset, 1);
		}
		
		return map;
	}
	
	private void getHypernymMapAux(ObjectIntHashMap<WNSynset> map, WNSynset synset, int height)
	{
		List<WNRelation> list = synset.getHypernymList();
		WNSynset s;
		int h;
		
		for (WNRelation rel : list)
		{
			s = rel.getWNSynset();
			h = map.get(s);
			
			if (h == 0 || height < h)
			{
				map.put(s, height);
				getHypernymMapAux(map, s, height+1);
			}
		}
	}
	
	public ObjectIntIntTriple<WNSynset> getLowestCommonSubsumer(char posTag, String lemma1, String lemma2)
	{
		ObjectIntHashMap<WNSynset> map1 = getHypernymMap(posTag, lemma1);
		if (map1.isEmpty()) return null;
		
		ObjectIntHashMap<WNSynset> map2 = getHypernymMap(posTag, lemma2);
		if (map2.isEmpty()) return null;
		
		return getLowestCommonSubsumer(map1, map2);
	}
	
	/** {@code map1.size()} > {@code map2.size()}. */
	private ObjectIntIntTriple<WNSynset> getLowestCommonSubsumer(ObjectIntHashMap<WNSynset> map1, ObjectIntHashMap<WNSynset> map2)
	{
		ObjectIntIntTriple<WNSynset> lcs = new ObjectIntIntTriple<WNSynset>(null, 0, Integer.MAX_VALUE);
		boolean b = map1.size() < map2.size();
		int h1, h2, hs, ms;
		
		if (b)
		{
			ObjectIntHashMap<WNSynset> t = map1;
			map1 = map2;
			map2 = t;
		}
		
		for (ObjectIntPair<WNSynset> p : map2)
		{
			if (!map1.containsKey(p.o)) continue;
			h1 = map1.get(p.o);
			h2 = p.i;
			hs = h1 + h2;
			ms = lcs.i1 + lcs.i2;
			
			if (hs < ms || (hs == ms && Math.abs(h1-h2) < Math.abs(lcs.i1-lcs.i2)))
				lcs.set(p.o, h1, h2);
		}
		
		if (lcs.o == null)
			return null;
		
		if (b)
		{
			int  t = lcs.i1;
			lcs.i1 = lcs.i2;
			lcs.i2 = t;
		}
		
		return lcs;
	}
	
	public Set<String> getSynonymSet(char posTag, String lemma, int... senseIDs)
	{
		WNIndex index = getIndex(posTag, lemma);
		Set<String> set = Sets.newHashSet();
		
		if (senseIDs.length == 0)
		{
			for (WNSynset synset : index.getSynsetList())
				set.addAll(synset.getWords());	
		}
		else
		{
			for (int senseID : senseIDs)
				set.addAll(index.getSynset(senseID).getWords());
		}
		
		return set;
	}
	
}