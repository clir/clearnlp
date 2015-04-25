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
package edu.emory.clir.clearnlp.component.configuration;

import java.io.InputStream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractAdaGrad;
import edu.emory.clir.clearnlp.classification.trainer.AbstractLiblinear;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AdaGradLR;
import edu.emory.clir.clearnlp.classification.trainer.AdaGradSVM;
import edu.emory.clir.clearnlp.classification.trainer.LiblinearL2LR;
import edu.emory.clir.clearnlp.classification.trainer.LiblinearL2SVM;
import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
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
	private AbstractReader<?> d_reader;
	private NLPMode           n_mode;
	private Element           x_top;
	
//	=================================== CONSTRUCTORS ===================================
	
	public AbstractConfiguration(NLPMode mode)
	{
		setMode(mode);
	}
	
	public AbstractConfiguration(InputStream in)
	{
		init(in);
	}
	
	public AbstractConfiguration(NLPMode mode, InputStream in)
	{
		this(mode);
		init(in);
	}
	
	private void init(InputStream in)
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
		
		return null;
	}

//	=================================== MODE ===================================
	
	public NLPMode getMode()
	{
		return n_mode;
	}
	
	public void setMode(NLPMode mode)
	{
		n_mode = mode;
	}
	
	protected Element getModeElement()
	{
		return getModeElement(n_mode);
	}
	
//	=================================== TRAINER ===================================
	
	public boolean isBootstrap()
	{
		Element eMode = getModeElement();
		Element eBootstrap = XmlUtils.getFirstElementByTagName(eMode, E_BOOTSTRAPS);
		return (eBootstrap != null) ? Boolean.parseBoolean(XmlUtils.getTrimmedTextContent(eBootstrap)) : false;
	}
	
	public AbstractTrainer[] getTrainers(StringModel[] models)
	{
		return getTrainers(models, true);
	}
	
	public AbstractTrainer[] getTrainers(StringModel[] models, boolean reset)
	{
		AbstractTrainer[] trainers = new AbstractTrainer[models.length];
		Element eMode = getModeElement();
		
		for (int i=0; i<models.length; i++)
			trainers[i] = getTrainer(eMode, models, i, reset);
		
		return trainers;
	}
	
	private AbstractTrainer getTrainer(Element eMode, StringModel[] models, int index, boolean reset)
	{
		Element  eTrainer = XmlUtils.getElementByTagName(eMode, E_TRAINER, index);
		String  algorithm = XmlUtils.getTrimmedAttribute(eTrainer, A_ALGORITHM);
		StringModel model = models[index];
		if (reset) model.reset();
		
		switch (algorithm)
		{
		case ALG_ADAGRAD  : return getTrainerAdaGrad  (eTrainer, model);
		case ALG_LIBLINEAR: return getTrainerLiblinear(eTrainer, model);
		}
		
		throw new IllegalArgumentException(algorithm+" is not a valid algorithm name.");
	}
	
	private AbstractAdaGrad getTrainerAdaGrad(Element eTrainer, StringModel model)
	{
		int labelCutoff   = XmlUtils.getIntegerAttribute(eTrainer, A_LABEL_CUTOFF);
		int featureCutoff = XmlUtils.getIntegerAttribute(eTrainer, A_FEATURE_CUTOFF);
		String type       = XmlUtils.getTrimmedAttribute(eTrainer, A_TYPE);
		
		boolean average = XmlUtils.getBooleanAttribute(eTrainer, "average");
		double  alpha   = XmlUtils.getDoubleAttribute (eTrainer, "alpha");
		double  rho     = XmlUtils.getDoubleAttribute (eTrainer, "rho");
		double  bias    = XmlUtils.getDoubleAttribute (eTrainer, "bias");
		
		switch (type)
		{
		case V_SUPPORT_VECTOR_MACHINE: return new AdaGradSVM(model, labelCutoff, featureCutoff, average, alpha, rho, bias);
		case V_LOGISTIC_REGRESSION   : return new AdaGradLR (model, labelCutoff, featureCutoff, average, alpha, rho, bias);
		}
		
		throw new IllegalArgumentException(type+" is not a valid algorithm type.");
	}
	
	private AbstractLiblinear getTrainerLiblinear(Element eTrainer, StringModel model)
	{
		int labelCutoff   = XmlUtils.getIntegerAttribute(eTrainer, A_LABEL_CUTOFF);
		int featureCutoff = XmlUtils.getIntegerAttribute(eTrainer, A_FEATURE_CUTOFF);
		int numThreads    = XmlUtils.getIntegerAttribute(eTrainer, A_NUMBER_OF_THREADS);
		String type       = XmlUtils.getTrimmedAttribute(eTrainer, A_TYPE);
		
		double cost = XmlUtils.getDoubleAttribute(eTrainer, "cost");
		double eps  = XmlUtils.getDoubleAttribute(eTrainer, "eps");
		double bias = XmlUtils.getDoubleAttribute(eTrainer, "bias");
		
		switch (type)
		{
		case V_SUPPORT_VECTOR_MACHINE: return new LiblinearL2SVM(model, labelCutoff, featureCutoff, numThreads, cost, eps, bias);
		case V_LOGISTIC_REGRESSION   : return new LiblinearL2LR (model, labelCutoff, featureCutoff, numThreads, cost, eps, bias);
		}
		
		throw new IllegalArgumentException(type+" is not a valid algorithm type.");
	}
	
//	=================================== TEXT CONTENTS ===================================

	public double getDoubleTextContent(Element eMode, String tagName)
	{
		return XmlUtils.getDoubleTextContent(XmlUtils.getFirstElementByTagName(eMode, tagName));
	}
	
	public int getIntegerTextContent(Element eMode, String tagName)
	{
		return XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, tagName));
	}
	
	public String getTextContent(Element eMode, String tagName)
	{
		return XmlUtils.getTrimmedTextContent(XmlUtils.getFirstElementByTagName(eMode, tagName));
	}
	
//	=================================== BEAM ===================================
	
	public int getBeamSize(NLPMode mode)
	{
		Element eMode = getModeElement();
		return XmlUtils.getIntegerTextContent(XmlUtils.getFirstElementByTagName(eMode, E_BEAM_SIZE));
	}
}
