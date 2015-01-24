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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.kohsuke.args4j.Option;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.pos.DefaultPOSTagger;
import edu.emory.clir.clearnlp.dependency.DEPFeat;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.nlp.NLPUtils;
import edu.emory.clir.clearnlp.nlp.trainer.AbstractNLPTrainer;
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
	@Option(name="-stop", usage="stopping criteria (optional; development only)", required=false, metaVar="<double>")
	static public double d_stop = 0;
	
	public NLPTrain() {}
	
	public NLPTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		List<String> trainFiles   = FileUtils.getFileList(s_trainPath  , s_trainExt  , false);
		List<String> developFiles = FileUtils.getFileList(s_developPath, s_developExt, false);
		String[]     featureFiles = Splitter.splitColons(s_featureTemplateFile);
		NLPMode      mode         = NLPMode.valueOf(s_mode);
	
		AbstractStatisticalComponent<?,?,?,?> component = train(trainFiles, developFiles, featureFiles, s_configurationFile, mode);
		if (s_modelPath != null) saveModel(component, s_modelPath);
	}
	
	public AbstractStatisticalComponent<?,?,?,?> train(List<String> trainFiles, List<String> developFiles, String[] featureFiles, String configurationFile, NLPMode mode)
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
			out = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(modelPath))));
			component.save(out);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	
	
	
	
	
	void onlineTrain()
	{
		try
		{
			DefaultPOSTagger tagger = new DefaultPOSTagger(new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(s_modelPath)))));
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
		
	static public void main(String[] args)
	{
		new NLPTrain(args);
	}
}
