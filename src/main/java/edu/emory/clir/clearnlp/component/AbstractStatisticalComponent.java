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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AdaGradSVM;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor;



/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractStatisticalComponent<LabelType, StateType extends AbstractState<?,LabelType>, EvalType extends AbstractEval<?>, FeatureType extends AbstractFeatureExtractor<?,?,?>> extends AbstractComponent
{
	protected ICollector<StateType> l_collector;
	protected FeatureType[] f_extractors;
	protected StringModel[] s_models;
	protected EvalType      c_eval;
	protected CFlag         c_flag;
	
	/** Constructs a statistical component for collect. */
	public AbstractStatisticalComponent(ICollector<StateType> collector)
	{
		l_collector = collector;
		setFlag(CFlag.COLLECT);
	}
	
	/** Constructs a statistical component for train. */
	public AbstractStatisticalComponent(FeatureType[] extractors, Object[] lexicons, boolean binary, int modelSize)
	{
		setFlag(CFlag.TRAIN);
		setFeatureExtractors(extractors);
		setLexicons(lexicons);
		setModels(createModels(binary, modelSize));
	}
	
	/** Constructs a statistical component for bootstrap or evaluate. */
	public AbstractStatisticalComponent(FeatureType[] extractors, Object[] lexicons, StringModel[] models, boolean bootstrap)
	{
		if (bootstrap)
			setFlag(CFlag.BOOTSTRAP);
		else
		{
			setFlag(CFlag.EVALUATE);
			initEval();
		}
		
		setFeatureExtractors(extractors);
		setLexicons(lexicons);
		setModels(models);
	}
	
	/** Constructs a statistical component for decode. */
	public AbstractStatisticalComponent(ObjectInputStream in)
	{
		initDecode(in);
	}
	
	/** Constructs a statistical component for decode. */
	public AbstractStatisticalComponent(byte[] models)
	{
		initDecode(models);
	}
	
	private StringModel[] createModels(boolean binary, int modelSize)
	{
		StringModel[] models = new StringModel[modelSize];
		int i;
		
		for (i=0; i<modelSize; i++)
			models[i] = new StringModel(binary);
		
		return models;
	}
	
	protected void initDecode(ObjectInputStream in)
	{
		setFlag(CFlag.DECODE);
		
		try
		{
			load(in);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	protected void initDecode(byte[] models)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(models))));
			initDecode(ois);
		}
		catch (IOException e) {e.printStackTrace();}
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
	
	public byte[] toByteArray() throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(bos)));
		save(oos);
		oos.close();
		return bos.toByteArray();
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
	
	public StringModel getModel(int index)
	{
		return s_models[index];
	}
	
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
		LabelType label;
		
		switch (c_flag)
		{
		case TRAIN    : label = train(state, instances); break;
		case BOOTSTRAP: label = bootstrap(state, instances); break;
		default       : label = decode(state); break;
		}
		
		state.next(label);
	}
	
	protected List<StringInstance> process(StateType state)
	{
		List<StringInstance> instances = isTrainOrBootstrap() ? new ArrayList<StringInstance>() : null;
		
		while (!state.isTerminate())
			process(state, instances);
		
		return instances;
	}
	
	protected LabelType train(StateType state, List<StringInstance> instances)
	{
		StringFeatureVector vector = createStringFeatureVector(state);
		LabelType label = state.getGoldLabel();
		if (!vector.isEmpty()) instances.add(new StringInstance(label.toString(), vector));
		return label;
	}
	
	protected LabelType bootstrap(StateType state, List<StringInstance> instances)
	{
		StringFeatureVector vector = createStringFeatureVector(state);
		LabelType label = state.getGoldLabel();
		if (!vector.isEmpty()) instances.add(new StringInstance(label.toString(), vector));
		return getAutoLabel(state, vector);
	}
	
	protected LabelType decode(StateType state)
	{
		StringFeatureVector vector = createStringFeatureVector(state);
		return getAutoLabel(state, vector);
	}
	
	abstract protected StringFeatureVector createStringFeatureVector(StateType state);
	abstract protected LabelType getAutoLabel(StateType state, StringFeatureVector vector);
	
//	====================================== EVAL ======================================
	
	public EvalType getEval()
	{
		return c_eval;
	}
	
	abstract protected void initEval();

//	====================================== FLAG ======================================
	
	public CFlag getFlag()
	{
		return c_flag;
	}
	
	public void setFlag(CFlag flag)
	{
		c_flag = flag;
	}
	
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
	
//	====================================== ONLINE TRAIN ======================================
	
	abstract public void onlineTrain(List<DEPTree> trees);
	
	protected void onlineTrainSingleAdaGrad(List<DEPTree> trees)
	{
		double currScore = onlineScore(trees);
		if (currScore == 100) return;
		onlineBootstrap(trees);
		
		AbstractOnlineTrainer trainer = new AdaGradSVM(s_models[0], 0, 0, false, 0.01, 0.1);
		byte[] prevModels;
		double prevScore;
		
		try
		{
			while (true)
			{
				prevModels = toByteArray();
				prevScore  = currScore;
				
				trainer.train();
				currScore = onlineScore(trees);
				
				if (prevScore >= currScore)
				{
					initDecode(prevModels);
					break;
				}
			}			
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	protected double onlineScore(List<DEPTree> trees)
	{
		CFlag originalFlag = c_flag;
		c_flag = CFlag.EVALUATE;
		initEval();
		
		for (DEPTree tree : trees)
			process(tree);
		
		c_flag = originalFlag;
		return c_eval.getScore();
	}
	
	protected void onlineBootstrap(List<DEPTree> trees)
	{
		CFlag originalFlag = c_flag;
		c_flag = CFlag.BOOTSTRAP;
		
		for (DEPTree tree : trees)
		{
			onlineLexicons(tree);
			process(tree);
		}
		
		c_flag = originalFlag;
	}
	
	abstract protected void onlineLexicons(DEPTree tree);
}

