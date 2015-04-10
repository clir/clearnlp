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
package edu.emory.clir.clearnlp.component.mode.ner;

import java.io.InputStream;

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.util.XmlUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERConfiguration extends AbstractConfiguration
{
	private String dictionary_path;
	private int dictionary_cutoff;
	
//	============================== Initialization ==============================
	
	public NERConfiguration()
	{
		super(NLPMode.ner);
	}
	
	public NERConfiguration(InputStream in)
	{
		super(in, NLPMode.ner);
		initXml();
	}
	
	private void initXml()
	{
		Element eMode = getModeElement();
		
		String dictPath = XmlUtils.getTrimmedTextContent(XmlUtils.getFirstElementByTagName(eMode, "dictionary_path"));
		if (dictPath.equals(StringConst.EMPTY)) dictPath = null;
		setDictionaryPath(dictPath);
		
		int dictCutoff = XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, "dictionary_cutoff"));
		setDictionaryCutoff(dictCutoff);
	}
	
	public String getDictionaryPath()
	{
		return dictionary_path;
	}
	
	public void setDictionaryPath(String path)
	{
		dictionary_path = path;
	}
	
	public int getDictionaryCutoff()
	{
		return dictionary_cutoff;
	}

	public void setDictionaryCutoff(int dictionaryCutoff)
	{
		dictionary_cutoff = dictionaryCutoff;
	}
}
