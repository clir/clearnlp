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

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSConfiguration extends AbstractConfiguration
{
	private double ambiguity_class_threshold;
	private int document_frequency_cutoff;
	private int document_size;

//	============================== Initialization ==============================
	
	public POSConfiguration()
	{
		super(NLPMode.pos);
	}
	
	public POSConfiguration(InputStream in)
	{
		super(in, NLPMode.pos);
		initXml();
	}
	
	private void initXml()
	{
		Element eMode = getModeElement();
		
		double ac = XmlUtils.getDoubleTextContent (XmlUtils.getFirstElementByTagName(eMode, "ambiguity_class_threshold"));
		int    df = XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, "document_frequency_cutoff"));
		int    ds = XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, "document_size"));
		
		setAmbiguityClassThreshold(ac);
		setDocumentFrequencyCutoff(df);
		setDocumentSize(ds);
	}
	
//	============================== Getters ==============================
	
	public double getAmbiguityClassThreshold()
	{
		return ambiguity_class_threshold;
	}
	
	public int getDocumentFrequencyCutoff()
	{
		return document_frequency_cutoff;
	}
	
	public int getDocumentSize()
	{
		return document_size;
	}
	
//	============================== Setters ==============================
	
	public void setAmbiguityClassThreshold(double threshold)
	{
		ambiguity_class_threshold = threshold;
	}
	
	public void setDocumentFrequencyCutoff(int cutoff)
	{
		document_frequency_cutoff = cutoff;
	}
	
	public void setDocumentSize(int size)
	{
		document_size = size;
	}
}
