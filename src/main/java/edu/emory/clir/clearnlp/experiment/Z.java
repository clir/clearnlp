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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

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
//		PBFMap map = new PBFMap(args[0]);
//		ObjectOutputStream out = new ObjectOutputStream(new XZOutputStream(new BufferedOutputStream(new FileOutputStream("tmp.xz")), new LZMA2Options()));
//		out.writeObject(map);
//		out.close();
		
		String filename = "/Users/jdchoi/Documents/Data/experiments/craft-1.1.0/trn.dep";
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6);
		DEPTree tree;
		
		reader.open(IOUtils.createFileInputStream(filename));
		
		while ((tree = reader.next()) != null)
		{
			if (tree.isNonProjective())
				System.out.println("NONP");
		}
		System.out.println("DONE");
	}
	
	class Tmp
	{
		long i;
		
		public void add(int j)
		{
			i += j;
		}
	}
	
	@SuppressWarnings("resource")
	public void amazon(String[] args) throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("/Users/jdchoi/Downloads/Books.txt.gz"))));
		Pattern p = Pattern.compile(" ");
		String[] t;
		String line;
		long l, max = -1, min = Long.MAX_VALUE;
		
		for (long e=1; (line = reader.readLine()) != null; e++)
		{
			line = line.trim();
			if (line.startsWith("review/time:"))
			{
				t = p.split(line);
				l = Long.parseLong(t[1]);
				max = Math.max(l, max);
				min = Math.min(l, min);
			}
			
			if (e%10000000 == 0) System.out.print(".");
		}	System.out.println();
		
		System.out.println(new Date(max*1000).toString());
		System.out.println(new Date(min*1000).toString());
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
	
	static public void main(String[] args)
	{
		try
		{
			new Z(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}