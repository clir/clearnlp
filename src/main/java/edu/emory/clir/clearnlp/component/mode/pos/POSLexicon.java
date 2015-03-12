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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.ngram.Bigram;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSLexicon implements Serializable
{
	private static final long serialVersionUID = 8363531867786160098L;
	private final double PROPER_NOUN_THRESHOLD = 0.9;
	
	private Map<String,String> ambiguity_class_features;
	private Bigram<String,String> ambiguity_classes;
	private Set<String> proper_noun_tags;
	
	public POSLexicon(Set<String> properNounTags)
	{	
		ambiguity_classes = new Bigram<>();
		proper_noun_tags = properNounTags;
		ambiguity_class_features = null;
	}
	
	public void collect(POSState state)
	{
		for (DEPNode node : state.getTree())
			ambiguity_classes.add(node.getSimplifiedWordForm(), node.getPOSTag());
	}
	
	public Bigram<String,String> getAmbiguityClassMap()
	{
		return ambiguity_classes;
	}

	public String getAmbiguityClassFeature(String simplifiedWordForm)
	{
		return ambiguity_class_features.get(simplifiedWordForm);
	}
	
	public Set<String> getAmbiguityClasses(String simplifiedWordForm)
	{
		return ambiguity_classes.getUnigramSet(simplifiedWordForm);
	}
	
	public void finalizeAmbiguityClassFeatures(double threshold)
	{
		if (ambiguity_class_features != null) return;
		ambiguity_class_features = new HashMap<>();
		List<ObjectDoublePair<String>> ps;
		
		for (String key : ambiguity_classes.getBigramSet())
		{
			if (isProperNoun(key)) continue;
			ps = ambiguity_classes.toList(key, threshold);
			
			if (!ps.isEmpty())
			{
				DSUtils.sortReverseOrder(ps);
				ambiguity_class_features.put(key, Joiner.joinObject(ps, StringConst.UNDERSCORE));
			}
		}
	}
	
	public boolean isProperNoun(String simplifiedWordForm)
	{
		ObjectDoublePair<String> p = ambiguity_classes.getBest(simplifiedWordForm);
		return (p != null) && proper_noun_tags.contains(p.o) && (p.d > PROPER_NOUN_THRESHOLD);
	}
}
