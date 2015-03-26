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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.ngram.Bigram;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSLexicon implements Serializable
{
	private static final long serialVersionUID = 8363531867786160098L;

	private ObjectIntHashMap<String> document_frequencies;
	private Map<String,String> ambiguity_class_features;
	private Bigram<String,String> ambiguity_classes;
	private Set<String> document; 
	private int tree_count;
	
	private double ambiguity_class_threshold;
	private int document_frequency_cutoff;
	private int document_size;
	
	public POSLexicon(POSConfiguration configuration)
	{	
		document_frequencies = new ObjectIntHashMap<>();
		ambiguity_class_features = new HashMap<>();
		ambiguity_classes = new Bigram<>();
		initDocument();
		
		setAmbiguityClassThreshold(configuration.getAmbiguityClassThreshold());
		setDocumentFrequencyCutoff(configuration.getDocumentFrequencyCutoff());
		setDocumentSize(configuration.getDocumentSize());
	}
	
	public void collect(POSState state)
	{
		String sf;
		
		for (DEPNode node : state.getTree())
		{
			sf = node.getSimplifiedWordForm();
			ambiguity_classes.add(sf, node.getPOSTag());
			document.add(StringUtils.toLowerCase(sf));
		}
		
		if (++tree_count == document_size)
			initDocument();
	}
	
	private void initDocument()
	{
		if (document != null) document_frequencies.addAll(document);
		document = new HashSet<>();
		tree_count = 0;
	}
	
	public String getAmbiguityClassFeature(String simplifiedWordForm)
	{
		return ambiguity_class_features.get(simplifiedWordForm);
	}
	
	public void finalizeCollect()
	{
		finalizeCollect(ambiguity_classes.getBigramSet());
	}
	
	public void finalizeCollect(Set<String> simplifiedWordForms)
	{
		List<ObjectDoublePair<String>> ps;
		initDocument();
		
		for (String key : simplifiedWordForms)
		{
			if (!includeForm(StringUtils.toLowerCase(key))) continue;
			ps = ambiguity_classes.toList(key, ambiguity_class_threshold);
			
			if (!ps.isEmpty())
			{
				DSUtils.sortReverseOrder(ps);
				ambiguity_class_features.put(key, Joiner.joinObject(ps, StringConst.UNDERSCORE));
			}
		}
	}
	
	public boolean includeForm(String lowerSimplifiedWordForm)
	{
		return document_frequencies.get(lowerSimplifiedWordForm) > document_frequency_cutoff;
	}
	
	public void setAmbiguityClassThreshold(double threshold)
	{
		ambiguity_class_threshold = threshold;
	}
	
	public void setDocumentFrequencyCutoff(int cutoff)
	{
		document_frequency_cutoff = cutoff;
	}
	
	public void setDocumentSize(int size)
	{
		document_size = size;
	}
}
