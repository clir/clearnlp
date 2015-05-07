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
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.constituent.CTNode;
import edu.emory.clir.clearnlp.constituent.CTReader;
import edu.emory.clir.clearnlp.constituent.CTTree;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFFrameset;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFMap;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFRole;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFRoleset;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFType;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.arc.SRLArc;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Z
{
	public Z(String[] args) throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		DEPTree tree;
		
		reader.open(IOUtils.createFileInputStream("/Users/jdchoi/Documents/Data/general/english/onto.dep"));
		
		while ((tree = reader.next()) != null)
		{
			for (DEPNode node : tree)
			{
				for (SRLArc arc : node.getSemanticHeadArcList())
				{
					if (arc.getLabel().startsWith("AM"))
						map.add(arc.getLabel().substring(3));
				}
			}
		}
		
		List<ObjectIntPair<String>> ps = map.toList();
		Collections.sort(ps, Collections.reverseOrder());
		
		for (ObjectIntPair<String> p : ps)
			System.out.println(p.o+" "+p.i);
	}
	
	public void extractPBFunctionTags() throws Exception
	{
		String dir = "/Users/jdchoi/Downloads/frames";
		PBFMap map = new PBFMap(dir);
		
		Map<String,PBFFrameset> framesets = map.getFramesetMap(PBFType.VERB);
		ObjectIntHashMap<String> argn = new ObjectIntHashMap<>();
		ObjectIntHashMap<String> argm = new ObjectIntHashMap<>();
		String f;
		
		for (PBFFrameset frameset : framesets.values())
		{
			for (PBFRoleset roleset : frameset.getRolesets())
			{
				for (PBFRole role : roleset.getRoles())
				{
					f = role.getFunctionTag();
					if (f.isEmpty()) continue;
							
					if (StringUtils.containsDigitOnly(role.getArgumentNumber()))
						argn.add(f.toUpperCase());
					else
						argm.add(f.toUpperCase());
				}
			}
		}

		List<ObjectIntPair<String>> ps = argn.toList();
		Collections.sort(ps, Collections.reverseOrder());
		
		System.out.println("ARGN ----------");
		for (ObjectIntPair<String> p : ps)
			System.out.println(p.o+" "+p.i);
		
		ps = argm.toList();
		Collections.sort(ps, Collections.reverseOrder());
		
		System.out.println("ARGM ----------");
		for (ObjectIntPair<String> p : ps)
			System.out.println(p.o+" "+p.i);
	}
	
	public void printRaw(String[] args) throws Exception
	{
		String filename = "/Users/jdchoi/Documents/Data/general/google.parse";
		CTReader reader = new CTReader(new FileInputStream(filename));
		PrintStream fout = IOUtils.createBufferedPrintStream(filename+".raw");
		StringJoiner joiner;
		CTTree tree;
		
		while ((tree = reader.nextTree()) != null)
		{
			joiner = new StringJoiner(" ");
			
			for (CTNode node : tree.getTokenList())
				joiner.add(node.getWordForm());
			
			fout.println(joiner.toString());
		}
		
		fout.close();
	}
	
	class Tmp
	{
		long i;
		
		public void add(int j)
		{
			i += j;
		}
	}
	
	public void frameset(String[] args) throws Exception
	{
//		PBFMap map = new PBFMap(args[0]);
//		ObjectOutputStream out = new ObjectOutputStream(IOUtils.createXZBufferedOutputStream(args[1]));
//		out.writeObject(map);
//		out.close();
		
		ObjectInputStream in = new ObjectInputStream(IOUtils.createXZBufferedInputStream(args[0]));
		PBFMap map = (PBFMap)in.readObject();
		
		Map<String,PBFFrameset> framesets = map.getFramesetMap(PBFType.VERB);
		List<Set<String>> argns = new ArrayList<>();
		List<String> list; int n;
		for (n=0; n<6; n++) argns.add(new HashSet<String>());
		Set<String> rolesets = new HashSet<>();
//		int count;
		final int N = Integer.parseInt(args[1]);
		final String TAG = args.length > 2 ? args[2] : "";
		
		for (PBFFrameset frameset : framesets.values())
		{
			for (PBFRoleset roleset : frameset.getRolesets())
			{
//				count = 0;
				
				for (PBFRole role : roleset.getRoles())
				{
					if (StringUtils.containsDigitOnly(role.getArgumentNumber()))
					{
						n = Integer.parseInt(role.getArgumentNumber());
						argns.get(n).add(role.getFunctionTag());
						
//						if (n == 0 && role.isFunctionTag("PPT")) count++;
//						if (n == 1 && role.isFunctionTag("PAG")) count++;
//						if (n == N && role.isFunctionTag(TAG)) rolesets.add(roleset.getID());
//						if (role.isFunctionTag(TAG)) rolesets.add(roleset.getID());
						if (n == N && !role.isFunctionTag(TAG)) rolesets.add(roleset.getID());
//						if (role.isFunctionTag(TAG)) count++;
					}
				}
				
//				if (count > 1) rolesets.add(roleset.getID());
			}
		}
		
//		for (n=0; n<argns.size(); n++)
//		{
//			list = new ArrayList<>(argns.get(n));
//			Collections.sort(list);
//			System.out.println(n+" "+list.toString());
//		}
		
		list = new ArrayList<>(rolesets);
		Collections.sort(list);
		for (String s : list) System.out.println(s);
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