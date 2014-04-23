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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.clearnlp.classification.model.SparseModel;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.vector.BinaryWeightVector;
import com.clearnlp.classification.vector.SparseFeatureVector;
import com.clearnlp.util.BinUtils;
import com.clearnlp.util.MathUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractOneVsAllTrainer extends AbstractTrainer
{
	protected int n_threads;
	
	/** @param numThreads the number of threads. */
	public AbstractOneVsAllTrainer(SparseModel model, int numThreads)
	{
		super(model);
		setNumberOfThreads(numThreads);
	}
	
	/** @param numThreads the number of threads. */
	public AbstractOneVsAllTrainer(StringModel model, int labelCutoff, int featureCutoff, int numThreads)
	{
		super(model, labelCutoff, featureCutoff);
		setNumberOfThreads(numThreads);
	}
	
	public void setNumberOfThreads(int numThreads)
	{
		n_threads = numThreads;
	}

	public void train()
	{	
		if (w_vector.isBinaryLabel())	trainBinary();
		else							trainMulti();
	}
	
	private void trainBinary()
	{
		update(BinaryWeightVector.POSITIVE);
	}
	
	private void trainMulti()
	{
		ExecutorService executor = Executors.newFixedThreadPool(n_threads);
		int currLabel, size = w_vector.getLabelSize();
		
		BinUtils.LOG.info("One vs. All\n");
		
		for (currLabel=0; currLabel<size; currLabel++)
			executor.execute(new TrainTask(currLabel));
		
		executor.shutdown();
		
		try
		{
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
	class TrainTask implements Runnable
	{
		int curr_label;
		
		/** @param currLabel the current label to train. */
		public TrainTask(int currLabel)
		{
			curr_label = currLabel;
		}
		
		public void run()
		{
			update(curr_label);
		}
    }
	
	abstract protected void update(int currLabel);
	
	/** @return an array of 1 or -1. */
	protected byte[] getBinaryLabels(int currLabel)
	{
		int i, size = getInstanceSize();
		byte[] aY = new byte[size];
		
		for (i=0; i<size; i++)
			aY[i] = getInstance(i).isLabel(currLabel) ? (byte)1 : (byte)-1;
			
		return aY;
	}
	
	protected double getScore(float[] weight, SparseFeatureVector x, double bias)
	{
		double score = weight[0] * bias;
		int i, len = x.size();
		
		for (i=0; i<len; i++)
			score += weight[x.getIndex(i)] * x.getWeight(i);
		
		return score;
	}
	
	protected void update(float[] weight, SparseFeatureVector x, double bias, double cost)
	{
		weight[0] += cost * bias;
		int i, len = x.size();
		
		for (i=0; i<len; i++)
			weight[x.getIndex(i)] += cost * x.getWeight(i);
	}

	protected double[] getSumOfSquares(double init, double bias)
	{
		int i, size = getInstanceSize();
		double[] qd = new double[size];
		init += MathUtils.sq(bias);
		SparseFeatureVector x;
		
		for (i=0; i<size; i++)
		{
			x = getInstance(i).getFeatureVector();
			qd[i] = init + x.sumOfSquares();
		}
		
		return qd;
	}
}