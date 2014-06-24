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
package com.clearnlp.lexicon.wordnet;

import java.util.ArrayList;
import java.util.List;

import com.clearnlp.util.Joiner;
import com.clearnlp.util.Splitter;
import com.clearnlp.util.constant.StringConst;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class WNSynset
{
	private String			s_lexicographerFileNumber;
	private int				n_synsetOffset;
	private char			c_posTag; 
	private WNPointer[]		a_pointers;
	private List<String>	s_words;
	private String			s_gloss;
	
	private List<WNRelation> l_antonym;
	private List<WNRelation> l_hypernym;
	private List<WNRelation> l_hyponym;
	
//	private List<WNRelation> l_attribute;
//	private List<WNRelation> l_derivationallyRelatedForm;
//	private List<WNRelation> l_entailment;
//	private List<WNRelation> l_cause;
//	private List<WNRelation> l_alsoSee;
//	private List<WNRelation> l_verbGroup;
//	private List<WNRelation> l_similarTo;
//	private List<WNRelation> l_participleOfVerb;
//	private List<WNRelation> l_pertainym;
//	private List<WNRelation> l_instanceHypernym;
//	private List<WNRelation> l_instanceHyponym;
//	private List<WNRelation> l_memberHolonym;
//	private List<WNRelation> l_substanceHolonym;
//	private List<WNRelation> l_partHolonym;
//	private List<WNRelation> l_memberMeronym;
//	private List<WNRelation> l_substanceMeronym;
//	private List<WNRelation> l_partMeronym;
//	private List<WNRelation> l_domainTopic;
//	private List<WNRelation> l_domainRegion;
//	private List<WNRelation> l_domainUsage;
//	private List<WNRelation> l_memberTopic;
//	private List<WNRelation> l_memberRegion;
//	private List<WNRelation> l_memberUsage;

	public WNSynset()
	{
		s_words = Lists.newArrayList();
	}
	
	/**
	 * @param line a line from the WordNet data file (e.g., data.verb).
	 * e.g., "02166460 39 v 02 study 0 consider 0 009 @ 00630380 v 0000 + 07138915 n 0201 + 05822746 n 0203 + 05784831 n 0201 + 05784242 n 0102 + 00644503 n 0102 ~ 00640650 v 0000 ~ 00653620 v 0000 $ 00813044 v 0000 03 + 08 00 + 09 00 + 29 00 | give careful consideration to; "consider the possibility of moving""
	 */
	public WNSynset(String line)
	{
		int i, count, idx = 0;
		WNPointer pointer;
		String[] t;
		
		i = line.indexOf(StringConst.PIPE);
		
		if (i > 0)
		{
			setGloss(line.substring(i+1).trim());
			line = line.substring(0, i).trim();
		}
		else
		{
			setGloss(StringConst.EMPTY);
			line = line.trim();
		}
		
		t = Splitter.splitSpace(line);
		
		setSynsetOffset(Integer.parseInt(t[idx++]));
		setLexicographerFileNumber(t[idx++]);
		setPOSTag(t[idx++].charAt(0));
		
		count = Integer.parseInt(t[idx++], 16);
		s_words = new ArrayList<String>(count);
		
		for (i=0; i<count; i++)
		{
			addWord(t[idx++]);
			idx++;	// skip lexical ID
		}

		count = Integer.parseInt(t[idx++]);
		a_pointers = new WNPointer[count];
		
		for (i=0; i<count; i++)
		{
			pointer = new WNPointer();
			pointer.setPointerSymbol(t[idx++]);
			pointer.setSynsetOffset(Integer.parseInt(t[idx++]));
			pointer.setPOSTag(t[idx++].charAt(0));
			pointer.setSource((short)(Short.parseShort(t[idx].substring(0,2), 16) - 1));
			pointer.setTarget((short)(Short.parseShort(t[idx++].substring(2), 16) - 1));
			a_pointers[i] = pointer;
		}
		
		// ignores [+ f_num w_num]*
	}
	
//	------------------------------------ Getters/Setters ------------------------------------
	
	public String getLexicographerFileNumber()
	{
		return s_lexicographerFileNumber;
	}
	
	public int getSynsetOffset()
	{
		return n_synsetOffset;
	}
	
	public char getPOSTag()
	{
		return c_posTag;
	}
	
	public List<String> getWords()
	{
		return s_words;
	}
	
	public String getGloss()
	{
		return s_gloss;
	}
	
	public void setLexicographerFileNumber(String number)
	{
		s_lexicographerFileNumber = number;
	}
	
	public void setSynsetOffset(int offset)
	{
		n_synsetOffset = offset;
	}
	
	public void setPOSTag(char tag)
	{
		c_posTag = tag;
	}
	
	public void addWord(String word)
	{
		s_words.add(word);
	}
	
	public void setGloss(String gloss)
	{
		s_gloss = gloss;
	}
	
//	------------------------------------ Initializers ------------------------------------
	
	void initRelations(WNMap map)
	{
		l_antonym  = Lists.newArrayList();
		l_hypernym = Lists.newArrayList();
		l_hyponym  = Lists.newArrayList();
		
		for (WNPointer pointer : a_pointers)
			initRelation(map, pointer);
		
		a_pointers = null;
	}
	
	/** Called by {@link #initRelations(WNDataMap)}. */
	private void initRelation(WNMap map, WNPointer pointer)
	{
		switch (pointer.getPointerSymbol())
		{
		case "!": initRelationAux(map, pointer, l_antonym); break;
		case "@": initRelationAux(map, pointer, l_hypernym); break;
		case "~": initRelationAux(map, pointer, l_hyponym); break;
		}
	}
	
	/** Called by {@link #initRelation(WNDataMap, WNPointer)}. */
	private void initRelationAux(WNMap map, WNPointer pointer, List<WNRelation> list)
	{
		list.add(getRelation(map, pointer));
	}
	
	private WNRelation getRelation(WNMap map, WNPointer pointer)
	{
		WNSynset synset = map.getSynset(pointer.getPOSTag(), pointer.getSynsetOffset());
		WNRelation relation = new WNRelation();
		
		relation.setWNSynset(synset);
		relation.setSource(pointer.getSource());
		relation.setTarget(pointer.getTarget());
		
		return relation;
	}
	
//	------------------------------------ Initializers ------------------------------------

	public List<WNRelation> getAntonymList()
	{
		return l_antonym;
	}
	
	public List<WNRelation> getHypernymList()
	{
		return l_hypernym;
	}
	
	public List<WNRelation> getHyponymList()
	{
		return l_hyponym;
	}
	
	public String toString()
	{
		return c_posTag + ": " + Joiner.join(s_words, StringConst.SPACE);
	}
}