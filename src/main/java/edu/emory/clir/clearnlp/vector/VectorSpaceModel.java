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
package edu.emory.clir.clearnlp.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.MathUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VectorSpaceModel implements Serializable
{
	private static final long serialVersionUID = 4172483442205081702L;
	private List<ObjectIntPair<String>> id_to_term;
	private ObjectIntHashMap<String> term_to_id;
	private Set<String> stop_words;
	private int DOCUMENT_SIZE;

	public VectorSpaceModel()
	{
		term_to_id = new ObjectIntHashMap<>();
		id_to_term = new ArrayList<>();
		stop_words = new HashSet<>();
	}
	
	public void addStopWords(Set<String> stopWords)
	{
		stop_words.addAll(stopWords);
	}
	
	public void addStopWord(String stopWord)
	{
		stop_words.add(stopWord);
	}
	
	public List<Term> toBagOfWords(List<String> document, int ngram, boolean df)
	{
		ObjectIntHashMap<String> map = getBagOfWords(document, stop_words, ngram);
		List<Term> terms = new ArrayList<>(map.size());
		int id;
		
		for (ObjectIntPair<String> t : map)
		{
			id = getID(t.o);
			
			if (id < 0)	// term doesn't exist
			{
				id = term_to_id.size();
				term_to_id.put(t.o, id+1);
				id_to_term.add(new ObjectIntPair<>(t.o, 0));
			}
			
			terms.add(new Term(id, t.i));
			if (df) id_to_term.get(id).i++;
		}
		
		Collections.sort(terms);
		return terms;
	}
	
	/** @param documents each document is represented as a list of strings. */
	public List<List<Term>> toTFIDFs(List<List<String>> documents, int ngram, BiFunction<Term,Integer,Double> f)
	{
		List<List<Term>> list = documents.stream().map(document -> toBagOfWords(document, ngram, true)).collect(Collectors.toCollection(ArrayList::new)); 
		DOCUMENT_SIZE = documents.size();
		
		for (List<Term> terms : list)
		{
			for (Term term : terms)
			{
				term.setDocumentFrequency(getDocumentFrequency(term.getID()));
				term.setScore(f.apply(term, DOCUMENT_SIZE));
			}
		}
		
		return list;
	}
	
	public List<Term> getTFIDFs(List<String> document, int ngram, BiFunction<Term,Integer,Double> f)
	{
		ObjectIntHashMap<String> map = getBagOfWords(document, stop_words, ngram);
		List<Term> list = new ArrayList<>();
		Term term;
		int id;
		
		for (ObjectIntPair<String> t : map)
		{
			id = getID(t.o);
			
			if (id >= 0)
			{
				term = new Term(id, t.i, getDocumentFrequency(id));
				term.setScore(f.apply(term, DOCUMENT_SIZE));
				list.add(term);
			}
		}
		
		return list;
	}
	
	/** @return the term corresponding to the ID if exists; otherwise, null. */
	public String getTerm(int id)
	{
		return DSUtils.isRange(id_to_term, id) ? id_to_term.get(id).o : null; 
	}
	
	/** @return the ID of the term if exists; otherwise, -1. */
	public int getID(String term)
	{
		return term_to_id.get(term) - 1;
	}
	
	public int getTermSize()
	{
		return id_to_term.size();
	}
	
	public int getDocumentFrequency(int id)
	{
		return DSUtils.isRange(id_to_term, id) ? id_to_term.get(id).i : 0; 
	}
	
	public void resetDocumentFrequency()
	{
		for (ObjectIntPair<String> term : id_to_term) term.i = 0;
		DOCUMENT_SIZE = 0;
	}
	
	static public double getTFIDF(double termScore, int documentFrequency, int documentSize)
	{
		return Math.log(MathUtils.divide(documentSize, documentFrequency)) * termScore; 
	}
	
	static public double getTFIDF(Term term, int documentSize)
	{
		return getTFIDF(term.getTermFrequency(), term.getDocumentFrequency(), documentSize); 
	}
	
	static public double getWFIDF(Term term, int documentSize)
	{
		double termScore = (term.getTermFrequency() > 0) ? 1d + Math.log(term.getTermFrequency()) : 0;
		return getTFIDF(termScore, term.getDocumentFrequency(), documentSize);
	}
	
	static public double getEuclideanDistance(List<Term> d1, List<Term> d2)
	{
		int i = 0, j = 0, len1 = d1.size(), len2 = d2.size(); 
		double sum = 0;
		Term t1, t2;

		while (i<len1 && j<len2)
		{
			t1 = d1.get(i);
			t2 = d2.get(j);
			
			if (t1.getID() < t2.getID())
			{
				sum += MathUtils.sq(t1.getScore());
				i++;
			}
			else if (t1.getID() > t2.getID())
			{
				sum += MathUtils.sq(t2.getScore());
				j++;
			}
			else
			{
				sum += MathUtils.sq(t1.getScore() - t2.getScore());
				i++; j++;
			}
		}
		
		for (; i<len1; i++) sum += MathUtils.sq(d1.get(i).getScore());
		for (; j<len2; j++) sum += MathUtils.sq(d2.get(j).getScore());
		
		return Math.sqrt(sum);
	}
	
	static public double getCosineSimilarity(List<Term> d1, List<Term> d2)
	{
		int i = 0, j = 0, len1 = d1.size(), len2 = d2.size(); 
		double num = 0, den1 = 0, den2 = 0;
		Term t1, t2;

		while (i<len1 && j<len2)
		{
			t1 = d1.get(i);
			t2 = d2.get(j);
			den1 += MathUtils.sq(t1.getScore());
			den2 += MathUtils.sq(t2.getScore());
			
			if (t1.getID() < t2.getID())
				i++;
			else if (t1.getID() > t2.getID())
				j++;
			else
			{
				num += t1.getScore() * t2.getScore();
				i++; j++;
			}
		}
		
		for (; i<len1; i++) den1 += MathUtils.sq(d1.get(i).getScore());
		for (; j<len2; j++) den2 += MathUtils.sq(d2.get(j).getScore());
		
		return num / (Math.sqrt(den1) * Math.sqrt(den2));
	}
	
	static public ObjectIntHashMap<String> getBagOfWords(List<String> terms, Set<String> stopWords, int n)
	{
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		int i, j, k, size;

		terms = DSUtils.removeAll(terms, stopWords);
		size  = terms.size();
		
		for (i=0; i<size; i++)
			for (j=0,k=i; j<n && k>=0; j++,k--)
				map.add(Joiner.join(terms, StringConst.UNDERSCORE, k, i+1));

		return map;
	}
	
	static public Set<String> generateStopWords(List<List<String>> documents, int cutoff)
	{
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		Set<String> set;
		
		for (List<String> document : documents)
		{
			set = new HashSet<>(document);
			for (String term : set) map.add(term);
		}
		
		List<ObjectIntPair<String>> list = map.toList();
		Collections.sort(list, Collections.reverseOrder());
		int i, len = list.size();
		set = new HashSet<>();
		
		for (i=0; i<len; i++)
			set.add(list.get(i).o);

		return set;
	}
}
