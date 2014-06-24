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
package com.clearnlp.experiment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Pattern;

import org.kohsuke.args4j.Option;

import com.clearnlp.collection.map.ObjectIntHashMap;
import com.clearnlp.collection.pair.ObjectIntPair;
import com.clearnlp.util.BinUtils;
import com.clearnlp.util.CharTokenizer;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.FileUtils;
import com.clearnlp.util.Joiner;
import com.clearnlp.util.Splitter;
import com.clearnlp.util.constant.CharConst;
import com.clearnlp.util.constant.StringConst;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class Kaist2CoNLL
{
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String s_inputPath;
	@Option(name="-ie", usage="input file extension (default: .*)", required=false, metaVar="<regex>")
	private String s_inputExt = ".*";
	@Option(name="-oe", usage="output file extension (required)", required=true, metaVar="<string>")
	private String s_outputExt;
	@Option(name="-ir", usage="if set, process all files under the input path recursively.", required=false, metaVar="<boolean>")
	private boolean b_recursive = false;
	@Option(name="-src", usage="encoding of source files (default: euc-kr)", required=false, metaVar="<string>")
	private String s_source = "euc-kr";
	@Option(name="-trg", usage="encoding of target files (default: utf8)", required=false, metaVar="<string>")
	private String s_target = "utf8";
	
	private final String S_REPL = "_P_";
	private final String S_SLASH = "\\//sp";
	private final CharTokenizer T_PLUS = new CharTokenizer(CharConst.PLUS);
	private final Pattern P_PLUS = Pattern.compile("\\\\\\+");
	private final Pattern P_REPL = Pattern.compile(S_REPL);
	
	
	public Kaist2CoNLL(String[] args)
	{
		BinUtils.initArgs(args, this);
		
		try
		{
			encode(s_inputPath, s_inputExt, s_outputExt, b_recursive, s_source, s_target);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public void encode(String inputPath, String inputExtension, String outputExtension, boolean recursive, String source, String target) throws IOException
	{
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		List<String> list = Lists.newArrayList();
		StringBuilder build;
		BufferedReader fin;
		String line, conll;
		PrintStream fout;
		int total = 0;
		
		for (String inputFile : FileUtils.getFileList(inputPath, inputExtension, recursive))
		{
			fin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), source));
			build = new StringBuilder();
			
			while ((line = fin.readLine()) != null)
			{
				line = line.trim();
				
				if (line.isEmpty())
				{					
					conll = toCoNLL(list, map);
					
					if (conll != null)
					{
						build.append(conll);
						build.append(StringConst.NEW_LINE);
						total += list.size();
					}
					
					list = Lists.newArrayList();
				}
				else
					list.add(line);
			}
			
			fin.close();
			
			if (build.length() > 0)
			{
				fout = new PrintStream(new BufferedOutputStream(new FileOutputStream(inputFile+"."+outputExtension), 65536), false, target);
				fout.println(build.toString());
				fout.close();				
			}
			else
				System.err.println("Empty file: "+inputFile);
		}
		
		List<ObjectIntPair<String>> ls = map.toList();
		DSUtils.sortReverseOrder(ls);
		
		System.out.println("WC: "+total);
		for (ObjectIntPair<String> p : ls)
			System.out.println(p.o+" "+p.i);
	}
	
	private String toCoNLL(List<String> list, ObjectIntHashMap<String> gmap)
	{
		if (list.isEmpty()) return null;
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		StringBuilder build = new StringBuilder();
		int i, size = list.size();
		String conll;
		
		for (i=0; i<size; i++)
		{
			 conll = toCoNLL(map, list.get(i), i+1);
			 if (conll == null) return null;
			 build.append(conll);
			 build.append(StringConst.NEW_LINE);
		}
		
		for (ObjectIntPair<String> p : map) gmap.add(p.o, p.i);
		return build.toString();
	}
	
	private String toCoNLL(ObjectIntHashMap<String> map, String line, int id)
	{
		List<String> lemma = Lists.newArrayList();
		List<String> pos   = Lists.newArrayList();
		String[] t = Splitter.splitTabs(line);
		String form, morph, m, p;
		int idx;
		
		if (t.length < 2)
		{
			if (line.equals(S_SLASH))
			{
				t = new String[]{StringConst.FW_SLASH, S_SLASH};
			}
			else
			{
				System.err.println("Incomplete: "+id+" "+line);
				return null;				
			}
		}
		
		form  = t[0];
		morph = P_PLUS.matcher(t[1]).replaceAll(S_REPL);
		
		for (String s : T_PLUS.tokenize(morph))
		{
			idx = s.lastIndexOf(CharConst.FW_SLASH);
			if (idx <= 0 || idx+1 >= s.length()) return null;
			
			m = P_REPL.matcher(s.substring(0, idx)).replaceAll("\\"+StringConst.PLUS);
			p = s.substring(idx+1);
			if (p.equals("eff")) p = "ef";
			
			lemma.add(m);
			pos.add(p);
			map.add(p);
		}
		
		if (lemma.isEmpty())
		{
			System.err.println("Empty: "+id+" "+line);
			return null;
		}
		
		StringBuilder build = new StringBuilder();

		build.append(id);
		build.append(StringConst.TAB);
		
		build.append(form);
		build.append(StringConst.TAB);
		
		build.append(Joiner.join(lemma, StringConst.PLUS));
		build.append(StringConst.TAB);
		
		build.append(Joiner.join(pos, StringConst.PLUS));
		build.append(StringConst.TAB);
		
		build.append("_\t_\t_\t_\t_");
		return build.toString();
	}
	
	static public void main(String[] args)
	{
		new Kaist2CoNLL(args);
	}
}
