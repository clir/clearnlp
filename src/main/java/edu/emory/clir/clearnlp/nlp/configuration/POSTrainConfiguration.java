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
package edu.emory.clir.clearnlp.nlp.configuration;

import java.io.InputStream;

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTrainConfiguration extends AbstractTrainConfiguration
{
	private int    cutoff_label;
	private int    cutoff_feature;
	private int    cutoff_documentFrequency;
	private int    cutoff_documentBoundary;
	private double threshold_ambiguityClass;

//	============================== Initialization ==============================
	
	public POSTrainConfiguration()
	{
		super(NLPMode.pos);
	}
	
	public POSTrainConfiguration(InputStream in)
	{
		super(in, NLPMode.pos);
		init();
	}
	
	private void init()
	{
		Element eMode = getFirstElement(n_mode.toString());
		Element eCutoff = XmlUtils.getFirstElementByTagName(eMode, E_CUTOFF);
		
		setLabelCutoff  (XmlUtils.getIntegerAttribute(eCutoff, A_LABEL));
		setFeatureCutoff(XmlUtils.getIntegerAttribute(eCutoff, A_FEATURE));
		setDocumentFrequencyCutoff(XmlUtils.getIntegerAttribute(eCutoff, "documentFrequency"));
		setDocumentBoundaryCutoff (XmlUtils.getIntegerAttribute(eCutoff, "documentBoundary"));
		setAmbiguityClassThreshold(XmlUtils.getDoubleAttribute (eCutoff, "ambiguityClass"));
	}
	
//	============================== Getters ==============================
	
	public int getLabelCutoff()
	{
		return cutoff_label;
	}
	
	public int getFeatureCutoff()
	{
		return cutoff_feature;
	}
	
	public int getDocumentFrequencyCutoff()
	{
		return cutoff_documentFrequency;
	}
	
	public int getDocumentBoundaryCutoff()
	{
		return cutoff_documentBoundary;
	}
	
	public double getAmbiguityClassThreshold()
	{
		return threshold_ambiguityClass;
	}
	
//	============================== Setters ==============================
	
	public void setLabelCutoff(int cutoff)
	{
		cutoff_label = cutoff;
	}
	
	public void setFeatureCutoff(int cutoff)
	{
		cutoff_feature = cutoff;
	}
	
	public void setDocumentFrequencyCutoff(int cutoff)
	{
		cutoff_documentFrequency = cutoff;
	}
	
	public void setDocumentBoundaryCutoff(int cutoff)
	{
		cutoff_documentBoundary = cutoff;
	}
	
	public void setAmbiguityClassThreshold(double threshold)
	{
		threshold_ambiguityClass = threshold;
	}
}
