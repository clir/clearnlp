/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.component.utils;

import java.io.Serializable;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class BeamConfiguration implements Serializable
{
	private static final long serialVersionUID = -2078059683919838245L;
	private int    beam_size;
	private double d_threshold;
	
	public BeamConfiguration(int beamSize, double threshold)
	{
		beam_size   = beamSize;
		d_threshold = threshold;
	}

	public int getBeamSize()
	{
		return beam_size;
	}

	public void setBeamSize(int size)
	{
		beam_size = size;
	}

	public double getThreshold()
	{
		return d_threshold;
	}

	public void setThreshold(double threshold)
	{
		d_threshold = threshold;
	}
}
