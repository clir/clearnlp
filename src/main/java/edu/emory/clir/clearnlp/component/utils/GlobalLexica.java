/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.component.utils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.ner.NERInfoSet;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.XmlUtils;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class GlobalLexica
{
	static private List<Map<String,Set<String>>> distributional_semantics_words;
	static private PrefixTree<String,NERInfoSet> named_entity_dictionary;
	
	static public void init(InputStream in)
	{
		Element doc = XmlUtils.getDocumentElement(in);
		Element eLexica = XmlUtils.getFirstElementByTagName(doc, "global");
		if (eLexica == null) return;
		
		initDistributionalSemanticsWords(XmlUtils.getTrimmedTextContents(eLexica, "distributional_semantics"));
		initNamedEntityDictionary(XmlUtils.getTrimmedTextContent(eLexica, "named_entity_dictionary"));
	}
	
	static public void initNamedEntityDictionary(String path)
	{
		if (path != null && !path.isEmpty())
			named_entity_dictionary = NLPUtils.getNERDictionary(path);
	}
	
	static public void initDistributionalSemanticsWords(List<String> paths)
	{
		distributional_semantics_words = paths.stream().map(path -> NLPUtils.getDistributionalSemantics(path)).collect(Collectors.toList());
	}
	
	static public PrefixTree<String,NERInfoSet> getNamedEntityDictionary()
	{
		return named_entity_dictionary;
	}
	
	static public String[] getDistributionalSemanticFeatures(int index, String word)
	{
		if (!DSUtils.isRange(distributional_semantics_words, index)) return null;
		Set<String> set = distributional_semantics_words.get(index).get(word);
		if (set == null) return null;
		String[] t = new String[set.size()];
		set.toArray(t);
		return t;
	}
}
