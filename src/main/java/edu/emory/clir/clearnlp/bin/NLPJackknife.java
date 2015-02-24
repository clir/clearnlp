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

import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.Option;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPJackknife extends NLPTrain
{
	@Option(name="-bidx", usage="index of the first cv-set (inclusive)", required=true, metaVar="<integer>")
	protected int i_beginIndex;
	@Option(name="-eidx", usage="index of the last cv-set (exclusive)", required=true, metaVar="<integer>")
	protected int i_endIndex;
	
	public NLPJackknife() {}
	
	public NLPJackknife(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		List<String> trainFiles   = FileUtils.getFileList(s_trainPath, s_trainExt, false);
		String[]     featureFiles = Splitter.splitColons(s_featureTemplateFile);
		NLPMode      mode         = NLPMode.valueOf(s_mode);
	
		trainCV(trainFiles, featureFiles, s_configurationFile, mode, i_beginIndex, i_endIndex);
	}
	
	private void trainCV(List<String> trainFiles, String[] featureFiles, String configurationFile, NLPMode mode, int beginIndex, int endIndex)
	{
		AbstractStatisticalComponent<?,?,?,?> component;
		String developFile;
		
		for (int i=beginIndex; i<endIndex; i++)
		{
			Collections.sort(trainFiles);
			developFile = trainFiles.remove(i);
			component = train(trainFiles, Lists.newArrayList(developFile), featureFiles, s_configurationFile, mode);
			saveModel(component, s_modelPath+"/cv"+i+".tgz");
			trainFiles.add(developFile);
		}
	}
	
	static public void main(String[] args)
	{
		new NLPJackknife(args);
	}
}
