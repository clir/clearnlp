/**
 * Copyright 2015, Emory University
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.FileUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPJackknife extends NLPTrain
{
	@Option(name="-threads", usage="number of threads (default: 1)", required=false, metaVar="<Integer>")
	protected int n_threads = 1;
	
	public NLPJackknife() {}
	
	public NLPJackknife(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		List<String> trainFiles = FileUtils.getFileList(s_trainPath, s_trainExt, false);
		NLPMode      mode       = NLPMode.valueOf(s_mode);
	
		trainCV(trainFiles, s_featureFiles, s_configurationFile, mode, n_threads);
	}
	
	private void trainCV(List<String> trainFiles, String[] featureFiles, String configurationFile, NLPMode mode, int threads)
	{
		int i, size = trainFiles.size();
		Collections.sort(trainFiles);
		
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		
		for (i=0; i<size; i++)
			executor.execute(new TrainTask(new ArrayList<>(trainFiles), featureFiles, configurationFile, mode, i));
		
		executor.shutdown();
		
		try
		{
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
	class TrainTask implements Runnable
	{
		private List<String> train_files;
		private String[] feature_files;
		private String develop_file;
		String configuration_file;
		private NLPMode nlp_mode;
		private int dev_index;
		
		/** @param currLabel the current label to train. */
		public TrainTask(List<String> trainFiles, String[] featureFiles, String configurationFile, NLPMode mode, int devIndex)
		{
			train_files  = trainFiles;
			develop_file = trainFiles.remove(devIndex);
			configuration_file = configurationFile;
			feature_files = featureFiles;
			dev_index = devIndex;
			nlp_mode = mode;
		}
		
		public void run()
		{
			AbstractStatisticalComponent<?,?,?,?> component = train(train_files, DSUtils.toArrayList(develop_file), feature_files, configuration_file, nlp_mode).o;
			saveModel(component, s_modelPath+"."+dev_index);
		}
    }
	
	static public void main(String[] args)
	{
		new NLPJackknife(args);
	}
}
