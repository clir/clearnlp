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
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFFrameset;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFMap;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFRole;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFRoleset;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFType;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.srl.SRLTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
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
		matchPropBankTags();
		
//		String root = "/Users/jdchoi/Desktop/allen/mturk-old/";
//		String f0 = root+"AllenFixedVsOpenFutureRuleOld.csv.0.txt.cnlp";
//		String f1 = root+"AllenFixedVsOpenFutureRuleOld.csv.1.txt.cnlp";
//		String f2 = root+"AllenFixedVsOpenFutureRuleOld.csv.2.txt.cnlp";
//		int i, N = 5;
//		
//		PrintStream[] fout = new PrintStream[N];
//		for (i=0; i<N; i++) fout[i] = IOUtils.createBufferedPrintStream(root+"cv"+i+".tst");
//		
//		crossValidate(f0, fout, N);
//		crossValidate(f1, fout, N);
//		crossValidate(f2, fout, N);
//		for (i=0; i<N; i++) fout[i].close();
	}
	
	public void matchPropBankTags() throws Exception
	{
		String frameDir = "/Users/jdchoi/Downloads/frames";
		String inputFile = "/Users/jdchoi/Documents/Data/experiments/general-en/onto.all";
		
		PBFMap map = new PBFMap(frameDir);
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(IOUtils.createFileInputStream(inputFile));
		Pattern argn = Pattern.compile("^A(\\d)");
		Pattern argm = Pattern.compile("^AM");
		Set<String> set = new HashSet<>();
		String pb, n, lemma;
		PBFRoleset roleset;
		DEPTree tree;
		PBFRole role;
		SRLTree srl;
		
		while ((tree = reader.next()) != null)
		{
			for (DEPNode node : tree)
			{
				pb = node.getFeat(DEPLib.FEAT_PB);
				if (pb == null || pb.endsWith("LV")) continue;
				srl = tree.getSRLTree(node);
				lemma = pb.substring(0, pb.length()-3);
				roleset = map.getRoleset(PBFType.VERB, lemma, pb);
				if (roleset == null) continue;
				
				for (SRLArc arc : srl.getArgumentArcList(argn))
				{
					n = arc.getLabel().substring(1,2);
					role = roleset.getRole(n);
					if (role == null) System.out.println(pb+" "+n+" "+arc.getLabel());
				}
				
				for (SRLArc arc : srl.getArgumentArcList(argm))
					set.add(arc.getLabel());
			}
		}
		
		List<String> list = new ArrayList<>(set);
		Collections.sort(list);
		for (String s : list) System.out.println(s);
	}
	
	void crossValidate(String inputFile, PrintStream[] fout, int N)
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6);
		reader.open(IOUtils.createFileInputStream(inputFile));
		DEPTree tree;
		int i = -1;
		
		while ((tree = reader.next()) != null)
		{
			i = (i+1) % N;
			fout[i].println(tree.toString(DEPNode::toStringNER)+"\n");
		}
		
		reader.close();
	}
	
	public void simplifyTokens(String[] args) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(args[0]);
		PrintStream fout = IOUtils.createBufferedPrintStream(args[0]+".wop");
		Set<String> set = new HashSet<>();
		StringJoiner joiner;
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			joiner = new StringJoiner(" ");
			
			for (String s : Splitter.splitSpace(line))
				if (!StringUtils.containsPunctuationOnly(s))
					joiner.add(StringUtils.toSimplifiedForm(s));
			
			line = StringUtils.toLowerCase(joiner.toString().trim());
			
			if (!line.isEmpty() && !set.contains(line))
			{
				fout.println(line);
				set.add(line);
			}
		}
		
		reader.close();
		fout.close();
	}
	
	public void countPOS(String[] args) throws Exception
	{
		final String inputPath  = args[0];
		final String outputPath = args[1];
		
		ObjectIntHashMap<String> noun = new ObjectIntHashMap<>();
		ObjectIntHashMap<String> verb = new ObjectIntHashMap<>();
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6);
		DEPTree tree;
		
		for (String filename : FileUtils.getFileList(inputPath, "cnlp", false))
		{
			reader.open(IOUtils.createFileInputStream(filename));
			System.out.println(filename);
			
			while ((tree = reader.next()) != null)
			{
				for (DEPNode node : tree)
				{
					if (POSLibEn.isCommonOrProperNoun(node.getPOSTag()))
						noun.add(node.getLemma());
					else if (POSLibEn.isVerb(node.getPOSTag()))
						verb.add(node.getLemma());
				}
			}
			
			reader.close();
		}
		
		print(verb, outputPath+".verb");
		print(noun, outputPath+".noun");
	}
	
	private void print(ObjectIntHashMap<String> map, String outputFile)
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
		List<ObjectIntPair<String>> list = map.toList();
		Collections.sort(list);
		for (ObjectIntPair<String> p : list) fout.println(p.o+"\t"+p.i);
		fout.close();
	}
	
	public void extractARGM() throws Exception
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