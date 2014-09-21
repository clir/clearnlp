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
package com.clearnlp.nlp.configuration;

import java.io.InputStream;

import org.w3c.dom.Element;

import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.trainer.AbstractAdaGrad;
import com.clearnlp.classification.trainer.AbstractLiblinear;
import com.clearnlp.classification.trainer.AbstractTrainer;
import com.clearnlp.classification.trainer.AdaGradLR;
import com.clearnlp.classification.trainer.AdaGradSVM;
import com.clearnlp.classification.trainer.LiblinearL2LR;
import com.clearnlp.classification.trainer.LiblinearL2SVM;
import com.clearnlp.nlp.NLPMode;
import com.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractTrainConfiguration extends AbstractConfiguration
{
	protected NLPMode n_mode;
	
	public AbstractTrainConfiguration(NLPMode mode)
	{
		super();
		setMode(mode);
	}
	
	public AbstractTrainConfiguration(InputStream in, NLPMode mode)
	{
		super(in);
		setMode(mode);
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

//	=================================== TRAINER ===================================
	
	public AbstractTrainer[] getTrainers(StringModel[] models)
	{
		return getTrainers(models, true);
	}
	
	public AbstractTrainer[] getTrainers(StringModel[] models, boolean reset)
	{
		AbstractTrainer[] trainers = new AbstractTrainer[models.length];
		Element eMode = getFirstElement(n_mode.toString());
		
		for (int i=0; i<models.length; i++)
			trainers[i] = getTrainer(eMode, models, i, reset);
		
		return trainers;
	}
	
	private AbstractTrainer getTrainer(Element eMode, StringModel[] models, int index, boolean reset)
	{
		Element eTrainer = XmlUtils.getFirstElementByTagName(eMode, E_TRAINER);
		String algorithm = XmlUtils.getTrimmedAttribute(eTrainer, A_ALGORITHM);

		if (reset) models[index].reset();
		
		switch (algorithm)
		{
		case ALG_ADAGRAD  : return getTrainerAdaGrad  (eTrainer, models[index]);
		case ALG_LIBLINEAR: return getTrainerLiblinear(eTrainer, models[index]);
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
		
		switch (type)
		{
		case V_SUPPORT_VECTOR_MACHINE: return new AdaGradSVM(model, labelCutoff, featureCutoff, average, alpha, rho);
		case V_LOGISTIC_REGRESSION   : return new AdaGradLR (model, labelCutoff, featureCutoff, average, alpha, rho);
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
}
