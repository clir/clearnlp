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

import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import edu.emory.clir.clearnlp.component.mode.future.AbstractFutureClassifier;
import edu.emory.clir.clearnlp.component.mode.future.DefaultFutureClassifier;
import edu.emory.clir.clearnlp.component.mode.future.FCEval;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FutureDecode
{
	public FutureDecode(String[] args)
	{
		final String INPUT_PATH = args[0];
		final String MODEL_FILE = args[1];
		
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(MODEL_FILE);
		List<String> inputFiles = FileUtils.getFileList(INPUT_PATH, "cnlp", false);
		AbstractFutureClassifier fc = new DefaultFutureClassifier(in);
		Collections.sort(inputFiles);
		
		for (String inputFile : inputFiles)
		{
			System.out.print(FileUtils.getBaseName(inputFile)+"\t");
			count(fc, inputFile);
		}
	}
	
	public void count(AbstractFutureClassifier fc, String inputFile)
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6);
		Integer[] count = {0,0,0};
		int f, total = 0;
		DEPTree tree;
		DEPNode node;
		
		reader.open(IOUtils.createFileInputStream(inputFile));
		PrintStream fout = IOUtils.createBufferedPrintStream(inputFile+".fut");
		
		while ((tree = reader.next()) != null)
		{		
			fc.process(tree);
			node = tree.get(FCEval.INFO_NODE);
			f = Integer.parseInt(node.getFeat(DEPLib.FEAT_FUTURE));
			fout.println(tree.join(DEPNode::getWordForm, " ", 0, tree.size())+"\t"+f);
			count[f]++;
			total++;
		}
		
		System.out.println(Joiner.join(count, "\t")+"\t"+total);
		reader.clone();
		fout.close();
	}
	
	static public void main(String[] args)
	{
		new FutureDecode(args);
	}
}
