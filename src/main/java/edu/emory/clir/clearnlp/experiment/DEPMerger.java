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

import java.io.FileInputStream;
import java.util.List;

import edu.emory.clir.clearnlp.component.mode.dep.DEPEval;
import edu.emory.clir.clearnlp.component.mode.dep.merge.DEPMerge;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.arc.DEPArc;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPMerger
{
	public DEPMerger() throws Exception
	{
		List<String> filenames = FileUtils.getFileList("/Users/jdchoi/Desktop/out/", ".out", false);
		int i, size = filenames.size();
		
		TSVReader gReader = new TSVReader(0, 1, 2, 4, 5, 6, 7);
		TSVReader[] sReaders = new TSVReader[size];
		DEPEval eval = new DEPEval();
		DEPArc[] gHeads;
		DEPMerge merge;
		DEPTree tree;
		
		gReader.open(new FileInputStream("/Users/jdchoi/Desktop/out/gold/gold.out"));
		
		for (i=0; i<size; i++)
		{
			sReaders[i] = new TSVReader(0, 1, 2, 4, 5, 6, 7);
			sReaders[i].open(new FileInputStream(filenames.get(i)));
		}
		
		while ((tree = gReader.next()) != null)
		{
			gHeads = tree.getHeads();
			tree.clearDependencies();
			merge = new DEPMerge(tree);
			
			for (TSVReader sReader : sReaders)
				reset(merge, tree, sReader.next());
			
			merge.merge();
			eval.countCorrect(tree, gHeads);
		}
		
		System.out.println(eval.toString());
	}
	
	private void reset(DEPMerge merge, DEPTree tree1, DEPTree tree2)
	{
		DEPNode node1, node2, head2;
		int i, size = tree1.size();
		
		for (i=1; i<size; i++)
		{
			node1 = tree1.get(i);
			node2 = tree2.get(i);
			head2 = node2.getHead();
			
			if (head2 != null)
				merge.addEdge(node1, tree1.get(head2.getID()), node2.getLabel(), 1);
		}
	}
	
	static public void main(String[] args)
	{
		try {
			new DEPMerger();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
