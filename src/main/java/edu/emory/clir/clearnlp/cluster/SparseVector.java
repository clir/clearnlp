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
package edu.emory.clir.clearnlp.cluster;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.MathUtils;


/**
 * @since 3.1.2
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseVector
{
	private IntObjectHashMap<Term> term_map;
	private int document_id;
	
	public SparseVector(int id)
	{
		term_map = new IntObjectHashMap<>();
		setDocumentID(id);
	}
	
	public int getDocumentID()
	{
		return document_id;
	}

	public void setDocumentID(int id)
	{
		document_id = id;
	}
	
	public Term get(int id)
	{
		return term_map.get(id); 
	}
	
	public int size()
	{
		return term_map.size();
	}
	
	public IntObjectHashMap<Term> getTermMap()
	{
		return term_map;
	}
	
	public void add(SparseVector vector)
	{
		for (ObjectIntPair<Term> p : vector.getTermMap())
			add(p.o);
	}
	
	public void add(Term term)
	{
		Term t = term_map.get(term.getID());
		
		if (t == null)	term_map.put(term.getID(), term);
		else			t.addScore(term.getScore());
	}
	
	public double dotProduct(SparseVector vector)
	{
		double sum = 0;
		Term t;
		
		for (ObjectIntPair<Term> p : vector.getTermMap())
		{
			t = get(p.i);
			if (t != null) sum += p.o.getScore() * t.getScore();
		}
		
		return sum;
	}
	
	public void divide(int denominator)
	{
		for (ObjectIntPair<Term> p : term_map)
			p.o.setScore(p.o.getScore()/denominator);
	}
	
	public double euclideanNorm()
	{
		double d = 0;
		
		for (ObjectIntPair<Term> p : term_map)
			d += MathUtils.sq(p.o.getScore());
		
		return Math.sqrt(d);
	}
}