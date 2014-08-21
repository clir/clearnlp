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
package com.clearnlp.bin;

import org.kohsuke.args4j.Option;

import com.clearnlp.util.BinUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class NLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<string>")
	private String s_configurationFile;
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
		
//		try
//		{
//			AbstractTokenizer tokenizer = NLPGetter.getTokenizer(TLanguage.getType(s_language));
//			
//			for (String inputFile : FileUtils.getFileList(s_inputPath, s_inputExt, false))
//			{
//				System.out.println(inputFile);
//				tokenize(tokenizer, inputFile, inputFile+"."+s_outputExt);
//			}
//		}
//		catch (IOException e) {e.printStackTrace();}
	}
	
	public void train(String[] trainFiles)
	{
		
		
	}
	
	static public void main(String[] args)
	{
		new NLPTrain(args);
	}
}
