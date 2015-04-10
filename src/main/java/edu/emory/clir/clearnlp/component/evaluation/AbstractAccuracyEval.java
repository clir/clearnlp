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
package edu.emory.clir.clearnlp.component.evaluation;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.MathUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractAccuracyEval<LabelType> extends AbstractEval<LabelType>
{
	protected int n_totalTokens;
	protected int n_totalTrees;
	protected int n_correctTokens;
	protected int n_correctTrees;
	protected boolean token_based;
	
	public AbstractAccuracyEval()
	{
		this(true);
	}
	
	public AbstractAccuracyEval(boolean tokenBased)
	{
		setTokenBased(tokenBased); 
		clear();
	}
	
	public boolean isTokenBased()
	{
		return token_based;
	}
	
	public void setTokenBased(boolean b)
	{
		token_based = b;
	}
	
	public void countCorrect(DEPTree sTree, LabelType[] gLabels)
	{
		int i, total =  sTree.size() - 1, correct = 0;
		
		for (i=1; i<=total; i++)
		{
			if (isCorrect(sTree.get(i), gLabels[i]))
				correct++;
		}
		
		n_correctTokens += correct;
		n_totalTokens += total;
		
		if (correct == total) n_correctTrees++;
		n_totalTrees++;
	}
	
	abstract protected boolean isCorrect(DEPNode node, LabelType label);
	
	@Override
	public void clear()
	{
		n_totalTokens   = 0;
		n_totalTrees    = 0;
		n_correctTokens = 0;
		n_correctTrees  = 0;
	}
	
	@Override
	public double getScore()
	{
		return token_based ? MathUtils.getAccuracy(n_correctTokens, n_totalTokens) : MathUtils.getAccuracy(n_correctTrees, n_totalTrees);
	}
	
	@Override
	public String toString()
	{
		return String.format("Token: %5.2f (%d/%d), Tree: %5.2f (%d/%d)", MathUtils.getAccuracy(n_correctTokens, n_totalTokens), n_correctTokens, n_totalTokens, MathUtils.getAccuracy(n_correctTrees, n_totalTrees), n_correctTrees, n_totalTrees);
	}
}
