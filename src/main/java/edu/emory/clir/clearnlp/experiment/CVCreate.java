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
package edu.emory.clir.clearnlp.experiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CVCreate
{
	@Option(name="-t", usage="path to training files (required)", required=true, metaVar="<filepath>")
	protected String s_trainPath;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<regex>")
	protected String s_trainExt = "*";
	@Option(name="-o", usage="path to output files (required)", required=true, metaVar="<filepath>")
	protected String s_outputPath;
	@Option(name="-cv", usage="number of cross-validation sets (default: 10)", required=false, metaVar="<integer>")
	protected int n_cv = 10;
	
	public CVCreate() {}
	
	public CVCreate(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		try
		{
			List<String> trainFiles = FileUtils.getFileList(s_trainPath, s_trainExt, false);
			create(trainFiles, s_outputPath, n_cv);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public void create(List<String> trainFiles, String outputPath, final int N) throws IOException
	{
		PrintStream[] fout = createPrintStreams(outputPath, N);
		BufferedReader fin;
		String line;
		int cv = 0;
		
		for (String trainFile : trainFiles)
		{
			fin = IOUtils.createBufferedReader(trainFile);
			
			while ((line = fin.readLine()) != null)
			{
				fout[cv].println(line);
				
				if (line.trim().equals(StringConst.EMPTY))
					cv = (cv + 1) % N;
			}
			
			fin.close();
		}
		
		for (PrintStream f : fout) f.close();
	}
	
	private PrintStream[] createPrintStreams(String outputPath, final int N)
	{
		PrintStream[] fout = new PrintStream[N];
		
		for (int i=0; i<N; i++)
			fout[i] = IOUtils.createBufferedPrintStream(outputPath+"/"+i+".cv");
		
		return fout;
	}
	
	static public void main(String[] args)
	{
		new CVCreate(args);
	}
}
