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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.clearnlp.collection.list.DoubleArrayList;
import com.clearnlp.collection.map.ObjectIntHashMap;
import com.clearnlp.collection.map.StringIntMinimalPerfectHashMap;
import com.clearnlp.constant.StringConst;
import com.clearnlp.constituent.CTNode;
import com.clearnlp.constituent.CTReader;
import com.clearnlp.constituent.CTTree;
import com.clearnlp.dictionary.DTPath;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.IOUtils;
import com.clearnlp.util.pair.ObjectIntPair;
import com.clearnlp.util.pair.Pair;
import com.clearnlp.util.triple.ObjectIntIntTriple;
import com.clearnlp.wordnet.WNMap;
import com.clearnlp.wordnet.WNPOSTag;
import com.clearnlp.wordnet.WNSynset;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class Z
{
	public Z(String[] args) throws Exception
	{
//		int i = 0, size = 20000000;
//		long st, et;
//
//		st = System.currentTimeMillis();
//		for (i=0; i<size; i++)
//		{
//		}
//		et = System.currentTimeMillis();
//		System.out.println(et-st);
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("tmp.obj"));
		Object[] arr = null;
		
		out.writeObject(arr);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("tmp.obj"));
		arr = (Object[])in.readObject();
		in.close();
		
		System.out.println(arr == null);
	}
	
	public void emoticon(String[] args) throws Exception
	{
		Set<String> set = DSUtils.createStringHashSet(IOUtils.getInputStreamsFromClasspath(DTPath.EMOTICONS), true, true);
		Pattern e0 = Pattern.compile("^[\\!\\|;:#%][-]*[\\(\\)\\[\\]\\{\\}\\|<>]+$");
		int count = set.size();
		List<String> list = Lists.newArrayList(set);
		Collections.sort(list);
		
		for (String s : list)
		{
			if (e0.matcher(s).find())
			{
				System.out.println(s);
				set.remove(s);
			}
		}
		
		System.out.println(count+" -> "+set.size());
		
		list = Lists.newArrayList(set);
		Collections.sort(list);
		
		for (String s : list)
			System.out.println(s);
	}
	
	public void minimalHash(String[] args) throws Exception
	{
		Set<String> set = DSUtils.createStringHashSet(IOUtils.createFileInputStream(args[0]), true, false);
		StringIntMinimalPerfectHashMap map = new StringIntMinimalPerfectHashMap();
		ObjectIntHashMap<String> kmap = new ObjectIntHashMap<>();
		int min = Integer.MAX_VALUE, max = -1;
		int h1;
		
		for (String s : set)
			map.addkey(s);
		
		map.initHashFunction();
		
		for (String s : set)
		{
			h1 = map.lookup(s);
			max  = Math.max(max, h1);
			min  = Math.min(min, h1);
			kmap.put(s, h1);
		}
		
		System.out.println(min+" "+max+" "+kmap.size());
		
		int i, size = 1000;
		long st, et;

		st = System.currentTimeMillis();
		for (i=0; i<size; i++)
		{
			for (String s : set)
			{
				kmap.get(s);
				map.lookup(s);
			}
		}
		et = System.currentTimeMillis();
		System.out.println(et-st);
	}
	
	class StringList extends ArrayList<String> implements Comparable<StringList>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public int compareTo(StringList list)
		{
			return size() - list.size();
		}
	}
	
	void printPattern0(Pattern pattern, int index, String s)
	{
		StringBuffer sb = new StringBuffer();
		Matcher m = pattern.matcher(s);
		int i, size = m.groupCount();
		String t;
		
		m.find();

		for (i=1; i<index; i++)
		{
			t = m.group(i);
			if (t != null) sb.append(t);
		}
		
		m.appendReplacement(sb, StringConst.SPACE);
		
		for (i=index; i<=size; i++)
		{
			t = m.group(i);
			if (t != null) sb.append(t);
		}
		
		m.appendTail(sb);
		System.out.println("["+s +"] -> ["+sb.toString()+"]");
	}
	
	void printPattern1(Pattern pattern, int index, String s)
	{
		StringBuffer sb = new StringBuffer();
		Matcher m = pattern.matcher(s);
		int i, size = m.groupCount();
		String t;
		
		m.find();
		sb.append(s.substring(0, m.start()));

		for (i=1; i<=index; i++)
		{
			t = m.group(i);
			if (t != null) sb.append(t);
		}
		
		sb.append(StringConst.SPACE);
		
		for (i=index+1; i<=size; i++)
		{
			t = m.group(i);
			if (t != null) sb.append(t);
		}
		
		System.out.println("["+s +"] -> ["+sb.toString()+"]");
	}
	
	public void extractPOSs(String[] args) throws Exception
	{
		CTReader reader = new CTReader(IOUtils.createFileInputStream(args[0]));
		Set<String> set = Sets.newHashSet();
		CTTree tree;
		
		while ((tree = reader.nextTree()) != null)
			extract(set, tree.getRoot(), "ADD");
		
		reader.close();
		
		for (String s : set)
			System.out.println(s);
		
//		PrintStream out = IOUtils.createBufferedPrintStream(args[1]);
//		List<String> list = Lists.newArrayList(set);
//		Collections.sort(list);
//		Matcher m;
//		String sm;
//		
//		for (String s : list)
//		{
//			m = PatternConst.URL.matcher(s);
//			sm = m.find() ? m.group() : null;
//			
//			if (sm == null)
//			{
//				out.println(s);
//				out.println(sm);
//				out.println();
//			}
//		}
//		
//		out.close();
	}
	
	private void extract(Set<String> set, CTNode node, String pos)
	{
		if (node.isTerminal())
		{
			if (node.isWordForm(","))
				set.add(node.getConstituentTag());
		}
		else
		{
			for (CTNode child : node.getChildrenList())
				extract(set, child, pos);
		}
	}
	
	public double getWeight(DoubleArrayList weight, int index)
	{
		return weight == null ? 1d : weight.get(index);
	}
	
	public List<String> split(String s, char delim)
	{
		List<String> list = Lists.newArrayList();
		int pIdx, cIdx = -1, len = s.length();
		String t;
		
		while (true)
		{
			pIdx = cIdx + 1;
			cIdx = s.indexOf(delim, pIdx);
			
			if (cIdx < 0)
			{
				t = s.substring(pIdx, len);
				if (!t.isEmpty()) list.add(t);
				break;
			}
			else
			{
				t = s.substring(pIdx, cIdx);
				if (!t.isEmpty()) list.add(t);
			}
		}
		
		return list;
	}
    
	public void wordnet(String[] args) throws Exception
	{
		WNMap map = new WNMap(args[0]);
		ObjectIntIntTriple<WNSynset> lcs = map.getLowestCommonSubsumer(WNPOSTag.NOUN, "year", "month");
		System.out.println(lcs.i1+" "+lcs.i2+" "+lcs.o.toString());
		
		lcs = map.getLowestCommonSubsumer(WNPOSTag.NOUN, "nurse", "person");
		System.out.println(lcs.i1+" "+lcs.i2+" "+lcs.o.toString());
		
		lcs = map.getLowestCommonSubsumer(WNPOSTag.NOUN, "person", "brother");
		System.out.println(lcs.i1+" "+lcs.i2+" "+lcs.o.toString());
		
		lcs = map.getLowestCommonSubsumer(WNPOSTag.NOUN, "nurse", "nanny");
		System.out.println(lcs.i1+" "+lcs.i2+" "+lcs.o.toString());
		
		lcs = map.getLowestCommonSubsumer(WNPOSTag.NOUN, "brother", "buddy");
		System.out.println(lcs.i1+" "+lcs.i2+" "+lcs.o.toString());
		
		lcs = map.getLowestCommonSubsumer(WNPOSTag.NOUN, "brother", "you");
		System.out.println(lcs);
		
		lcs = map.getLowestCommonSubsumer(WNPOSTag.NOUN, "brother", "beautiful");
		System.out.println(lcs);
		
		ObjectIntHashMap<WNSynset> m = map.getHypernymMap(WNPOSTag.NOUN, "dithyramb");
		List<ObjectIntPair<WNSynset>> list = m.toList();
		DSUtils.sortReverseOrder(list);
		
		for (ObjectIntPair<WNSynset> s : list)
			System.out.println(s.i+ " "+s.o.toString());
		
		System.out.println(map.getSynonymSet(WNPOSTag.NOUN, "study"));
		System.out.println(map.getSynonymSet(WNPOSTag.NOUN, "study", 1, 2, 3, 6));
		System.out.println(map.getSynonymSet(WNPOSTag.VERB, "study"));
		System.out.println(map.getSynonymSet(WNPOSTag.ADJECTIVE, "dynamic"));
		System.out.println(map.getSynonymSet(WNPOSTag.ADVERB, "well"));
	}
	
	@SuppressWarnings("unused")
	void test1()
	{
		String s1 = "ADD";
		String s2 = "AFX";
		String s3 = "CODE";
		long et, st;
		String s;
		int i;
		
		st = System.currentTimeMillis();
		
		for (i=0; i<1000000; i++)
			s = s1+" "+s2+" "+s3;
		
		et = System.currentTimeMillis();
		System.out.println(et-st);
	}
	
	@SuppressWarnings("unused")
	long largeVsSmallHashMapAux(List<Pair<String,String>> bigrams)
	{
		long st, et;
		String s;
		
		st = System.currentTimeMillis();
		
		for (Pair<String,String> key : bigrams)
			s = key.o1+" "+key.o2;
		
		et = System.currentTimeMillis();
		return et - st;
	}
	
	long largeVsSmallHashMapAux0(List<String> bigrams, ObjectIntOpenHashMap<String> map1)
	{
		long st, et;
		
		st = System.currentTimeMillis();
		
		for (String key : bigrams)
			map1.get(key);
		
		et = System.currentTimeMillis();
		return et - st;
	}
	
	long largeVsSmallHashMapAux(List<Pair<String,String>> bigrams, ObjectIntOpenHashMap<String> map1)
	{
		long st, et;
		
		st = System.currentTimeMillis();
		
		for (Pair<String,String> key : bigrams)
			map1.get(key.o1+" "+key.o2);
		
		et = System.currentTimeMillis();
		return et - st;
	}
	
	long largeVsSmallHashMapAux(List<Pair<String,String>> bigrams, Map<String,ObjectIntOpenHashMap<String>> map2)
	{
		long st, et;
		
		st = System.currentTimeMillis();
		
		for (Pair<String,String> key : bigrams)
			map2.get(key.o1).get(key.o2);
		
		et = System.currentTimeMillis();
		return et - st;
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