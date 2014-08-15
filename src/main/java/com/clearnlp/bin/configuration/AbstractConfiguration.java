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
package com.clearnlp.bin.configuration;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.train.AbstractAdaGrad;
import com.clearnlp.classification.train.AbstractLiblinear;
import com.clearnlp.classification.train.AbstractTrainer;
import com.clearnlp.classification.train.AdaGradLR;
import com.clearnlp.classification.train.AdaGradSVM;
import com.clearnlp.classification.train.LiblinearL2LR;
import com.clearnlp.classification.train.LiblinearL2SVM;
import com.clearnlp.collection.map.ObjectIntHashMap;
import com.clearnlp.reader.AbstractReader;
import com.clearnlp.reader.LineReader;
import com.clearnlp.reader.RawReader;
import com.clearnlp.reader.TReader;
import com.clearnlp.reader.TSVReader;
import com.clearnlp.util.XmlUtils;
import com.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class AbstractConfiguration implements ConfigurationXML
{
	protected final AbstractReader<?>	t_reader;
	protected final TLanguage			t_language;
	protected final Element				x_document;
	protected final Path				p_model;
	
	public AbstractConfiguration(InputStream in)
	{
		x_document = XmlUtils.getDocumentElement(in);
		t_language = initLanguage();
		p_model    = initModelPath();
		t_reader   = initReader();
	}
	
//	=================================== Initialization ===================================  
	
	private TLanguage initLanguage()
	{
		String language = XmlUtils.getTrimmedTextContentFromFirstElement(x_document, E_LANGUAGE);
		return TLanguage.getType(language);
	}
	
	private Path initModelPath()
	{
		String path = XmlUtils.getTrimmedTextContentFromFirstElement(x_document, E_MODEL);
		return Paths.get(path);
	}
	
	private AbstractReader<?> initReader()
	{
		Element eReader = XmlUtils.getFirstElementByTagName(x_document, E_READER);
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
	
//	=================================== Algorithm ===================================
	
	protected AbstractTrainer getTrainer(Element eMode, StringModel model, int labelCutoff, int featureCutoff, boolean reset)
	{
		Element eAlgorithm = XmlUtils.getFirstElementByTagName(eMode, E_ALGORITHM);
		String name = XmlUtils.getTrimmedAttribute(eAlgorithm, A_NAME);
		
		switch (name)
		{
		case ALG_ADAGRAD  : return getTrainerAdaGrad  (eMode, model, labelCutoff, featureCutoff, reset);
		case ALG_LIBLINEAR: return getTrainerLiblinear(eMode, model, labelCutoff, featureCutoff, reset);
		}
		
		throw new IllegalArgumentException(name+" is not a valid algorithm name.");
	}
	
	private AbstractAdaGrad getTrainerAdaGrad(Element eMode, StringModel model, int labelCutoff, int featureCutoff, boolean reset)
	{
		boolean average = Boolean.parseBoolean(XmlUtils.getTrimmedAttribute(eMode, "average"));
		double alpha = Double.parseDouble(XmlUtils.getTrimmedAttribute(eMode, "alpha"));
		double rho   = Double.parseDouble(XmlUtils.getTrimmedAttribute(eMode, "rho"));
		String type  = XmlUtils.getTrimmedAttribute(eMode, A_TYPE);
		
		switch (type)
		{
		case V_SUPPORT_VECTOR_MACHINE: return new AdaGradSVM(model, labelCutoff, featureCutoff, reset, average, alpha, rho);
		case V_LOGISTIC_REGRESSION   : return new AdaGradLR (model, labelCutoff, featureCutoff, reset, average, alpha, rho);
		}
		
		throw new IllegalArgumentException(type+" is not a valid algorithm type.");
	}
	
	private AbstractLiblinear getTrainerLiblinear(Element eMode, StringModel model, int labelCutoff, int featureCutoff, boolean reset)
	{
		int numThreads = Integer.parseInt(XmlUtils.getTrimmedAttribute(eMode, "threads"));
		double cost = Double.parseDouble(XmlUtils.getTrimmedAttribute(eMode, "cost"));
		double eps  = Double.parseDouble(XmlUtils.getTrimmedAttribute(eMode, "eps"));
		double bias = Double.parseDouble(XmlUtils.getTrimmedAttribute(eMode, "bias"));
		String type = XmlUtils.getTrimmedAttribute(eMode, A_TYPE);
		
		switch (type)
		{
		case V_SUPPORT_VECTOR_MACHINE: return new LiblinearL2SVM(model, labelCutoff, featureCutoff, reset, numThreads, cost, eps, bias);
		case V_LOGISTIC_REGRESSION   : return new LiblinearL2LR (model, labelCutoff, featureCutoff, reset, numThreads, cost, eps, bias);
		}
		
		throw new IllegalArgumentException(type+" is not a valid algorithm type.");
	}
	
//	=================================== Getters ===================================

	public TLanguage getLanguage()
	{
		return t_language;
	}
	
	public Path getModelPath()
	{
		return p_model;
	}
	
	public AbstractReader<?> getReader()
	{
		return t_reader;
	}
}
