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
package edu.emory.clir.clearnlp.component.mode.sequence;

import java.io.InputStream;

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.nlp.configuration.AbstractTrainConfiguration;
import edu.emory.clir.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SeqTrainConfiguration extends AbstractTrainConfiguration
{
	private boolean token_based;

//	============================== Initialization ==============================
	
	public SeqTrainConfiguration()
	{
		super(NLPMode.seq);
	}
	
	public SeqTrainConfiguration(InputStream in)
	{
		super(in, NLPMode.seq);
		init();
	}
	
	private void init()
	{
		Element eMode = getModeElement();
		token_based = XmlUtils.getBooleanTextContent(XmlUtils.getFirstElementByTagName(eMode, "token_based"));
	}
	
//	============================== Getters ==============================
	
	public boolean isTokenBased()
	{
		return token_based;
	}
	
//	============================== Setters ==============================
	
	public void setTokenBased(boolean b)
	{
		token_based = b;
	}
}
