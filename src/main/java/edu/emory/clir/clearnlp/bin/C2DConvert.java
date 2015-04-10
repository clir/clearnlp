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
package edu.emory.clir.clearnlp.bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.triple.ObjectIntIntTriple;
import edu.emory.clir.clearnlp.component.mode.morph.AbstractMPAnalyzer;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.constituent.CTNode;
import edu.emory.clir.clearnlp.constituent.CTReader;
import edu.emory.clir.clearnlp.constituent.CTTree;
import edu.emory.clir.clearnlp.conversion.AbstractC2DConverter;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.lexicon.propbank.PBInstance;
import edu.emory.clir.clearnlp.lexicon.propbank.PBReader;
import edu.emory.clir.clearnlp.ner.BILOU;
import edu.emory.clir.clearnlp.ner.NERTag;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.arc.SRLArc;
import edu.emory.clir.clearnlp.util.lang.TLanguage;


public class C2DConvert
{
	@Option(name="-h", usage="headrule file (required)", required=true, metaVar="<filename>")
	private String s_headruleFile;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String s_inputPath;
	@Option(name="-pe", usage="parse file extension (default: parse)", required=false, metaVar="<string>")
	private String s_parseExt = "parse";
	@Option(name="-re", usage="propbank file extension (default: prop)", required=false, metaVar="<string>")
	private String s_propExt = "prop";
	@Option(name="-ne", usage="named entity file extension (default: name)", required=false, metaVar="<string>")
	private String s_nameExt = "name";
	@Option(name="-oe", usage="output file extension (default: dep)", required=false, metaVar="<string>")
	private String s_outputExt = "dep";
	@Option(name="-l", usage="language (default: english)", required=false, metaVar="<language>")
	private String s_language = TLanguage.ENGLISH.toString();
	@Option(name="-n", usage="if set, normalize empty category indices", required=false, metaVar="<boolean>")
	private boolean b_normalize = false;
	@Option(name="-r", usage="if set, traverse parse files recursively", required=false, metaVar="<boolean>")
	private boolean b_recursive = false;

	public C2DConvert() {}
	
	public C2DConvert(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		List<String> parseFiles = FileUtils.getFileList(s_inputPath, s_parseExt, b_recursive);
		TLanguage language = TLanguage.getType(s_language);
		int n;
		
		AbstractC2DConverter converter = NLPUtils.getC2DConverter(language, IOUtils.createFileInputStream(s_headruleFile));
		AbstractMPAnalyzer   analyzer  = NLPUtils.getMPAnalyzer(language);
		
		for (String parseFile : parseFiles)
		{
			n = convert(converter, analyzer, parseFile, s_parseExt, s_propExt, s_nameExt, s_outputExt, b_normalize);
			System.out.printf("%s: %d trees\n", parseFile, n);
		}
	}
	
	protected int convert(AbstractC2DConverter converter, AbstractMPAnalyzer analyzer, String parseFile, String parseExt, String propExt, String nameExt, String outputExt, boolean normalize) throws Exception
	{
		IntObjectHashMap<List<ObjectIntIntTriple<String>>> mName = getNamedEntityMap(parseFile, parseExt, nameExt);
		IntObjectHashMap<List<PBInstance>> mProp = getPBInstanceMap(parseFile, parseExt, propExt);
		PrintStream fout = IOUtils.createBufferedPrintStream(parseFile+"."+outputExt);
		CTReader reader = new CTReader(IOUtils.createFileInputStream(parseFile));
		List<ObjectIntIntTriple<String>> names = null;
		List<PBInstance> instances = null;
		CTTree  cTree;
		DEPTree dTree;
		int n;
		
		for (n=0; (cTree = reader.nextTree()) != null; n++)
		{
			if (normalize) cTree.normalizeIndices();
			if (mProp != null && (instances = mProp.get(n)) != null)	initPropBank(cTree, instances);
			if (mName != null && (names = mName.get(n)) != null)		initNamedEntities(cTree, names);
			dTree = converter.toDEPTree(cTree);
			
			if (dTree != null)
			{
				if (instances != null)
				{
					retainOnyVerbPredicates(dTree);
					DEPLibEn.postLabel(dTree);
				}
				
				analyzer.process(dTree);
				fout.println(dTree.toString()+"\n");
			}
			else
				System.err.println("No token in the tree "+(n+1)+"\n"+cTree.toStringLine());
		}
		
		reader.close();
		fout.close();
		
		return n;
	}
	
	private IntObjectHashMap<List<PBInstance>> getPBInstanceMap(String parseFile, String parseExt, String propExt)
	{
		String filename = getFilename(parseFile, parseExt, propExt); 
		return filename != null ? new PBReader(IOUtils.createFileInputStream(filename)).getInstanceMap() : null;
	}
	
	private String getFilename(String parseFile, String parseExt, String otherExt)
	{
		if (parseExt == null || otherExt == null) return null;
		String filename = FileUtils.replaceExtension(parseFile, parseExt, otherExt);
		if (filename == null || !new File(filename).isFile()) return null;
		return filename;
	}
	
	private void initPropBank(CTTree tree, List<PBInstance> instances)
	{
		for (PBInstance instance : instances)
		{
			if (!instance.isTemporaryInstance())
				tree.initPBInstance(instance);			
		}
	}
	
	private void retainOnyVerbPredicates(DEPTree tree)
	{
		Set<DEPNode> verbs;
		DEPNode head;
		SRLArc  arc;
		
		for (DEPNode node : tree)
		{
			if (node.isSemanticHead() && !POSLibEn.isVerb(node.getPOSTag()))
			{
				verbs = node.getSemanticHeadSet("AM-PRR");
				
				for (DEPNode arg : tree)
				{
					if (node != arg && (arc = arg.getSemanticHeadArc(node)) != null)
					{
						head = arg.getHead();
					
						if (verbs.contains(head) || (head = getRCVerb(verbs, arg)) != null)
							arc.setNode(head);
						else
							arg.removeSemanticHead(arc);
					}
				}
				
				node.clearRolesetID();
			}
		}
	}
	
	private DEPNode getRCVerb(Set<DEPNode> verbs, DEPNode arg)
	{
		for (DEPNode verb : verbs)
		{
			if (verb.isDependentOf(arg))
				return verb;
		}
		
		return null;
	}
	
	private IntObjectHashMap<List<ObjectIntIntTriple<String>>> getNamedEntityMap(String parseFile, String parseExt, String nameExt) throws Exception
	{
		String filename = getFilename(parseFile, parseExt, nameExt);
		if (filename == null) return null;
		
		IntObjectHashMap<List<ObjectIntIntTriple<String>>> map = new IntObjectHashMap<>();
		BufferedReader fin = IOUtils.createBufferedReader(filename);
		String[] tmp;
		String line;
		int treeID;
		
		while ((line = fin.readLine()) != null)
		{
			tmp    = Splitter.splitSpace(line);
			treeID = Integer.parseInt(tmp[1]);
			map.put(treeID, getNamedEntityList(tmp));
		}
		
		fin.close();
		return map;
	}
	
	private List<ObjectIntIntTriple<String>> getNamedEntityList(String[] names)
	{
		int i, bIdx, eIdx, size = names.length;
		List<ObjectIntIntTriple<String>> list = new ArrayList<>(size-2);
		String[] t0, t1;
		String ent;

		for (i=2; i<size; i++)
		{
			t0   = Splitter.splitHyphens(names[i]);
			t1   = Splitter.splitColons(t0[0]);
			ent  = t0[1];
			bIdx = Integer.parseInt(t1[0]);
			eIdx = Integer.parseInt(t1[1]);
			list.add(new ObjectIntIntTriple<>(ent, bIdx, eIdx));
		}
		
		return list;
	}
	
	private void initNamedEntities(CTTree cTree, List<ObjectIntIntTriple<String>> names)
	{
		if (names == null)	return;
		int i;
		
		for (CTNode node : cTree.getTerminalList())
			node.setNamedEntityTag(BILOU.O.toString());
		
		for (ObjectIntIntTriple<String> t : names)
		{
			if (t.i1 == t.i2)
				cTree.getTerminal(t.i1).setNamedEntityTag(NERTag.toBILOUTag(BILOU.U, t.o));
			else
			{
				cTree.getTerminal(t.i1).setNamedEntityTag(NERTag.toBILOUTag(BILOU.B, t.o));
				cTree.getTerminal(t.i2).setNamedEntityTag(NERTag.toBILOUTag(BILOU.L, t.o));
				
				for (i=t.i1+1; i<t.i2; i++)
					cTree.getTerminal(i).setNamedEntityTag(NERTag.toBILOUTag(BILOU.I, t.o));
			}
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			new C2DConvert(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}