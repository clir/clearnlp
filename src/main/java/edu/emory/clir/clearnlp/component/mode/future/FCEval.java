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
package edu.emory.clir.clearnlp.component.mode.future;

import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FCEval extends AbstractEval<String>
{
	static public int INFO_NODE = 1;
	
	private String feat_key;
	private int n_correct;
	private int n_total;
	
	public FCEval(String featKey)
	{
		feat_key = featKey;
	}
	
	@Override
	public void countCorrect(DEPTree sTree, String[] gLabels)
	{
		if (gLabels[0].equals(sTree.get(INFO_NODE).getFeat(feat_key))) n_correct++;
		n_total++;
	}

	@Override
	public double getScore()
	{
		return MathUtils.getAccuracy(n_correct, n_total);
	}

	@Override
	public void clear()
	{
		n_correct = 0;
		n_total   = 0;
	}
	
	@Override
	public String toString()
	{
		return String.format("%5.2f (%d/%d)", MathUtils.getAccuracy(n_correct, n_total), n_correct, n_total);
	}
}
