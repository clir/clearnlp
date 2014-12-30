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
package edu.emory.clir.clearnlp.collection.graph;

import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Edge implements Comparable<Edge>
{
	private int    i_source;
	private int    i_target;
	private double d_weight;
	
	public Edge(int source, int target, double weight)
	{
		setSource(source);
		setTarget(target);
		setWeight(weight);
	}
	
	public int getSource()
	{
		return i_source;
	}

	public void setSource(int source)
	{
		i_source = source;
	}

	public int getTarget()
	{
		return i_target;
	}

	public void setTarget(int target)
	{
		i_target = target;
	}

	public double getWeight()
	{
		return d_weight;
	}

	public void setWeight(double weight)
	{
		d_weight = weight;
	}
	
	@Override
	public int compareTo(Edge o)
	{
		return MathUtils.signum(d_weight - o.d_weight);
	}
}
