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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magicwerk.brownies.collections.primitive.IntGapList;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.component.mode.dep.DEPFeatureExtractor;
import edu.emory.clir.clearnlp.component.mode.dep.DefaultDEPParser;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.IOUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Z
{
	public Z(String[] args) throws Exception
	{
		test();
	}
	
	public void test()
	{
		Map<Integer,Double> map;
		
		int i, j, len = 100, size = 1000000;
		long st, et;
		
		map = new HashMap<>();
		for (j=0; j<len; j++) map.put(j, (double)j);
		st = System.currentTimeMillis();

		for (i=0; i<size; i++)
		{
			for (j=0; j<len; j++)
				map.compute(j, (k, v) -> v /len);
		}
		
		et = System.currentTimeMillis();
		System.out.println(et-st);
		
		map = new HashMap<>();
		for (j=0; j<len; j++) map.put(j, (double)j);
		st = System.currentTimeMillis();

		for (i=0; i<size; i++)
		{
			for (j=0; j<len; j++)
				map.computeIfPresent(j, (k, v) -> v /len);
		}
		
		et = System.currentTimeMillis();
		System.out.println(et-st);
	}
	
	public void readDEP(String[] args) throws Exception
	{
		String featureFile = args[0];
		String inputFile   = args[1];
//		String outputFile  = args[2];
		
		DEPFeatureExtractor df = new DEPFeatureExtractor(IOUtils.createFileInputStream(featureFile));
		DefaultDEPParser parser = new DefaultDEPParser(new DEPFeatureExtractor[]{df}, null);
//		PrintStream fout   = IOUtils.createBufferedPrintStream(outputFile);
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6);
		reader.open(IOUtils.createFileInputStream(inputFile));
//		DEPState state;
		DEPTree tree;
		int i;
		
		for (i=0; (tree = reader.next()) != null; i++)
		{
			parser.process(tree);
//			fout.println(tree.toStringDEP()+"\n");
			if ((i+1)%10000 == 0) System.out.print(".");
		}
		
		reader.close();
//		fout.close();
		System.out.println();
	}
	
	void compareAddAll()
	{
		List<Integer> tmp = Lists.newArrayList(0,1,2,3,4,5);
		int i, j, size = 1000000;
		List<Integer> list;
		long st, et;
		
		st = System.currentTimeMillis();
		
		for (i=0; i<size; i++)
		{
			list = new ArrayList<>(tmp);
			for (j=5; j>=0; j--)
				list.remove(j);
		}
		
		et = System.currentTimeMillis();
		System.out.println(et-st);
		
		st = System.currentTimeMillis();
		
		for (i=0; i<size; i++)
		{
			list = new ArrayList<>(tmp);
			list.clear();
		}
		
		et = System.currentTimeMillis();
		System.out.println(et-st);
	}
	
	void compare(String[] args)
	{
		int i, j, size = 1000000;
		IntGapList glist;
		IntArrayList alist;
		long st, et;
		
		st = System.currentTimeMillis();
		
		for (i=0; i<size; i++)
		{
			alist = new IntArrayList();
			for (j=0; j<20; j++) alist.add(0, j);
//			while (!alist.isEmpty()) alist.remove(0);
		}
		
		et = System.currentTimeMillis();
		System.out.println(et-st);
		
		st = System.currentTimeMillis();
		
		for (i=0; i<size; i++)
		{
			glist = new IntGapList();
			for (j=0; j<20; j++) glist.add(0, j);
//			while (!glist.isEmpty()) glist.remove(0);
		}
		
		et = System.currentTimeMillis();
		System.out.println(et-st);
		
		
	}
	
	static public void main(String[] args)
	{
		try
		{
			new Z(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}