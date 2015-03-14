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
package edu.emory.clir.clearnlp.component.mode.pos;

import java.io.InputStream;
import java.util.Set;

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSConfiguration extends AbstractConfiguration
{
	private double threshold_ambiguityClass;
	private Set<String> proper_noun_tagset;

//	============================== Initialization ==============================
	
	public POSConfiguration(InputStream in)
	{
		super(in, NLPMode.pos);
		init();
	}
	
	private void init()
	{
		Element eMode = getModeElement();
		
		double ac = XmlUtils.getDoubleTextContent(XmlUtils.getFirstElementByTagName(eMode, "ambiguity_class_threshold"));
		String[] nnp = Splitter.splitCommas(XmlUtils.getTrimmedTextContent(XmlUtils.getFirstElementByTagName(eMode, "proper_noun_tagset")));
		
		setAmbiguityClassThreshold(ac);
		setProperNounTagset(DSUtils.toHashSet(nnp));
	}
	
//	============================== Getters ==============================
	
	public double getAmbiguityClassThreshold()
	{
		return threshold_ambiguityClass;
	}
	
	public Set<String> getProperNounTagset()
	{
		return proper_noun_tagset;
	}
	
//	============================== Setters ==============================
	
	public void setAmbiguityClassThreshold(double threshold)
	{
		threshold_ambiguityClass = threshold;
	}
	
	public void setProperNounTagset(Set<String> set)
	{
		proper_noun_tagset = set;
	}
	
//	============================== Booleans ==============================
	
	public boolean isProperNoun(String posTag)
	{
		return proper_noun_tagset.contains(posTag);
	}
}
