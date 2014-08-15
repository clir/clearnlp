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
package com.clearnlp.component.pos;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class POSConfig
{
	private int n_label;
	private int n_feature;
	private int n_documentFrequency;
	private int n_documentBoundary;
	private double t_ambiguityClass;
	
	public int getLabelCutoff()
	{
		return n_label;
	}
	
	public int getFeatureCutoff()
	{
		return n_feature;
	}
	
	public int getDocumentFrequencyCutoff()
	{
		return n_documentFrequency;
	}
	
	public int getDocumentBoundaryCutoff()
	{
		return n_documentBoundary;
	}
	
	public double getAmbiguityClassThreshold()
	{
		return t_ambiguityClass;
	}
	
	public void setLabelCutoff(int cutoff)
	{
		n_label = cutoff;
	}
	
	public void setFeatureCutoff(int cutoff)
	{
		n_feature = cutoff;
	}
	
	public void setDocumentFrequencyCutoff(int cutoff)
	{
		n_documentFrequency = cutoff;
	}
	
	public void setDocumentBoundaryCutoff(int cutoff)
	{
		n_documentBoundary = cutoff;
	}
	
	public void setAmbiguityClassThreshold(double threshold)
	{
		t_ambiguityClass = threshold;
	}
}
