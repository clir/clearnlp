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
package com.clearnlp.classification.prediction;

import com.clearnlp.util.MathUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class StringPrediction extends AbstractPrediction implements Comparable<StringPrediction>
{
	private String s_label;
	
	public StringPrediction(String label, double score)
	{
		super(score);
		set(label, score);
	}
	
	public void set(String label, double score)
	{
		setLabel(label);
		setScore(score);
	}
	
	public String getLabel()
	{
		return s_label;
	}
	
	public void setLabel(String label)
	{
		s_label = label;
	}
	
	public boolean isLabel(String label)
	{
		return s_label.equals(label);
	}
	
	public void set(StringPrediction p)
	{
		set(p.s_label, p.d_score);
	}
	
	@Override
	public int compareTo(StringPrediction p)
	{
		return MathUtils.signum(d_score - p.d_score);
	}
}