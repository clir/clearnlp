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

import java.io.InputStream;
import java.util.List;

import edu.emory.clir.clearnlp.bin.NLPTrain;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOneVsAllTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;
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
	
	public AbstractStatisticalComponent<?,?,?,?> train(List<String> trainFiles, List<String> developFiles)
	{
		Object[] lexicons = getLexicons(trainFiles);
		ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> prev = train(trainFiles, developFiles, lexicons, null, 0);
		if (!t_configuration.isBootstrap() || NLPTrain.d_stop > 0) return prev.o;
		ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> curr;
		byte[] backup;
		int boot = 1;
		
		try
		{
			while (true)
			{
				// save the previous model
				backup = prev.o.toByteArray();
				curr = train(trainFiles, developFiles, lexicons, prev.o.getModels(), boot++);
				
				if (prev.d >= curr.d)
				{
					BinUtils.LOG.info(String.format("Final score: %4.2f\n", prev.d));
					return createComponentForDecode(backup);
				}
				
				prev = curr;
			}
		}
		catch (Exception e) {e.printStackTrace();}
		throw new IllegalStateException();
	}
	
	private Object[] getLexicons(List<String> trainFiles)
	{
		AbstractStatisticalComponent<?,?,?,?> component = createComponentForCollect();
		Object[] lexicons = null;
		
		if (component != null)
		{
			BinUtils.LOG.info("Collecting lexicons:\n");
			process(component, trainFiles, true);
			lexicons = component.getLexicons();
		}
		
		return lexicons;
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
		double score = trainPipeline(component, trainers, developFiles);
		
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
	protected abstract AbstractStatisticalComponent<?,?,?,?> createComponentForDecode(byte[] models);
	
	private double trainPipeline(AbstractStatisticalComponent<?,?,?,?> component, AbstractTrainer[] trainers, List<String> developFiles)
	{
		AbstractTrainer trainer;
		double score = 0;
		
		try
		{
			for (int i=0; i<trainers.length; i++)
			{
				trainer = trainers[i];
				BinUtils.LOG.info(trainer.trainerInfoFull()+"\n");
				
				switch (trainer.getTrainerType())
				{
				case ONLINE    : score = trainOnline  (component, (AbstractOnlineTrainer)  trainer, developFiles, i); break;
				case ONE_VS_ALL: score = trainOneVsAll(component, (AbstractOneVsAllTrainer)trainer, developFiles, i); break;
				}
			}			
		}
		catch (Exception e) {e.printStackTrace();}
		
		BinUtils.LOG.info("\n");
		return score;
	}
	
	private double trainOnline(AbstractStatisticalComponent<?,?,?,?> component, AbstractOnlineTrainer trainer, List<String> developFiles, int modelID) throws Exception
	{
//		final File TMP_FILE = new File("TZ"+System.currentTimeMillis());
		StringModel model = component.getModel(modelID);
		AbstractEval<?> eval = component.getEval();
		double currScore, prevScore = 0;
		byte[] prevWeights = null;
//		ObjectOutputStream oos;
//		ObjectInputStream ois;
		
		for (int iter=1; ; iter++)
		{
			trainer.train();
			eval.clear();
			process(component, developFiles, false);
			currScore = eval.getScore();
			BinUtils.LOG.info(String.format("%3d: %4.2f\n", iter, currScore));
			
			if (0 < NLPTrain.d_stop && NLPTrain.d_stop < currScore)
				break;
			else if (prevScore < currScore)
			{
				prevScore = currScore;
				prevWeights = model.saveWeightVectorToByteArray();
//				oos = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(TMP_FILE))));
//				oos.writeObject(model.getWeightVector());
//				oos.close();
			}
			else
			{
//				ois = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(TMP_FILE))));
//				model.setWeightVector((AbstractWeightVector)ois.readObject());
//				ois.close();
				model.loadWeightVectorFromByteArray(prevWeights);
				break;
			}			
		}
		
//		TMP_FILE.delete();
		return prevScore;
	}
	
	private double trainOneVsAll(AbstractStatisticalComponent<?,?,?,?> component, AbstractOneVsAllTrainer trainer, List<String> developFiles, int modelID)
	{
		return 0;
	}
	
//	private double evaluate(AbstractStatisticalComponent<?,?,?,?> component, List<String> developFiles, int modelID)
//	
//	{
//		
//	}
	
//	private double trainOnline(AbstractStatisticalComponent<?,?,?,?> component, AbstractOnlineTrainer[] trainers, List<String> developFiles)
//	{
//		int i, count, iter = 0, size = trainers.length;
//		
//		FloatArrayList[] weights = new FloatArrayList[size];
//		StringModel[] models = component.getModels();
//		AbstractEval<?> eval = component.getEval();
//		double[] prevScores = new double[size];
//		boolean[] train = {true, true};
//		double currScore;
//		
//		do
//		{
//			BinUtils.LOG.info("Iteration: "+(++iter)+"\n");
//			count = 0;
//			
//			for (i=0; i<size; i++)
//			{
//				if (train[i])
//				{
//					trainers[i].train();
//					eval.clear();
//					process(component, developFiles, false);
//					currScore = eval.getScore();
//					BinUtils.LOG.info(String.format("%3d: %f\n", i, currScore));
//					
//					if (prevScores[i] < currScore)
//					{
//						count++;
//						prevScores[i] = currScore;
//						weights[i] = models[i].getWeightVector().cloneWeights();
//					}
//					else
//					{
//						train[i] = false;
//						models[i].getWeightVector().setWeights(weights[i]);
//					}
//				}
//			}			
//		}
//		while (count > 0);
//		
//		return prevScores[size-1];
//	}
	
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
