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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.PriorityQueue;
import java.util.Random;

import jdk.nashorn.internal.ir.annotations.Ignore;

import org.junit.Test;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DoubleBinaryHeapTest
{
	@Test
	public void test()
	{
		DoubleBinaryHeap heap = new DoubleBinaryHeap();
		
		heap.add(3);
		heap.add(1);
		heap.add(2);
		heap.add(5);
		heap.add(4);
		
		assertEquals(heap.remove(), 5d, 0);
		assertEquals(heap.remove(), 4d, 0);
		assertEquals(heap.remove(), 3d, 0);
		assertEquals(heap.remove(), 2d, 0);
		assertEquals(heap.remove(), 1d, 0);
		
		assertTrue(heap.isEmpty());
	}
	
	@Test
	@Ignore
	public void speed()
	{
		int i, j, len = 20, size = 1000000;
		PriorityQueue<Double> pq;
		DoubleBinaryHeap heap;
		Random rand;
		long st, et;
		
		rand = new Random(1); 
		st = System.currentTimeMillis();
		for (i=0; i<size; i++)
		{
			heap = new DoubleBinaryHeap();
			for (j=0; j<len; j++)
				heap.add(rand.nextDouble());
			for (j=0; j<len; j++)
				heap.remove();
		}
		et = System.currentTimeMillis();
		System.out.println(et-st);
		
		rand = new Random(1); 
		st = System.currentTimeMillis();
		for (i=0; i<size; i++)
		{
			pq = new PriorityQueue<>();
			for (j=0; j<len; j++)
				pq.add(rand.nextDouble());
			for (j=0; j<len; j++)
				pq.remove();
		}
		et = System.currentTimeMillis();
		System.out.println(et-st);
	}
}
