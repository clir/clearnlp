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
package com.clearnlp.util.random;

import java.util.Random;

import com.clearnlp.collection.set.IntHashSet;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class IntUniqueRandom extends Random
{
	private static final long serialVersionUID = -1187656136103151798L;
	private IntHashSet i_set;
	private int        n_size;
	
	public IntUniqueRandom(int size)
	{
		super();
		i_set  = new IntHashSet(size);
		n_size = size;
	}

	public IntUniqueRandom(long seed, int size)
	{
		super(seed);
		i_set  = new IntHashSet(size);
		n_size = size;
	}
	
	/** @return an unique random number given the size if exists; otherwise, {@code -1}. */
	public int next()
	{
		if (i_set.size() == n_size) return -1;
		int next = -1;
		
		while (true)
		{
			next = nextInt(n_size);
			
			if (!i_set.contains(next))
			{
				i_set.add(next);
				break;
			}
		}
		
		return next;
	}
}