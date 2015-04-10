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
package edu.emory.clir.clearnlp.component.mode.ner;

import java.io.Serializable;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.ngram.Bigram;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.ner.NERInfoList;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERLexicon implements Serializable
{
	private static final long serialVersionUID = 3816259878124239839L;
	private PrefixTree<String,NERInfoList> ne_dictionary;
	private Bigram<String,String> dict_counts;
	private int dictionary_cutoff;
	
	public NERLexicon(NERConfiguration configuration)
	{
		setDictionaryCutoff(configuration.getDictionaryCutoff());
		String dictPath = configuration.getDictionaryPath();
		dict_counts = new Bigram<>();
		
		if (dictPath != null)	setDictionary(NLPUtils.getNEDictionary(configuration.getLanguage(), dictPath));
		else					setDictionary(new PrefixTree<>());
	}
	
	public void collect(DEPTree tree)
	{
		DEPNode[] nodes = tree.toNodeArray();
		IntObjectHashMap<String> map = NEREval.collectNamedEntityMap(nodes, DEPNode::getNamedEntityTag);
		int bIdx, eIdx, size = tree.size();
		
		for (ObjectIntPair<String> p : map)
		{
			bIdx = p.i / size;
			eIdx = p.i % size;
			dict_counts.add(p.o, Joiner.join(nodes, StringConst.SPACE, bIdx, eIdx+1));
		}
	}
	
	public void populateDictionary()
	{
		NERInfoList list;
		String[]   array;
		
		for (String type : dict_counts.getBigramSet())
		{
			for (ObjectIntPair<String> p : dict_counts.toList(type, dictionary_cutoff))
			{
				array = Splitter.splitSpace(p.o);
				list  = NERState.pick(ne_dictionary, type, array, 0, array.length, String::toString, p.i);
				list.addCorrectCount(p.i);
			}
		}
		
		dict_counts = null;
	}
	
	public PrefixTree<String,NERInfoList> getDictionary()
	{
		return ne_dictionary;
	}
	
	public void setDictionary(PrefixTree<String,NERInfoList> dictionary)
	{
		ne_dictionary = dictionary;
	}
	
	public int getDictionaryCutoff()
	{
		return dictionary_cutoff;
	}
	
	public void setDictionaryCutoff(int cutoff)
	{
		dictionary_cutoff = cutoff;
	}
}
