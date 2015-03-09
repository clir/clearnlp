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
package edu.emory.clir.clearnlp.classification.vector;

import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.collection.list.DoubleArrayList;
import edu.emory.clir.clearnlp.util.constant.PatternConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractFeatureVector
{
	static public final String  DELIM_FEATURE = StringConst.SPACE;
	static public final Pattern SPLIT_FEATURE = PatternConst.SPACE;
	
	protected DoubleArrayList d_weights = null;
	
	public AbstractFeatureVector(boolean hasWeight)
	{
		 init(hasWeight);
	}
	
	private void init(boolean hasWeight)
	{
		if (hasWeight) d_weights = new DoubleArrayList();
	}
	
	/** @return the index'th feature weight. */
	public double getWeight(int index)
	{
		return hasWeight() ? d_weights.get(index) : 1d;
	}
	
	/** @return {@code true} if features are assigned with different weights. */
	public boolean hasWeight()
	{
		return d_weights != null;
	}
	
	abstract public int size();
	abstract public boolean isEmpty();
	abstract public void trimToSize();
}