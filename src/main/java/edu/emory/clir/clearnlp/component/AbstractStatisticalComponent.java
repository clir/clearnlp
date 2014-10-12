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
package edu.emory.clir.clearnlp.component;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor;



/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractStatisticalComponent<LabelType, StateType extends AbstractState<LabelType>, EvalType extends AbstractEval<LabelType>, FeatureType extends AbstractFeatureExtractor<?,?,?>> extends AbstractComponent
{
	protected FeatureType[] f_extractors;
	protected StringModel[] s_models;
	protected EvalType      c_eval;
	protected CFlag         c_flag;
	
	/** Constructs a statistical component for collect. */
	public AbstractStatisticalComponent()
	{
		c_flag = CFlag.COLLECT;
	}
	
	/** Constructs a statistical component for train. */
	public AbstractStatisticalComponent(FeatureType[] extractors, Object[] lexicons, boolean binary, int modelSize)
	{
		c_flag = CFlag.TRAIN;
		setFeatureExtractors(extractors);
		setLexicons(lexicons);
		setModels(createModels(binary, modelSize));
	}
	
	/** Constructs a statistical component for bootstrap. */
	public AbstractStatisticalComponent(FeatureType[] extractors, Object[] lexicons, StringModel[] models)
	{
		c_flag = CFlag.BOOTSTRAP;
		setFeatureExtractors(extractors);
		setLexicons(lexicons);
		setModels(models);
	}
	
	/** Constructs a statistical component for evaluate. */
	public AbstractStatisticalComponent(FeatureType[] extractors, Object[] lexicons, StringModel[] models, EvalType eval)
	{
		c_flag = CFlag.EVALUATE;
		setFeatureExtractors(extractors);
		setLexicons(lexicons);
		setModels(models);
		setEval(eval);
	}
	
	/** Constructs a statistical component for decode. */
	public AbstractStatisticalComponent(ObjectInputStream in)
	{
		c_flag = CFlag.DECODE;
		
		try
		{
			load(in);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	private StringModel[] createModels(boolean binary, int modelSize)
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
		setLexicons((Object[])in.readObject());
		setModels(loadModels(in));
	}
	
	/**
	 * Saves all models and objects of this component. 
	 * @throws Exception
	 */
	public void save(ObjectOutputStream out) throws Exception
	{
		out.writeObject(f_extractors);
		out.writeObject(getLexicons());
		saveModels(out);
	}
	
	private StringModel[] loadModels(ObjectInputStream in) throws Exception
	{
		int i, len = in.readInt();
		StringModel[] models = new StringModel[len];
		
		for (i=0; i<len; i++)
			models[i] = new StringModel(in);

		return models;
	}
	
	private void saveModels(ObjectOutputStream out) throws Exception
	{
		out.writeInt(s_models.length);
		
		for (StringModel model : s_models)
			model.save(out);
	}
	
//	====================================== LEXICONS ======================================

	/** @return all objects containing lexicons. */
	abstract public Object[] getLexicons();
	
	/** Sets lexicons used for this component. */
	abstract public void setLexicons(Object[] lexicons);

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
	
//	====================================== PROCESS ======================================
	
	protected void process(StateType state, List<StringInstance> instances)
	{
		switch (c_flag)
		{
		case TRAIN    : train(state, instances); break;
		case BOOTSTRAP: bootstrap(state, instances); break;
		default       : decode(state); break;
		}
	}
	
	protected void train(StateType state, List<StringInstance> instances)
	{
		StringFeatureVector vector = createStringFeatureVector(state);
		LabelType label = state.getGoldLabel();
		if (!vector.isEmpty()) instances.add(new StringInstance(label.toString(), vector));
		state.setAutoLabel(label);
	}
	
	protected void bootstrap(StateType state, List<StringInstance> instances)
	{
		StringFeatureVector vector = createStringFeatureVector(state);
		LabelType label = state.getGoldLabel();
		if (!vector.isEmpty()) instances.add(new StringInstance(label.toString(), vector));
		state.setAutoLabel(getAutoLabel(vector));
	}
	
	protected void decode(StateType state)
	{
		StringFeatureVector vector = createStringFeatureVector(state);
		state.setAutoLabel(getAutoLabel(vector));
	}
	
	abstract protected StringFeatureVector createStringFeatureVector(StateType state);
	abstract protected LabelType getAutoLabel(StringFeatureVector vector);
	
//	====================================== EVAL ======================================
	
	public EvalType getEval()
	{
		return c_eval;
	}
	
	public void setEval(EvalType eval)
	{
		c_eval = eval;
	}
	
//	====================================== FLAG ======================================

	public boolean isCollect()
	{
		return c_flag == CFlag.COLLECT;
	}
	
	public boolean isTrain()
	{
		return c_flag == CFlag.TRAIN;
	}
	
	public boolean isBootstrap()
	{
		return c_flag == CFlag.BOOTSTRAP;
	}
	
	public boolean isEvaluate()
	{
		return c_flag == CFlag.EVALUATE;
	}
	
	public boolean isDecode()
	{
		return c_flag == CFlag.DECODE;
	}
	
	public boolean isTrainOrBootstrap()
	{
		return isTrain() || isBootstrap();
	}
}
