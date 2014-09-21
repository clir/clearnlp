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
package edu.emory.clir.clearnlp.bin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOneVsAllTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;
import edu.emory.clir.clearnlp.classification.trainer.TrainerType;
import edu.emory.clir.clearnlp.collection.list.FloatArrayList;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.CFlag;
import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.configuration.AbstractTrainConfiguration;
import edu.emory.clir.clearnlp.nlp.factory.AbstractNLPFactory;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<string>")
	private String s_configurationFile;
	@Option(name="-f", usage="feature template files (required, delimited by \":\")", required=true, metaVar="<string>")
	private String s_featureTemplateFile;
	@Option(name="-t", usage="path to training files (required)", required=true, metaVar="<filepath>")
	private String s_trainPath;
	@Option(name="-te", usage="training file extension (default: .*)", required=false, metaVar="<regex>")
	private String s_trainExt = ".*";
	@Option(name="-d", usage="path to development files (optional)", required=false, metaVar="<filepath>")
	private String s_developPath;
	@Option(name="-de", usage="development file extension (default: .*)", required=false, metaVar="<regex>")
	private String s_developExt = ".*";
	
	public NLPTrain() {}
	
	public NLPTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		
	}
	
	protected void collect(AbstractNLPFactory factory, String[] featureFiles, String[] trainFiles, String[] developFiles, String modelFile) throws Exception
	{
		AbstractTrainConfiguration configuration = factory.getTrainConfiguration();
		TSVReader reader = (TSVReader)configuration.getReader();
		AbstractStatisticalComponent<?,?,?,?> component;
		AbstractTrainer[] trainers;
		
		// collect
		component = factory.createComponent(featureFiles);
		process(component, reader, trainFiles);
		
		// train
		component = factory.createComponent(component, CFlag.TRAIN);
		process(component, reader, trainFiles);
		
		component = factory.createComponent(component, CFlag.EVALUATE);
		trainers = configuration.getTrainers(component.getModels());
		train(trainers, developFiles, reader, component);
		
		// bootstrap
		ByteArrayOutputStream bos = null;
		double prevScore = 0, currScore;
		ObjectOutputStream oos;
		
		while (true)
		{
			component = factory.createComponent(component, CFlag.BOOTSTRAP);
			process(component, reader, trainFiles);

			component = factory.createComponent(component, CFlag.EVALUATE);
			trainers  = configuration.getTrainers(component.getModels());
			currScore = train(trainers, developFiles, reader, component);
			
			if (prevScore < currScore)
			{
				bos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(new BufferedOutputStream(bos));
				component.save(oos);
				oos.close();
			}
			else
			{
				ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bos.toByteArray())));
				component.load(ois);
				ois.close();		
				break;
			}
		}
		
		if (modelFile != null)
		{
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(modelFile)));
			component.save(oos);
			oos.close();
		}
	}
	
	private double train(AbstractTrainer[] trainers, String[] developFiles, TSVReader reader, AbstractStatisticalComponent<?,?,?,?> component)
	{
		if (trainers[0].getTrainerType() == TrainerType.ONLINE)
			return trainOnline((AbstractOnlineTrainer[])trainers, developFiles, reader, component);
		else
			return trainOneVsAll((AbstractOneVsAllTrainer[])trainers, developFiles, reader, component);
	}
	
	private double trainOnline(AbstractOnlineTrainer[] trainers, String[] developFiles, TSVReader reader, AbstractStatisticalComponent<?,?,?,?> component)
	{
		int i, count, size = trainers.length;
		
		FloatArrayList[] weights = new FloatArrayList[size];
		StringModel[] models = component.getModels();
		AbstractEval<?> eval = component.getEval();
		double[] prevScores = new double[size];
		double[] currScores = new double[size];
		boolean[] train = {true, true};
		
		do
		{
			count = 0;
			
			for (i=0; i<size; i++)
			{
				if (train[i])
				{
					trainers[i].train();
					eval.clear();
					process(component, reader, developFiles);
					currScores[i] = eval.getScore();
					
					if (prevScores[i] < currScores[i])
					{
						count++;
						prevScores[i] = currScores[i];
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
		while (0 < count);
		
		return currScores[size-1];
	}
	
	private double trainOneVsAll(AbstractOneVsAllTrainer[] trainers, String[] developFiles, TSVReader reader, AbstractStatisticalComponent<?,?,?,?> component)
	{
		return 0;
	}
	
	public void process(AbstractStatisticalComponent<?,?,?,?> component, TSVReader reader, String[] filelist)
	{
		for (String filename : filelist)
			process(component, reader, filename);
	}
	
	public void process(AbstractStatisticalComponent<?,?,?,?> component, TSVReader reader, String filename)
	{
		reader.open(IOUtils.createFileInputStream(filename));
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
			component.process(tree);
		
		reader.close();
	}
	
	static public void main(String[] args)
	{
		new NLPTrain(args);
	}
}
