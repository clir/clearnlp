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
package com.clearnlp.component.evaluation;

import com.clearnlp.util.MathUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractF1Eval extends AbstractEval
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
	public double[] getAccuracies()
	{
		double precision = 100d * n_correct / p_total;
		double recall    = 100d * n_correct / r_total;
		
		return new double[]{MathUtils.getF1(precision, recall), precision, recall};
	}
	
	@Override
	public String toString()
	{
		double[] d = getAccuracies();
		return String.format("F1: %5.2f, P: %5.2f, R: %5.2f\n", d[0], d[1], d[2]);
	}
}
