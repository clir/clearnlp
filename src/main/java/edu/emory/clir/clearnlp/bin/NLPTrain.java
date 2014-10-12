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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.nlp.trainer.AbstractNLPTrainer;
import edu.emory.clir.clearnlp.nlp.trainer.POSTrainer;
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
	private String s_configurationFile;
	@Option(name="-f", usage="feature template files (required, delimited by "+"\":\""+")", required=true, metaVar="<string>")
	private String s_featureTemplateFile;
	@Option(name="-t", usage="path to training files (required)", required=true, metaVar="<filepath>")
	private String s_trainPath;
	@Option(name="-te", usage="training file extension (default: .*)", required=false, metaVar="<regex>")
	private String s_trainExt = ".*";
	@Option(name="-d", usage="path to development files (optional)", required=true, metaVar="<filepath>")
	private String s_developPath;
	@Option(name="-de", usage="development file extension (default: .*)", required=false, metaVar="<regex>")
	private String s_developExt = ".*";
	@Option(name="-m", usage="model file (optional)", required=false, metaVar="<filename>")
	private String s_modelFile = null;
	@Option(name="-mode", usage="pos|dep|srl", required=true, metaVar="<string>")
	private String s_mode = ".*";
	
	public NLPTrain() {}
	
	public NLPTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
		NLPMode mode = NLPMode.valueOf(s_mode);
		List<String> trainFiles   = FileUtils.getFileList(s_trainPath  , s_trainExt  , false);
		List<String> developFiles = FileUtils.getFileList(s_developPath, s_developExt, false);
		
		AbstractStatisticalComponent<?,?,?,?> component = train(mode, s_configurationFile, Splitter.splitColons(s_featureTemplateFile), trainFiles, developFiles);
		if (s_modelFile != null) saveModel(component, s_modelFile);
	}
	
	public AbstractStatisticalComponent<?,?,?,?> train(NLPMode mode, String configurationFile, String[] featureFiles, List<String> trainFiles, List<String> developFiles)
	{
		
		InputStream configuration  = IOUtils.createFileInputStream(configurationFile);
		InputStream[] features     = IOUtils.createFileInputStreams(featureFiles);
		AbstractNLPTrainer trainer = getTrainer(mode, configuration, features);
		AbstractStatisticalComponent<?,?,?,?> component;
		
		component = trainer.collect(trainFiles);
		Object[] lexicons = component.getLexicons();
		
		return trainer.train(trainFiles, trainFiles, lexicons);
	}
	
	public void saveModel(AbstractStatisticalComponent<?,?,?,?> component, String modelFile)
	{
		ObjectOutputStream out;
		
		try
		{
			out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(s_modelFile)));
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	private AbstractNLPTrainer getTrainer(NLPMode mode, InputStream configuration, InputStream[] features)
	{
		switch (mode)
		{
		case pos: return new POSTrainer(configuration, features);
		case dep: return null;
		case srl: return null;
		default :throw new IllegalArgumentException("Invalid mode: "+mode.toString()); 
		}
	}
		
	static public void main(String[] args)
	{
		new NLPTrain(args);
	}
}
