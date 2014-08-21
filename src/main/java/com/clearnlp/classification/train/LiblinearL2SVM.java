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
package com.clearnlp.classification.train;

import java.util.Random;

import com.clearnlp.classification.model.SparseModel;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.vector.SparseFeatureVector;
import com.clearnlp.util.BinUtils;
import com.clearnlp.util.DSUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class LiblinearL2SVM extends AbstractLiblinear
{
	/**
	 * @param cost the cost.
	 * @param eps the tolerance of termination criterion.
	 * @param bias the bias.
	 */
	public LiblinearL2SVM(SparseModel model, int numThreads, double cost, double eps, double bias)
	{
		super(model, numThreads, cost, eps, bias);
	}
	
	/**
	 * @param cost the cost.
	 * @param eps the tolerance of termination criterion.
	 * @param bias the bias.
	 */
	public LiblinearL2SVM(StringModel model, int labelCutoff, int featureCutoff, int numThreads, double cost, double eps, double bias)
	{
		super(model, labelCutoff, featureCutoff, numThreads, cost, eps, bias);
	}
	
	@Override
	public void update(int currLabel)
	{
		final Random rand = new Random(RANDOM_SEED);
		final int N = getInstanceSize();
		
		float[] weight = w_vector.getWeights(currLabel);
		double[] alpha = new double[N];
		double G, d, alpha_old;
		
		// Projected gradient, for shrinking and stopping
		double Gmax_old = Double.POSITIVE_INFINITY;
		double Gmin_old = Double.NEGATIVE_INFINITY;
		double violation, Gmax_new, Gmin_new;
		double upper_bound = d_cost;
		
		int i, s, iter, active_size = N;
		SparseFeatureVector xi;
		byte yi;
		
		int []   index = DSUtils.range(N);
		byte[]   aY    = getBinaryLabels(currLabel);
		double[] QD    = getSumOfSquares(0, d_bias);
		
		for (iter=0; iter<MAX_ITER; iter++)
		{
			Gmax_new = Double.NEGATIVE_INFINITY;
			Gmin_new = Double.POSITIVE_INFINITY;
			DSUtils.shuffle(index, rand, active_size);
			
			for (s=0; s<active_size; s++)
			{
				i  = index[s];
				yi = aY[i];
				xi = getInstance(i).getFeatureVector();
				G  = getScore(weight, xi, d_bias) * yi - 1;
				
				if (alpha[i] == 0)
				{
					if (G > Gmax_old)
					{
						active_size--;
						DSUtils.swap(index, s, active_size);
						s--;
						continue;
					}
					
					violation = Math.min(G, 0);
                }
				else if (alpha[i] == upper_bound)
				{
					if (G < Gmin_old)
					{
						active_size--;
						DSUtils.swap(index, s, active_size);
						s--;
						continue;
					}
					
					violation = Math.max(G, 0);
				}
				else
				{
					violation = G;
				}
				
				Gmax_new = Math.max(Gmax_new, violation);
				Gmin_new = Math.min(Gmin_new, violation);
				
				if (Math.abs(violation) > 1.0e-12)
				{
					alpha_old = alpha[i];
					alpha[i]  = Math.min(Math.max(alpha[i] - G / QD[i], 0d), upper_bound);
					d = (alpha[i] - alpha_old) * yi;
					if (d != 0) update(weight, xi, d_bias, d);
				}
			}
			
			if (Gmax_new - Gmin_new <= d_eps)
			{
				if (active_size == N)
					break;
				else
				{
					active_size = N;
					Gmax_old = Double.POSITIVE_INFINITY;
					Gmin_old = Double.NEGATIVE_INFINITY;
					continue;
				}
			}
			
			Gmax_old = Gmax_new;
			Gmin_old = Gmin_new;
			if (Gmax_old <= 0) Gmax_old = Double.POSITIVE_INFINITY;
			if (Gmin_old >= 0) Gmin_old = Double.NEGATIVE_INFINITY;
		}
		
		weight[0] *= d_bias;
		w_vector.setWeights(currLabel, weight);
		
//		int nSV = 0;
//		for (i=0; i<N; i++) if (alpha[i] > 0) ++nSV;
		
		StringBuilder build = new StringBuilder();
		
		build.append("- label = ");	build.append(currLabel);
		build.append(": iter = ");	build.append(iter);
//		build.append(", nSV = ");	build.append(nSV);
		build.append("\n");
		
		BinUtils.LOG.info(build.toString());
	}
}