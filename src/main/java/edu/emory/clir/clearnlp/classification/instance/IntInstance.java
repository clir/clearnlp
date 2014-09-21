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
package edu.emory.clir.clearnlp.classification.instance;

import edu.emory.clir.clearnlp.classification.vector.AbstractFeatureVector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IntInstance
{
	private int i_label;
	private SparseFeatureVector f_vector;
	
	public IntInstance(int label, SparseFeatureVector vector)
	{
		set(label, vector);
	}
	
	public int getLabel()
	{
		return i_label;
	}
	
	public SparseFeatureVector getFeatureVector()
	{
		return f_vector;
	}
	
	public void set(int label, SparseFeatureVector vector)
	{
		setLabel(label);
		setFeatureVector(vector);
	}
	
	public void setLabel(int label)
	{
		i_label = label;
	}
	
	public void setFeatureVector(SparseFeatureVector vector)
	{
		f_vector = vector;
	}
	
	public boolean isLabel(int label)
	{
		return i_label == label;
	}
	
	public String toString()
	{
		return i_label + AbstractFeatureVector.DELIM_FEATURE + f_vector.toString();
	}
}