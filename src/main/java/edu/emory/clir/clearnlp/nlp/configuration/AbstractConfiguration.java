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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.reader.AbstractReader;
import edu.emory.clir.clearnlp.reader.LineReader;
import edu.emory.clir.clearnlp.reader.RawReader;
import edu.emory.clir.clearnlp.reader.TReader;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.XmlUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractConfiguration implements ConfigurationXML
{
	private Element           x_top;
	private AbstractReader<?> d_reader;
	
//	=================================== CONSTRUCTORS ===================================
	
	public AbstractConfiguration() {}
	
	public AbstractConfiguration(InputStream in)
	{
		x_top    = XmlUtils.getDocumentElement(in);
		d_reader = initReader();
	}
	
	private AbstractReader<?> initReader()
	{
		Element eReader = getFirstElement(E_READER);
		TReader type = TReader.getType(XmlUtils.getTrimmedAttribute(eReader, A_TYPE));
		
		if (type == TReader.RAW)
			return new RawReader();
		else if (type == TReader.LINE)
			return new LineReader();
		else
		{
			ObjectIntHashMap<String> map = getFieldMap(eReader);
			
			int iID		= map.get(FIELD_ID)		- 1;
			int iForm	= map.get(FIELD_FORM)	- 1;
			int iLemma	= map.get(FIELD_LEMMA)	- 1;
			int iPOSTag	= map.get(FIELD_POS)	- 1;
			int iNament = map.get(FIELD_NAMENT)	- 1;
			int iFeats	= map.get(FIELD_FEATS)	- 1;
			int iHeadID	= map.get(FIELD_HEADID)	- 1;
			int iDeprel	= map.get(FIELD_DEPREL)	- 1;
			int iXHeads = map.get(FIELD_XHEADS)	- 1;
			int iSHeads = map.get(FIELD_SHEADS)	- 1;
			
			return new TSVReader(iID, iForm, iLemma, iPOSTag, iNament, iFeats, iHeadID, iDeprel, iXHeads, iSHeads);	
		}
	}
	
	/** Called by {@link #initReader()}. */
	private ObjectIntHashMap<String> getFieldMap(Element eReader)
	{
		NodeList list = eReader.getElementsByTagName(E_COLUMN);
		int i, index, size = list.getLength();
		Element element;
		String field;
		
		ObjectIntHashMap<String> map = new ObjectIntHashMap<String>();
		
		for (i=0; i<size; i++)
		{
			element = (Element)list.item(i);
			field   = XmlUtils.getTrimmedAttribute(element, A_FIELD);
			index   = XmlUtils.getIntegerAttribute(element, A_INDEX);
			
			map.put(field, index);
		}
		
		return map;
	}
	
//	=================================== GETTERS ===================================  

	public AbstractReader<?> getReader()
	{
		return d_reader;
	}
	
	public TLanguage getLanguage()
	{
		String language = XmlUtils.getTrimmedTextContent(getFirstElement(E_LANGUAGE));
		return TLanguage.getType(language);
	}
	
	public int getTrainBeamSize(NLPMode mode)
	{
		Element eMode = getModeElement(mode);
		return XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, E_TRAIN_BEAM_SIZE));
	}
	
	public int getDecodeBeamSize(NLPMode mode)
	{
		Element eMode = getModeElement(mode);
		return XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, E_DECODE_BEAM_SIZE));
	}
	
	public int getThreadSize()
	{
		return XmlUtils.getIntegerTextContent(getFirstElement(E_THREAD_SIZE));
	}
	
//	=================================== ELEMENT ===================================  

	protected Element getFirstElement(String tag)
	{
		return XmlUtils.getFirstElementByTagName(x_top, tag);
	}
	
	protected Element getModeElement(NLPMode mode)
	{
		NodeList list = x_top.getChildNodes();
		int i, len = list.getLength();
		Node node;
		
		for (i=0; i<len; i++)
		{
			node = list.item(i);
			if (node.getNodeName().equals(mode.toString()))
				return (Element)node;
		}
		
		return null;//getFirstElement(mode.toString());
	}
}
