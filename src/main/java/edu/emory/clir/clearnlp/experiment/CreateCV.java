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
package edu.emory.clir.clearnlp.experiment;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CreateCV
{
	@Option(name="-t", usage="path to training files (required)", required=true, metaVar="<filepath>")
	private String s_trainPath;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<regex>")
	private String s_trainExt = "*";
	@Option(name="-o", usage="path to output files (required)", required=true, metaVar="<filepath>")
	private String s_outputPath;
	@Option(name="-cv", usage="humber of cross-validation sets (default: 10)", required=false, metaVar="<integer>")
	private int n_cv = 10;
	
	public CreateCV() {}
	
	public CreateCV(String[] args)
	{
		BinUtils.initArgs(args, this);
		List<String> trainFiles = FileUtils.getFileList(s_trainPath, s_trainExt, false);
		splitCrossValidationSets(trainFiles, s_outputPath, n_cv);
	}
	
	public void splitCrossValidationSets(List<String> trainFiles, String outputPath, int n)
	{
		PrintStream[] fout = new PrintStream[n];
		BufferedReader fin;
		String tree;
		int i;
		
		BinUtils.LOG.info("Generating cross-validation sets:\n");
		
		for (i=0; i<n; i++)
			fout[i] = IOUtils.createBufferedPrintStream(outputPath+"/"+i+".cv");
		
		try
		{
			i = -1;
			
			for (String trainFile : trainFiles)
			{
				BinUtils.LOG.info(".");
				fin = IOUtils.createBufferedReader(trainFile);
				
				while ((tree = next(fin)) != null)
					fout[++i%n].println(tree);
			}	
		}
		catch (Exception e) {e.printStackTrace();}
		for (i=0; i<n; i++) fout[i].close();
		BinUtils.LOG.info("\n\n");
	}
	
	private String next(BufferedReader fin) throws Exception
	{
		StringBuilder build = new StringBuilder();
		String line;
		
		while ((line = fin.readLine()) != null)
		{
			line = line.trim();
			
			if (line.equals(StringConst.EMPTY))
			{
				if (build.length() == 0) continue;
				else break;
			}
			else
			{
				build.append(line);
				build.append(StringConst.NEW_LINE);
			}
		}
		
		return (build.length() == 0) ? null : build.toString();
	}
	
	static public void main(String[] args)
	{
		new CreateCV(args);
	}
}
