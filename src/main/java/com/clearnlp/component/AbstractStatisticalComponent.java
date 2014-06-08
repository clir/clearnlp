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
package com.clearnlp.component;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.clearnlp.classification.model.StringModel;
import com.clearnlp.feature.AbstractFeatureExtractor;



/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractStatisticalComponent<FeatureType extends AbstractFeatureExtractor<?,?>> extends AbstractComponent
{
	protected FeatureType[] f_extractors;
	protected StringModel[] s_models;
	
	/** Constructs a statistical component for collecting lexicons. */
	public AbstractStatisticalComponent(FeatureType[] extractors)
	{
		setFeatureExtractors(extractors);
	}
	
	/** Constructs a statistical component for training and bootstrapping. */
	public AbstractStatisticalComponent(FeatureType[] extractors, Object[] lexicons, boolean binary, int modelSize)
	{
		setFeatureExtractors(extractors);
		setModels(initModels(binary, modelSize));
		setLexicons(lexicons);
	}
	
	/** Constructs a statistical component for bootstrapping and decoding. */
	public AbstractStatisticalComponent(ObjectInputStream in)
	{
		try
		{
			load(in);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	private StringModel[] initModels(boolean binary, int modelSize)
	{
		StringModel[] models = new StringModel[modelSize];
		int i;
		
		for (i=0; i<modelSize; i++)
			models[i] = new StringModel(binary);
		
		return models;
	}
	
//	====================================== LOAD/SAVE ======================================

	/**
	 * Loads all models and objects of this component.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void load(ObjectInputStream in) throws Exception
	{
		setFeatureExtractors((FeatureType[])in.readObject());
		setModels((StringModel[])in.readObject());
		setLexicons((Object[])in.readObject());
	}
	
	/**
	 * Saves all models and objects of this component. 
	 * @throws Exception
	 */
	public void save(ObjectOutputStream out) throws Exception
	{
		out.writeObject(f_extractors);
		out.writeObject(s_models);
		out.writeObject(getLexicons());
	}

//	====================================== FEATURES ======================================
	
	public FeatureType[] getFeatureExtractors()
	{
		return f_extractors;
	}
	
	public void setFeatureExtractors(FeatureType[] features)
	{
		f_extractors = features;
	}
	
//	====================================== MODELS ======================================

	public StringModel[] getModels()
	{
		return s_models;
	}
	
	public void setModels(StringModel[] models)
	{
		s_models = models;
	}
	
//	====================================== LEXICONS ======================================

	/** @return all objects containing lexicons. */
	abstract public Object[] getLexicons();
	
	/** Sets lexicons used for this component. */
	abstract public void setLexicons(Object[] lexicons);
}
