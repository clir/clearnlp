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
package com.clearnlp.classification.configuration;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class LiblinearTrainerConfiguration extends DefaultTrainerConfiguration
{
	private double d_cost;
	private double d_eps;
	private double d_bias;
	
	public LiblinearTrainerConfiguration(byte vectorType, boolean binary, int labelCutoff, int featureCutoff, int numberOfThreads, double cost, double epsilon, double bias)
	{
		super(vectorType, binary, labelCutoff, featureCutoff, numberOfThreads);
		setCost(cost);
		setEpsilon(epsilon);
		setBias(bias);
	}

	public double getCost()
	{
		return d_cost;
	}
	
	public double getEpsilon()
	{
		return d_eps;
	}
	
	public double getBias()
	{
		return d_bias;
	}

	public void setCost(double cost)
	{
		d_cost = cost;
	}

	public void setEpsilon(double eps)
	{
		d_eps = eps;
	}

	public void setBias(double bias)
	{
		d_bias = bias;
	}
}