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
package edu.emory.clir.clearnlp.collection.heap;

import edu.emory.clir.clearnlp.collection.list.DoubleArrayList;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DoubleBinaryHeap extends DoubleArrayList
{
	private static final long serialVersionUID = -5878140449332980390L;
	private int n_size;
	
	public DoubleBinaryHeap()
	{
		super.add(0);
		n_size = 0;
	}

	public void add(double key)
	{
		super.add(key);
		swim(++n_size);
	}
	
	public double remove()
	{
		swap(1, n_size);
		double max = remove(n_size--);
		sink(1);
		return max;
	}
	
	public int size()
	{
		return n_size;
	}
	
	public boolean isEmpty()
	{
		return n_size == 0;
	}
	
	private void swim(int k)
	{
		while (1 < k && get(k/2) < get(k))
		{
			swap(k/2, k);
			k /= 2;
		}
	}

	private void sink(int k)
	{
		for (int i=k*2; i<=n_size; k=i,i*=2)
		{
			if (i < n_size && get(i) < get(i+1)) i++;
			if (get(k) >= get(i)) break;
			swap(k, i);
		}
	}
}
