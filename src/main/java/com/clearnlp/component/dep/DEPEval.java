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
package com.clearnlp.component.dep;

import com.clearnlp.collection.pair.StringIntPair;
import com.clearnlp.component.evaluation.AbstractEval;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPEval extends AbstractEval<StringIntPair>
{
	private int n_total;
	private int n_las;
	private int n_uas;
	private int n_ls;
	
	public DEPEval()
	{
		clear();
	}
	
	@Override
	public void clear()
	{
		n_total = 0;
		n_las   = 0;
		n_uas   = 0;
		n_ls    = 0;
	}
	
	@Override
	public void countCorrect(DEPTree sTree, StringIntPair[] gHeads)
	{
		StringIntPair[] heads = (StringIntPair[])gHeads;
		int i, size = sTree.size();
		StringIntPair p;
		DEPNode node;
		
		n_total += size - 1;
		
		for (i=1; i<size; i++)
		{
			node = sTree.get(i);
			p = heads[i];
			
			if (node.isDependentOf(sTree.get(p.i)))
			{
				n_uas++;
				if (node.isLabel(p.o)) n_las++;
			}
			
			if (node.isLabel(p.o)) n_ls++;
		}
	}
	
	@Override
	public double[] getScores()
	{
		double[] acc = new double[3];
		
		acc[0] = 100d * n_las / n_total;
		acc[1] = 100d * n_uas / n_total;
		acc[2] = 100d * n_ls  / n_total;
		
		return acc;
	}
	
	@Override
	public String toString()
	{
		double[] d = getScores();
		return String.format("LAS: %5.2f (%d), UAS: %5.2f (%d), LS: %5.2f (%d), TOTAL: %d", d[0], n_las, d[1], n_uas, d[2], n_ls, n_total);
	}
}
