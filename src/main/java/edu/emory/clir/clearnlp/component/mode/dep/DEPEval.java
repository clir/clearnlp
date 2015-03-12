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
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.MathUtils;
import edu.emory.clir.clearnlp.util.arc.DEPArc;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPEval extends AbstractEval<DEPArc>
{
	private boolean eval_punct;
	private int n_total;
	private int n_las;
	private int n_uas;
	
	public DEPEval()
	{
		eval_punct = true;
		clear();
	}
	
	public DEPEval(boolean includePunct)
	{
		eval_punct = includePunct;
		clear();
	}
	
	@Override
	public void clear()
	{
		n_total = 0;
		n_las   = 0;
		n_uas   = 0;
	}
	
	@Override
	public void countCorrect(DEPTree sTree, DEPArc[] gHeads)
	{
		int[] counts = sTree.getScoreCounts(gHeads, eval_punct);
		
		n_total += sTree.size() - 1;
		n_las   += counts[0];
		n_uas   += counts[1];
	}
	
	public double getScore()
	{
		return MathUtils.getAccuracy(n_las, n_total);
	}
	
	@Override
	public String toString()
	{
		return String.format("LAS: %5.2f (%d), UAS: %5.2f (%d), TOTAL: %d", MathUtils.getAccuracy(n_las, n_total), n_las, MathUtils.getAccuracy(n_uas, n_total), n_uas, n_total);
	}
}
