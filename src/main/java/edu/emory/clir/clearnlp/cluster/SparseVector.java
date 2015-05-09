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


/**
 * @since 3.1.2
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseVector implements Comparable<SparseVector>
{
	private IntObjectHashMap<Term> term_map;
	private int id;
	
	public SparseVector(int id)
	{
		term_map = new IntObjectHashMap<>();
		setID(id);
	}
	
	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
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
	
//	public double norm()
//	{
//		double d = 0;
//		
//		for (int i=term_list.size()-1; i>=0; i--)
//			d += MathUtils.sq(term_list.get(i).getScore());
//		
//		return Math.sqrt(d);
//	}
	
	@Override
	public int compareTo(SparseVector o)
	{
		return id - o.id;
	}
}