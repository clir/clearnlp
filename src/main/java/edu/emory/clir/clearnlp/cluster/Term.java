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

/**
 * @since 3.1.2
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Term implements Comparable<Term>
{
	private int   id;
	private float score;
	
	public Term(int i1, int i2)
	{
		set(i1, i2);
	}
	
	public void set(int id, float score)
	{
		setID(id);
		setScore(score);
	}
	
	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public float getScore()
	{
		return score;
	}

	public void setScore(float score)
	{
		this.score = score;
	}
	
	public void addScore(float score)
	{
		this.score += score;
	}

	@Override
	public int compareTo(Term o)
	{
		return id - o.id;
	}
}