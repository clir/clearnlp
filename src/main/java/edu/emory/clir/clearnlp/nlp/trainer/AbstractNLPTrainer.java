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
package edu.emory.clir.clearnlp.nlp.trainer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOneVsAllTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;
import edu.emory.clir.clearnlp.classification.trainer.TrainerType;
import edu.emory.clir.clearnlp.collection.list.FloatArrayList;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.configuration.AbstractTrainConfiguration;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractNLPTrainer
{
	protected AbstractTrainConfiguration t_configuration;
	
//	====================================== CONSTRUCTORS ======================================
	
	public AbstractNLPTrainer(InputStream configuration)
	{	
		t_configuration = createConfiguration(configuration);
	}
	
	public AbstractStatisticalComponent<?,?,?,?> collect(List<String> trainFiles)
	{
		AbstractStatisticalComponent<?,?,?,?> component = createComponentForCollect();
		BinUtils.LOG.info("Collecting lexicons:\n");
		process(component, trainFiles, true);
		return component;
	}
	
	public AbstractStatisticalComponent<?,?,?,?> train(List<String> trainFiles, List<String> developFiles, Object[] lexicons)
	{
		ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> prev = train(trainFiles, developFiles, lexicons, null, 0);
		if (!t_configuration.isBootstrap()) return prev.o;
		
		ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> curr;
		ByteArrayOutputStream bos;
		ObjectOutputStream oos;
		int boot = 1;
		
		try
		{
			while (true)
			{
				// save the previous model
				bos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(bos)));
				prev.o.save(oos);
				oos.close();
				
				curr = train(trainFiles, developFiles, lexicons, prev.o.getModels(), boot++);
				
				if (prev.d >= curr.d)
				{
					ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(bos.toByteArray()))));
					BinUtils.LOG.info(String.format("Final score: %4.2f\n", prev.d));
					return createComponentForDecode(ois);
				}
				
				prev = curr;
			}
		}
		catch (Exception e) {e.printStackTrace();}
		throw new IllegalStateException();
	}
	
	private ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> train(List<String> trainFiles, List<String> developFiles, Object[] lexicons, StringModel[] models, int boot)
	{
		// train
		AbstractStatisticalComponent<?,?,?,?> component = (models == null) ? createComponentForTrain(lexicons) : createComponentForBootstrap(lexicons, models);
		BinUtils.LOG.info("Generating training instances: "+boot+"\n");
		process(component, trainFiles, true);
		
		// evaluate
		AbstractTrainer[] trainers = t_configuration.getTrainers(component.getModels());
		component = createComponentForEvaluate(lexicons, component.getModels());
		for (AbstractTrainer trainer : trainers)
			BinUtils.LOG.info(trainer.trainerInfoFull()+"\n\n");
		
		double score = train(component, trainers, developFiles);
		BinUtils.LOG.info("\n");
		
		return new ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>>(component, score); 
	}
	
	/** Initializes the training configuration. */
	protected abstract AbstractTrainConfiguration createConfiguration(InputStream in);
	
	/** Creates an NLP component for collecting lexicons. */
	protected abstract AbstractStatisticalComponent<?,?,?,?> createComponentForCollect();
	
	/** Creates an NLP component for training. */
	protected abstract AbstractStatisticalComponent<?,?,?,?> createComponentForTrain(Object[] lexicons);
	
	/** Creates an NLP component for bootstrap. */
	protected abstract AbstractStatisticalComponent<?,?,?,?> createComponentForBootstrap(Object[] lexicons, StringModel[] models);
	
	/** Creates an NLP component for evaluation. */
	protected abstract AbstractStatisticalComponent<?,?,?,?> createComponentForEvaluate(Object[] lexicons, StringModel[] models);
	
	/** Creates an NLP component for decode. */
	protected abstract AbstractStatisticalComponent<?,?,?,?> createComponentForDecode(ObjectInputStream in);
	
	private double train(AbstractStatisticalComponent<?,?,?,?> component, AbstractTrainer[] trainers, List<String> developFiles)
	{
		if (trainers[0].getTrainerType() == TrainerType.ONLINE)
			return trainOnline(component, toAbstractOnlineTrainers(trainers), developFiles);
		else
			return trainOneVsAll(component, toAbstractOneVsAllTrainers(trainers), developFiles);
	}
	
	private AbstractOnlineTrainer[] toAbstractOnlineTrainers(AbstractTrainer[] trainers)
	{
		int i, len = trainers.length;
		AbstractOnlineTrainer[] t = new AbstractOnlineTrainer[len];
		
		for (i=0; i<len; i++)
			t[i] = (AbstractOnlineTrainer)trainers[i];
		
		return t;
	}
	
	private AbstractOneVsAllTrainer[] toAbstractOneVsAllTrainers(AbstractTrainer[] trainers)
	{
		int i, len = trainers.length;
		AbstractOneVsAllTrainer[] t = new AbstractOneVsAllTrainer[len];
		
		for (i=0; i<len; i++)
			t[i] = (AbstractOneVsAllTrainer)trainers[i];
		
		return t;
	}
	
	private double trainOnline(AbstractStatisticalComponent<?,?,?,?> component, AbstractOnlineTrainer[] trainers, List<String> developFiles)
	{
		int i, count, iter = 0, size = trainers.length;
		
		FloatArrayList[] weights = new FloatArrayList[size];
		StringModel[] models = component.getModels();
		AbstractEval<?> eval = component.getEval();
		double[] prevScores = new double[size];
		boolean[] train = {true, true};
		double currScore;
		
		do
		{
			BinUtils.LOG.info("Iteration: "+(++iter)+"\n");
			count = 0;
			
			for (i=0; i<size; i++)
			{
				if (train[i])
				{
					trainers[i].train();
					eval.clear();
					process(component, developFiles, false);
					currScore = eval.getScore();
					BinUtils.LOG.info(String.format("%3d: %f\n", i, currScore));
					
					if (prevScores[i] < currScore)
					{
						count++;
						prevScores[i] = currScore;
						weights[i] = models[i].getWeightVector().cloneWeights();
					}
					else
					{
						train[i] = false;
						models[i].getWeightVector().setWeights(weights[i]);
					}
				}
			}			
		}
		while (count > 0);
		
		return prevScores[size-1];
	}
	
	private double trainOneVsAll(AbstractStatisticalComponent<?,?,?,?> component, AbstractOneVsAllTrainer[] trainers, List<String> developFiles)
	{
		return 0;
	}
	
	public void process(AbstractStatisticalComponent<?,?,?,?> component, List<String> filelist, boolean log)
	{
		for (String filename : filelist)
		{
			process(component, filename);
			if (log) BinUtils.LOG.info(".");
		}	if (log) BinUtils.LOG.info("\n\n");
	}
	
	public void process(AbstractStatisticalComponent<?,?,?,?> component, String filename)
	{
		TSVReader reader = (TSVReader)t_configuration.getReader();
		reader.open(IOUtils.createFileInputStream(filename));
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
			component.process(tree);
		
		reader.close();
	}
}
