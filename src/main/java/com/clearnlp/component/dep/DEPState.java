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
import com.clearnlp.collection.set.IntHashSet;
import com.clearnlp.util.MathUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPState implements Comparable<DEPState>
{
	private int             i_lambda;
	private int             i_beta;
	private int             n_trans;
	private double          d_score;
	private IntHashSet      s_reduce;
	private DEPLabel        d_label;
	private StringIntPair[] p_heads;
	
	public DEPState()
	{
		set(0, 1, 0, 0, new IntHashSet(), null, null);
	}
	
	public DEPState(int lambda, int beta, int trans, double score, IntHashSet reduce, DEPLabel label, StringIntPair[] heads)
	{
		set(lambda, beta, trans, score, reduce, label, heads);
	}
	
	public void set(int lambda, int beta, int trans, double score, IntHashSet reduce, DEPLabel label, StringIntPair[] heads)
	{
		i_lambda = lambda;
		i_beta   = beta;
		n_trans  = trans;
		d_score  = score;
		s_reduce = reduce;
		d_label  = label;
		p_heads  = heads;
	}
	
	public DEPState clone(DEPLabel label, StringIntPair[] heads)
	{
		return new DEPState(i_lambda, i_beta, n_trans, d_score, s_reduce.clone(), label, heads);
	}
	
	public int getLambda()
	{
		return i_lambda;
	}
	
	public int getBeta()
	{
		return i_beta;
	}
	
	public int getNumberOfTransitions()
	{
		return n_trans;
	}
	
	public double getScore()
	{
		return d_score;
	}
	
	public boolean skip(int nodeID)
	{
		return s_reduce.contains(nodeID);
	}
	
	public DEPLabel getLabel()
	{
		return d_label;
	}
	
	public StringIntPair[] getHeads()
	{
		return p_heads;
	}
	
	@Override
	public int compareTo(DEPState s)
	{
		return MathUtils.signum(d_score - s.d_score);
	}
}
