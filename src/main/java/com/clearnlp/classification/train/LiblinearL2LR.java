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
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class LiblinearL2LR extends AbstractLiblinear
{
	private final int MAX_ITER_NEWTON = 100;
	private final double ETA = 0.1;
	
	/**
	 * @param cost the cost.
	 * @param eps the tolerance of termination criterion.
	 * @param bias the bias.
	 */
	public LiblinearL2LR(SparseModel model, int numThreads, double cost, double eps, double bias)
	{
		super(model, numThreads, cost, eps, bias);
	}
	
	/**
	 * @param cost the cost.
	 * @param eps the tolerance of termination criterion.
	 * @param bias the bias.
	 */
	public LiblinearL2LR(StringModel model, int labelCutoff, int featureCutoff, int numThreads, double cost, double eps, double bias)
	{
		super(model, labelCutoff, featureCutoff, numThreads, cost, eps, bias);
	}
	
	@Override
	public void update(int currLabel)
	{
		final double INNER_MIN = Math.min(1e-8, d_eps);
		final Random rand = new Random(RANDOM_SEED);
		final int N = getInstanceSize();
		
		float[] weight = w_vector.getWeights(currLabel);
		double[] alpha = new double[2*N];
		double G, d, alpha_old, qd, z, gp, gpp, tmpz;

		double alpha_pre = Math.min(0.001 * d_cost, 1e-8);
		double innereps  = 1e-2;
		double Gmax;
		
		int i, s, iter, iter_newton, iter_inner, ind1, ind2, sign;
		SparseFeatureVector xi;
		byte yi;
		
		int []   index = DSUtils.range(N);
		byte[]   aY    = getBinaryLabels(currLabel);
		double[] QD    = getSumOfSquares(0, d_bias);
		
		for (i=0; i<N; i++)
		{
			alpha[2*i  ] = alpha_pre;
			alpha[2*i+1] = d_cost - alpha_pre;

			d  = aY[i] * alpha[2*i];
			xi = getInstance(i).getFeatureVector();
			if (d != 0) update(weight, xi, d_bias, d);
		}
		
		for (iter=0; iter<MAX_ITER; iter++)
		{
			Gmax = iter_newton = 0;
			DSUtils.shuffle(index, rand, N);
			
			for (s=0; s<N; s++)
			{
				i  = index[s];
				yi = aY[i];
				xi = getInstance(i).getFeatureVector();
				G  = getScore(weight, xi, d_bias) * yi;
 				qd = QD[i];
 				
 				ind1 = 2*i;
 				ind2 = 2*i + 1;
 				sign = 1;
 				
 				// decide to minimize g_1(z) or g_2(z)
 				if (0.5 * qd * (alpha[ind2] - alpha[ind1]) + G < 0) 
 				{
 					ind1 = 2*i + 1;
 					ind2 = 2*i;
 					sign = -1;
 				}
 				
 				// g_t(z) = z*log(z) + (C-z)*log(C-z) + 0.5a(z-alpha_old)^2 + sign*G(z-alpha_old)
 				alpha_old = alpha[ind1];
 				z = alpha_old;
 				if (d_cost-z < 0.5*d_cost)	z *= 0.1; 
 					
 				gp = qd * (z-alpha_old) + sign * G + Math.log(z/(d_cost-z));
 				Gmax = Math.max(Gmax, Math.abs(gp));
 				
 				// Newton method on the sub-problem
 				for (iter_inner=0; iter_inner<=MAX_ITER_NEWTON; iter_inner++) 
 				{
 					if (Math.abs(gp) < innereps)
 						break;
 					
 					gpp  = qd + d_cost/(d_cost-z)/z;
 					tmpz = z - gp/gpp;
 					
 					if (tmpz <= 0)	z *= ETA;
 					else 			z = tmpz;
 					
 					gp = qd * (z-alpha_old) + sign * G + Math.log(z/(d_cost-z));
 					iter_newton++;
 				}

 				if (iter_inner > 0)
 				{
 					alpha[ind1] = z;
 					alpha[ind2] = d_cost-z;
 					d = sign * (z-alpha_old) * yi;
 					if (d != 0) update(weight, xi, d_bias, d);
 				}
			}
			
			if (Gmax < d_eps)
				break;
			
			if (iter_newton <= N/10) 
				innereps = Math.max(INNER_MIN, 0.1*innereps);
		}
		
		weight[0] *= d_bias;
		w_vector.setWeights(currLabel, weight);
		
		StringBuilder build = new StringBuilder();
		
		build.append("- label = ");		build.append(currLabel);
		build.append(": iter = ");		build.append(iter);
		build.append("\n");

		BinUtils.LOG.info(build.toString());
	}
}
	