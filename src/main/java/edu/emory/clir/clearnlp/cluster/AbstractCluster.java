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

import java.util.ArrayList;
import java.util.List;

/**
 * @since 3.1.2
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractCluster
{
	protected List<SparseVector> s_points;
	
	public AbstractCluster()
	{
		s_points = new ArrayList<>();
	}
	
	public void addPoint(SparseVector point)
	{
		s_points.add(point);
	}
	
	public void setPoints(List<SparseVector> points)
	{
		s_points = points;
	}
	
	public SparseVector getPoint(int index)
	{
		return s_points.get(index);
	}

	public List<SparseVector> getPoints()
	{
		return s_points;
	}
	
	public abstract Cluster[] cluster();
}
