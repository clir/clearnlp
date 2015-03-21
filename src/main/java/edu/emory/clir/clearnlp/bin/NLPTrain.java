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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.trainer.AbstractNLPTrainer;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	protected String s_configurationFile;
	@Option(name="-f", usage="feature template files (required)", required=true, metaVar="<filename>", handler=StringArrayOptionHandler.class)
	protected String[] s_featureFiles;
	@Option(name="-m", usage="model filename (optional)", required=false, metaVar="<filename>")
	protected String s_modelPath = null;
	@Option(name="-t", usage="training path (required)", required=true, metaVar="<filepath>")
	protected String s_trainPath;
	@Option(name="-d", usage="development path (required)", required=true, metaVar="<filepath>")
	protected String s_developPath;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<string>")
	protected String s_trainExt = "*";
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	protected String s_developExt = "*";
	@Option(name="-mode", usage="pos|dep|srl", required=true, metaVar="<mode>")
	protected String s_mode = ".*";
//	@Option(name="-threads", usage="number of threads (default: 1)", required=false, metaVar="<Integer>")
//	protected int n_threads = 1;
	
	public NLPTrain() {}
	
	public NLPTrain(String[] args) throws InterruptedException, ExecutionException
	{
		BinUtils.initArgs(args, this);
		
		List<String> trainFiles   = FileUtils.getFileList(s_trainPath  , s_trainExt  , false);
		List<String> developFiles = FileUtils.getFileList(s_developPath, s_developExt, false);
		NLPMode      mode         = NLPMode.valueOf(s_mode);

		ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> p = train(trainFiles, developFiles, s_featureFiles, s_configurationFile, mode);
		BinUtils.LOG.info(String.format("Final score: %4.2f\n", p.d));
		if (s_modelPath != null) saveModel(p.o, s_modelPath);
	}
	
//	public NLPTrain(String[] args) throws InterruptedException, ExecutionException
//	{
//		BinUtils.initArgs(args, this);
//		
//		List<String> trainFiles   = FileUtils.getFileList(s_trainPath  , s_trainExt  , false);
//		List<String> developFiles = FileUtils.getFileList(s_developPath, s_developExt, false);
//		NLPMode      mode         = NLPMode.valueOf(s_mode);
//
//		List<Callable<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>>> tasks = new ArrayList<>();
//		Callable<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>> c;
//		ExecutorService executor = Executors.newFixedThreadPool(n_threads);
//		;
//		for (String configurationFile : s_configurationFiles)
//		{
//			for (String featureFile : s_featureFiles)
//			{
//				System.out.println(featureFile);
//				
//				c = new Callable<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>>()
//				{
//					@Override
//					public ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String> call() throws Exception
//					{
//						final ObjectDoublePair<AbstractStatisticalComponent<?,?,?,?>> p = train(trainFiles, developFiles, Splitter.splitColons(featureFile), configurationFile, mode);
//						return new ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>(p.o, FileUtils.getBaseName(configurationFile)+", "+FileUtils.getBaseName(featureFile), p.d);
//					}
//				};
//				
//				tasks.add(c);
//			}
//		}
//		
//		List<Future<ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String>>> futures = executor.invokeAll(tasks);
//		ObjectObjectDoubleTriple<AbstractStatisticalComponent<?,?,?,?>,String> max = null, t;
//		int i, size = futures.size();
//		
//		for (i=0; i<size; i++)
//		{
//			t = futures.get(i).get();
//			System.out.printf("%s: %5.2f\n", t.o2, t.d);
//			if (max == null || max.compareTo(t) < 0) max = t;
//		}
//		
//		executor.shutdown();
//		if (size > 1) BinUtils.LOG.info(String.format("Best\n%s: %5.2f\n", max.o2, max.d));
//		if (s_modelPath != null) saveModel(max.o1, s_modelPath);
//	}
	
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
	
	
	
	
	
	
//	void onlineTrain()
//	{
//		try
//		{
//			DefaultPOSTagger tagger = new DefaultPOSTagger(new ObjectInputStream(new XZInputStream(new BufferedInputStream(new FileInputStream(s_modelPath)))));
//			for (DEPTree tree : getTrees())
//			{
//				tagger.process(tree);
//				System.out.println(tree.toStringPOS()+"\n");
//			}
//			tagger.onlineTrain(getTrees());
//			System.out.println("---------------------------\n");
//			for (DEPTree tree : getTrees())
//			{
//				tagger.process(tree);
//				System.out.println(tree.toStringPOS()+"\n");
//			}
//		}
//		catch (Exception e) {e.printStackTrace();}
//	}
//	
//	private List<DEPTree> getTrees()
//	{
//		List<DEPTree> list = Lists.newArrayList();
//		DEPTree tree;
//		
//		tree = new DEPTree(5);
//		tree.add(new DEPNode(1, "mr.", "NNP", new DEPFeat()));
//		tree.add(new DEPNode(2, "boom", "NNP", new DEPFeat()));
//		tree.add(new DEPNode(3, "toissed", "VBD", new DEPFeat()));
//		tree.add(new DEPNode(4, "paat", "JJ", new DEPFeat()));
//		tree.add(new DEPNode(5, "balll", "NN", new DEPFeat()));
//		list.add(tree);
//		
//		tree = new DEPTree(4);
//		tree.add(new DEPNode(1, "John", "NNP", new DEPFeat()));
//		tree.add(new DEPNode(2, "bought", "VBD", new DEPFeat()));
//		tree.add(new DEPNode(3, "a", "DT", new DEPFeat()));
//		tree.add(new DEPNode(4, "car", "NN", new DEPFeat()));
//		list.add(tree);
//		
//		return list;
//	}
	
	static public void main(String[] args)
	{
		try 
		{
			new NLPTrain(args);
		}
		catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
	}
}
