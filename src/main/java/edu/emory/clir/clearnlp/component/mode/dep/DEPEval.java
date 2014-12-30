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
package edu.emory.clir.clearnlp.component.mode.dep;

import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.arc.DEPArc;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPEval extends AbstractEval<DEPArc>
{
	private boolean b_includePunct;
	private int n_total;
	private int n_las;
	private int n_uas;
	private int n_ls;
	
	public DEPEval(boolean includePunct)
	{
		b_includePunct = includePunct;
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
	public void countCorrect(DEPTree sTree, DEPArc[] gHeads)
	{
		int i, size = sTree.size();
		DEPNode node;
		DEPArc g;
		
		for (i=1; i<size; i++)
		{
			node = sTree.get(i);
			
			if (!b_includePunct && StringUtils.containsPunctuationOnly(node.getSimplifiedWordForm()))
				continue;
			
			g = gHeads[i];
			n_total++;
			
			if (node.isDependentOf(sTree.get(g.getNode().getID())))
			{
				n_uas++;
				if (node.isLabel(g.getLabel())) n_las++;
			}
			
			if (node.isLabel(g.getLabel())) n_ls++;
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
	
	public double getScore()
	{
		return 100d * n_las / n_total;
	}
	
	@Override
	public String toString()
	{
		double[] d = getScores();
		return String.format("LAS: %5.2f (%d), UAS: %5.2f (%d), LS: %5.2f (%d), TOTAL: %d", d[0], n_las, d[1], n_uas, d[2], n_ls, n_total);
	}
}
