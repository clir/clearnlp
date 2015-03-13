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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.kohsuke.args4j.Option;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.pos.DefaultPOSTagger;
import edu.emory.clir.clearnlp.component.trainer.AbstractNLPTrainer;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPFeat;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<string>")
	protected String s_configurationFile;
	@Option(name="-f", usage="feature template files (required, delimited by "+"\":\""+")", required=true, metaVar="<string>")
	protected String s_featureTemplateFile;
	@Option(name="-t", usage="path to training files (required)", required=true, metaVar="<filepath>")
	protected String s_trainPath;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<regex>")
	protected String s_trainExt = "*";
	@Option(name="-d", usage="path to development files (required)", required=true, metaVar="<filepath>")
	protected String s_developPath;
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<regex>")
	protected String s_developExt = "*";
	@Option(name="-m", usage="model path (optional)", required=false, metaVar="<filename>")
	protected String s_modelPath = null;
	@Option(name="-mode", usage="pos|dep|srl", required=true, metaVar="<string>")
	protected String s_mode = ".*";
	@Option(name="-threads", usage="number of threads (default: 1)", required=false, metaVar="<Integer>")
	protected int n_threads = 1;
	
	public NLPTrain() {}
	
	public NLPTrain(String[] args) throws InterruptedException, ExecutionException
	{
		BinUtils.initArgs(args, this);
		
		List<String> trainFiles   = FileUtils.getFileList(s_trainPath  , s_trainExt  , false);
		List<String> developFiles = FileUtils.getFileList(s_developPath, s_developExt, false);
		String[]     featureFiles = Splitter.splitColons(s_featureTemplateFile);
		NLPMode      mode         = NLPMode.valueOf(s_mode);

		ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> p = train(trainFiles, developFiles, featureFiles, s_configurationFile, mode);
		BinUtils.LOG.info(String.format("Final score: %4.2f\n", p.d));
		BinUtils.LOG.info(s_configurationFile+"\n");
		BinUtils.LOG.info(s_featureTemplateFile+"\n");
		if (s_modelPath != null) saveModel(p.o, s_modelPath);
	}
	
	class TrainTask implements Callable<ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>>>
	{
		private List<String> train_files;
		private String[] feature_files;
		private String develop_file;
		private NLPMode nlp_mode;
		private int dev_index;
		
		/** @param currLabel the current label to train. */
		public TrainTask(List<String> trainFiles, String[] featureFiles, NLPMode mode, int devIndex)
		{
			train_files  = trainFiles;
			develop_file = trainFiles.remove(devIndex);
			feature_files = featureFiles;
			dev_index = devIndex;
			nlp_mode = mode;
		}
		
		public ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> call()
		{
			AbstractStatisticalComponent<?,?,?,?> component = train(train_files, Lists.newArrayList(develop_file), feature_files, s_configurationFile, nlp_mode).o;
			saveModel(component, s_modelPath+"."+dev_index);
			return null;
		}
    }
	
	public ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> train(List<String> trainFiles, List<String> developFiles, String[] featureFiles, String configurationFile, NLPMode mode)
	{
		InputStream configuration  = IOUtils.createFileInputStream(configurationFile);
		InputStream[] features     = IOUtils.createFileInputStreams(featureFiles);
		AbstractNLPTrainer trainer = NLPUtils.getTrainer(mode, configuration, features);
		return trainer.train(trainFiles, developFiles);
	}
	
	public void saveModel(AbstractStatisticalComponent<?,?,?,?> component, String modelPath)
	{
		ObjectOutputStream out;
		
		try
		{
			out = new ObjectOutputStream(new XZOutputStream(new BufferedOutputStream(new FileOutputStream(modelPath)), new LZMA2Options()));
			component.save(out);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	
	
	
	
	
	void onlineTrain()
	{
		try
		{
			DefaultPOSTagger tagger = new DefaultPOSTagger(new ObjectInputStream(new XZInputStream(new BufferedInputStream(new FileInputStream(s_modelPath)))));
			for (DEPTree tree : getTrees())
			{
				tagger.process(tree);
				System.out.println(tree.toStringPOS()+"\n");
			}
			tagger.onlineTrain(getTrees());
			System.out.println("---------------------------\n");
			for (DEPTree tree : getTrees())
			{
				tagger.process(tree);
				System.out.println(tree.toStringPOS()+"\n");
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	private List<DEPTree> getTrees()
	{
		List<DEPTree> list = Lists.newArrayList();
		DEPTree tree;
		
		tree = new DEPTree(5);
		tree.add(new DEPNode(1, "mr.", "NNP", new DEPFeat()));
		tree.add(new DEPNode(2, "boom", "NNP", new DEPFeat()));
		tree.add(new DEPNode(3, "toissed", "VBD", new DEPFeat()));
		tree.add(new DEPNode(4, "paat", "JJ", new DEPFeat()));
		tree.add(new DEPNode(5, "balll", "NN", new DEPFeat()));
		list.add(tree);
		
		tree = new DEPTree(4);
		tree.add(new DEPNode(1, "John", "NNP", new DEPFeat()));
		tree.add(new DEPNode(2, "bought", "VBD", new DEPFeat()));
		tree.add(new DEPNode(3, "a", "DT", new DEPFeat()));
		tree.add(new DEPNode(4, "car", "NN", new DEPFeat()));
		list.add(tree);
		
		return list;
	}
	
//	================================= Multi-threaded Version =================================
	
//	public NLPTrain(String[] args) throws InterruptedException, ExecutionException
//	{
//		BinUtils.initArgs(args, this);
//		
//		List<String> trainFiles     = FileUtils.getFileList(s_trainPath  , s_trainExt  , false);
//		List<String> developFiles   = FileUtils.getFileList(s_developPath, s_developExt, false);
//		String[]     configurations = Splitter.splitCommas(s_configurationFile);
//		String[]     features       = Splitter.splitCommas(s_featureTemplateFile);
//		NLPMode      mode           = NLPMode.valueOf(s_mode);
//
//		List<Future<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>>> futures = new ArrayList<>();
//		Future<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>> future, max;
//		Callable<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>> c;
//		ExecutorService executor = Executors.newFixedThreadPool(n_threads);
//		
//		for (String configuration : configurations)
//		{
//			for (String feature : features)
//			{
//				c = new Callable<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>>()
//				{
//					@Override
//					public ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String> call() throws Exception
//					{
//						final ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> p = train(trainFiles, developFiles, Splitter.splitColons(feature), configuration, mode);
//						return new ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>(p.o, configuration+"\n"+features, p.d);
//					}
//				};
//				
//				futures.add(executor.submit(c));
//			}
//		}
//		
//		executor.shutdown();
//		
//		try
//		{
//			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//		}
//		catch (InterruptedException e) {e.printStackTrace();}
//		
//		max = futures.get(0);
//		
//		for (int i=1; i<futures.size(); i++)
//		{
//			future = futures.get(i);
//			
//			if (max.get().d < future.get().d)
//				max = future;
//		}
//		
//		BinUtils.LOG.info(String.format("Final score: %4.2f\n", max.get().d));
//		BinUtils.LOG.info(max.get().o2+"\n");
//		if (s_modelPath != null) saveModel(max.get().o1, s_modelPath);
//	}
//	
//	class TrainTask implements Callable<ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>>>
//	{
//		private List<String> train_files;
//		private String[] feature_files;
//		private String develop_file;
//		private NLPMode nlp_mode;
//		private int dev_index;
//		
//		/** @param currLabel the current label to train. */
//		public TrainTask(List<String> trainFiles, String[] featureFiles, NLPMode mode, int devIndex)
//		{
//			train_files  = trainFiles;
//			develop_file = trainFiles.remove(devIndex);
//			feature_files = featureFiles;
//			dev_index = devIndex;
//			nlp_mode = mode;
//		}
//		
//		public ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> call()
//		{
//			AbstractStatisticalComponent<?,?,?,?> component = train(train_files, Lists.newArrayList(develop_file), feature_files, s_configurationFile, nlp_mode).o;
//			saveModel(component, s_modelPath+"."+dev_index);
//			return null;
//		}
//    }
		
	static public void main(String[] args)
	{
		try 
		{
			new NLPTrain(args);
		}
		catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
	}
}
