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


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Term implements Comparable<Term>, Serializable
{
	private static final long serialVersionUID = -4073277115519852814L;
	private int id;
	private int term_frequency;
	private int document_frequency;
	private double score;
	
	public Term() {}
	
	public Term(int id, int termFrequency)
	{
		setID(id);
		setTermFrequency(termFrequency);
		setDocumentFrequency(0);
		setScore(1);
	}
	
	public Term(int id, int termFrequency, int documentFrequency)
	{
		setID(id);
		setTermFrequency(termFrequency);
		setDocumentFrequency(documentFrequency);
	}
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getTermFrequency()
	{
		return term_frequency;
	}
	
	public void setTermFrequency(int frequency)
	{
		term_frequency = frequency;
	}
	
	public int getDocumentFrequency()
	{
		return document_frequency;
	}
	
	public void setDocumentFrequency(int frequency)
	{
		this.document_frequency = frequency;
	}
	
	public double getScore()
	{
		return score;
	}
	
	public void setScore(double score)
	{
		this.score = score; 
	}
	
	@Override
	public int compareTo(Term o)
	{
		return id - o.id;
	}
}
