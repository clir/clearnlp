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

import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.train.AbstractAdaGrad;
import com.clearnlp.classification.train.AbstractLiblinear;
import com.clearnlp.classification.train.AbstractTrainer;
import com.clearnlp.classification.train.AdaGradLR;
import com.clearnlp.classification.train.AdaGradSVM;
import com.clearnlp.classification.train.LiblinearL2LR;
import com.clearnlp.classification.train.LiblinearL2SVM;
import com.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class TrainConfiguration extends AbstractConfiguration
{
	public TrainConfiguration(InputStream in)
	{
		super(in);
	}

//	=================================== ALGORITHM ===================================
	
	protected AbstractTrainer getTrainer(Element eMode, StringModel model, boolean reset)
	{
		Element eTrainer = XmlUtils.getFirstElementByTagName(eMode, E_TRAINER);
		String algorithm = XmlUtils.getTrimmedAttribute(eTrainer, A_ALGORITHM);

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
