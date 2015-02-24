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
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import edu.emory.clir.clearnlp.bin.NLPDecode;
import edu.emory.clir.clearnlp.component.mode.dep.state.DEPState;
import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.nlp.configuration.DecodeConfiguration;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPMerge extends NLPDecode
{
	static public int t_count = 0;
	public NLPMerge() {}
	
	public NLPMerge(String[] args)
	{
		BinUtils.initArgs(args, this);
		NLPMode mode = NLPMode.valueOf(s_mode);
		List<String> inputFiles = FileUtils.getFileList(s_inputPath, s_inputExt, false);
		DecodeConfiguration config = new DecodeConfiguration(IOUtils.createFileInputStream(s_configurationFile));
		decode(inputFiles, s_outputExt, config, mode);
		
		try
		{
			merge(inputFiles, s_outputExt, mode);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@SuppressWarnings("incomplete-switch")
	public void merge(List<String> inputFiles, String outputExt, NLPMode mode) throws Exception
	{
		int[] eval = getEval(mode);
		BufferedReader ing, ins;
		PrintStream out;
		List<String> tg;
		String[] ts;
		String line;
		File file;
		
		for (String inputFile : inputFiles)
		{
			file = new File(inputFile + StringConst.PERIOD + outputExt);
			ing = IOUtils.createBufferedReader(inputFile);
			ins = IOUtils.createBufferedReader(file);
			out = IOUtils.createBufferedPrintStream(inputFile + StringConst.PERIOD + mode);
		
			while ((line = ing.readLine()) != null)
			{
				tg = Splitter.splitTabsToList(line);
				ts = Splitter.splitTabs(ins.readLine());
				
				if (tg.size() > 1)
				{
					switch (mode)
					{
					case morph: evaluatePOS(tg, ts, eval); break;
					case dep  : evaluateDEP(tg, ts, eval); break;
					}
				}
				
				out.println(Joiner.join(tg, StringConst.TAB));
			}
			
			ing.close();
			ins.close();
			out.close();
			file.delete();
		}
		
		for (int i=1; i<eval.length; i++)
			BinUtils.LOG.info(String.format("%5.2f (%d/%d)\n", 100d*eval[i]/eval[0], eval[i], eval[0]));
	}
	
	private int[] getEval(NLPMode mode)
	{
		switch (mode)
		{
		case morph: return new int[2];
		case dep  : return new int[3];
		default   : throw new IllegalArgumentException("Invalid mode: "+mode);
		}
	}
	
	private void evaluatePOS(List<String> tg, String[] ts, int[] eval)
	{
		tg.add(3, ts[1]);	// lemma
		tg.add(5, ts[2]);	// pos tag
		tg.add(7, ts[3]);	// feats
		
		eval[0]++;
		if (tg.get(4).equals(tg.get(5))) eval[1]++;
	}
	
	private void evaluateDEP(List<String> tg, String[] ts, int[] eval)
	{
		if (StringUtils.containsPunctuationOnly(tg.get(2))) return;
		tg.add(9 , ts[5]);
		tg.add(11, ts[6]);
		
		if (ts[5].equals("_")) t_count++;
		eval[0]++;
		
		if (tg.get(8).equals(tg.get(9)))
		{
			eval[1]++;
			if (tg.get(10).equals(tg.get(11))) eval[2]++;
		}
	}
	
	static public void main(String[] args)
	{
		new NLPMerge(args);
		System.out.println(t_count);
		System.out.printf("%5.2f (%d/%d)\n", 100d*DEPState.n_trans2/DEPState.n_trans1, DEPState.n_trans1, DEPState.n_trans2);
	}
}
