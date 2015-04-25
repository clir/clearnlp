/**
 * Copyright (c) 2009/09-2012/08, Regents of the University of Colorado
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Copyright 2012/09-2013/04, 2013/11-Present, University of Massachusetts Amherst
 * Copyright 2013/05-2013/10, IPSoft Inc.
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
package edu.emory.clir.clearnlp.component.mode.dep;

import java.io.Serializable;

import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.util.MathUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPLabel implements Serializable, Comparable<DEPLabel>
{
	private static final long serialVersionUID = -7214636048814903365L;
	private static final String DELIM = StringConst.UNDERSCORE;
	
	private String s_arc;
	private String s_list;
	private String s_deprel;
	private double d_score;
	
	public DEPLabel() {}
	
	public DEPLabel(String arc, String list, String deprel)
	{
		setArc(arc);
		setList(list);
		setDeprel(deprel);
	}
	
	public DEPLabel(String label)
	{
		set(label, 0);
	}
	
	public DEPLabel(StringPrediction p)
	{
		set(p.getLabel(), p.getScore());
	}
	
	public void set(String label, double score)
	{
		int idx = label.indexOf(DELIM);
		setArc   (label.substring(0, idx));
		setList  (label.substring(idx+1, idx = label.lastIndexOf(DELIM)));
		setDeprel(label.substring(idx+1));
		setScore (score);
	}
	
	public String getArc()
	{
		return s_arc;
	}
	
	public String getList()
	{
		return s_list;
	}
	
	public String getDeprel()
	{
		return s_deprel;
	}
	
	public double getScore()
	{
		return d_score;
	}
	
	public void setArc(String arc)
	{
		s_arc = arc;
	}
	
	public void setList(String list)
	{
		s_list = list;
	}
	
	public void setDeprel(String deprel)
	{
		s_deprel = deprel;
	}
	
	public void setScore(double score)
	{
		d_score = score;
	}
	
	public boolean isArc(String label)
	{
		return s_arc.equals(label);
	}
	
	public boolean isArc(DEPLabel label)
	{
		return isArc(label.getArc());
	}

	
	public boolean isList(String label)
	{
		return s_list.equals(label);
	}
	
	public boolean isList(DEPLabel label)
	{
		return isList(label.getList());
	}
	
	public boolean isDeprel(String label)
	{
		return s_deprel.equals(label);
	}
	
	public boolean equalsAll(DEPLabel label)
	{
		return isArc(label.s_arc) && isList(label.s_list) && isDeprel(label.s_deprel);
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		
		build.append(s_arc);		build.append(DELIM);
		build.append(s_list);		build.append(DELIM);
		build.append(s_deprel);
		
		return build.toString();
	}

	@Override
	public int compareTo(DEPLabel o)
	{
		return MathUtils.signum(d_score - o.d_score);
	}
}
