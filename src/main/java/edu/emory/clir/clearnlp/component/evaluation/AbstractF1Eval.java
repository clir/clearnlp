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

import edu.emory.clir.clearnlp.util.MathUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractF1Eval<LabelType> extends AbstractEval<LabelType>
{
	protected int p_total;
	protected int r_total;
	protected int n_correct;
	
	public AbstractF1Eval()
	{
		clear();
	}
	
	@Override
	public void clear()
	{
		p_total   = 0;
		r_total   = 0;
		n_correct = 0;
	}
	
	@Override
	public double getScore()
	{
		return getScores()[0];
	}
	
	@Override
	public String toString()
	{
		double[] d = getScores();
		return String.format("F1: %5.2f, P: %5.2f, R: %5.2f", d[0], d[1], d[2]);
	}
	
	private double[] getScores()
	{
		double precision = 100d * n_correct / p_total;
		double recall    = 100d * n_correct / r_total;
		
		return new double[]{MathUtils.getF1(precision, recall), precision, recall};
	}
}
