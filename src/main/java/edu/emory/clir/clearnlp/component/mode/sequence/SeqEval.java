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
package edu.emory.clir.clearnlp.component.mode.sequence;

import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SeqEval extends AbstractEval<String>
{
	protected int n_tokenTotal;
	protected int n_tokenCorrect;
	protected int n_treeTotal;
	protected int n_treeCorrect;
	protected boolean token_based;
	
	public SeqEval(boolean tokenBase)
	{
		clear();
		token_based = tokenBase;
	}
	
	@Override
	public void clear()
	{
		n_tokenTotal   = 0;
		n_tokenCorrect = 0;
		n_treeTotal    = 0;
		n_treeCorrect  = 0;
	}
	
	@Override
	public double getScore()
	{
		return token_based ? MathUtils.getAccuracy(n_tokenCorrect, n_tokenTotal) : MathUtils.getAccuracy(n_treeCorrect, n_treeTotal);
	}
	
	public void countCorrect(DEPTree sTree, String[] gTags)
	{
		int i, correct = 0, total = sTree.size() - 1;
		DEPNode node;
		
		for (i=1; i<=total; i++)
		{
			node = sTree.get(i);
			
			if (node.isSequenceLabel(gTags[i]))
				correct++;
		}
		
		n_tokenCorrect += correct;
		n_tokenTotal += total;
		
		if (correct == total) n_treeCorrect++;
		n_treeTotal++;
	}
	
	@Override
	public String toString()
	{
		return String.format("Token: %5.2f (%d/%d), Tree: %5.2f (%d/%d)", MathUtils.getAccuracy(n_tokenCorrect, n_tokenTotal), n_tokenCorrect, n_tokenTotal, MathUtils.getAccuracy(n_treeCorrect, n_treeTotal), n_treeCorrect, n_treeTotal);
	}
}
