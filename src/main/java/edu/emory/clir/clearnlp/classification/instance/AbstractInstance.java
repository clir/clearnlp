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


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractInstance<F extends AbstractFeatureVector>
{
	private String s_label;
	private F      f_vector;
	
	public AbstractInstance(String label, F vector)
	{
		set(label, vector);
	}
	
	public String getLabel()
	{
		return s_label;
	}
	
	public F getFeatureVector()
	{
		return f_vector;
	}
	
	public void set(String label, F vector)
	{
		setLabel(label);
		setFeatureVector(vector);
	}
	
	public void setLabel(String label)
	{
		s_label = label;
	}
	
	public void setFeatureVector(F vector)
	{
		f_vector = vector;
	}

	public boolean isLabel(String label)
	{
		return s_label.equals(label);
	}
	
	public String toString()
	{
		return s_label + AbstractFeatureVector.DELIM_FEATURE + f_vector.toString();
	}
}