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
package com.clearnlp.nlp.configure;

import java.io.InputStream;

import org.w3c.dom.Element;

import com.clearnlp.component.pos.POSConfig;
import com.clearnlp.nlp.NLPMode;
import com.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSConfiguration extends AbstractConfiguration
{
	private POSConfig pos_config;
	
	public POSConfiguration(InputStream in)
	{
		super(in);
		init();
	}
	
	private void init()
	{
		Element eTop = XmlUtils.getFirstElementByTagName(x_top, NLPMode.pos.toString());
		pos_config = getPOSConfig(eTop);
	}

	private POSConfig getPOSConfig(Element eTop)
	{
		Element eCutoff = XmlUtils.getFirstElementByTagName(eTop, E_CUTOFF);
		POSConfig config = new POSConfig();
		
		config.setLabelCutoff  (XmlUtils.getIntegerAttribute(eCutoff, A_LABEL));
		config.setFeatureCutoff(XmlUtils.getIntegerAttribute(eCutoff, A_FEATURE));
		config.setDocumentFrequencyCutoff(XmlUtils.getIntegerAttribute(eCutoff, "documentFrequency"));
		config.setDocumentBoundaryCutoff (XmlUtils.getIntegerAttribute(eCutoff, "documentBoundary"));
		config.setAmbiguityClassThreshold(XmlUtils.getDoubleAttribute (eCutoff, "ambiguityClass"));
		
		return config;
	}
	
	public POSConfig getPOSConfig()
	{
		return pos_config;
	}
}
