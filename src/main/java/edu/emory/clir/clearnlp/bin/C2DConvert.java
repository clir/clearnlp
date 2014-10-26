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

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.component.mode.morph.AbstractMPAnalyzer;
import edu.emory.clir.clearnlp.constituent.CTReader;
import edu.emory.clir.clearnlp.constituent.CTTree;
import edu.emory.clir.clearnlp.conversion.AbstractC2DConverter;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.lexicon.propbank.PBInstance;
import edu.emory.clir.clearnlp.lexicon.propbank.PBReader;
import edu.emory.clir.clearnlp.nlp.NLPGetter;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.arc.SRLArc;
import edu.emory.clir.clearnlp.util.lang.TLanguage;


public class C2DConvert
{
	@Option(name="-h", usage="name of a headrule file (required)", required=true, metaVar="<filename>")
	private String s_headruleFile;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String s_inputPath;
	@Option(name="-pe", usage="parse file extension (default: null)", required=false, metaVar="<regex>")
	private String s_parseExt = null;
	@Option(name="-re", usage="propbank file extension (default: null)", required=false, metaVar="<regex>")
	private String s_propExt = null;
	@Option(name="-oe", usage="output file extension (default: cnlp)", required=false, metaVar="<string>")
	private String s_outputExt = "cnlp";
	@Option(name="-l", usage="language (default: en)", required=false, metaVar="<language>")
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
		
		AbstractC2DConverter converter = NLPGetter.getC2DConverter(language, IOUtils.createFileInputStream(s_headruleFile));
		AbstractMPAnalyzer   analyzer  = NLPGetter.getMPAnalyzer(language);
		
		for (String parseFile : parseFiles)
		{
			n = convert(converter, analyzer, parseFile, s_parseExt, s_propExt, s_outputExt, b_normalize);
			System.out.printf("%s: %d trees\n", parseFile, n);
		}
	}
	
	protected int convert(AbstractC2DConverter converter, AbstractMPAnalyzer analyzer, String parseFile, String parseExt, String propExt, String outputExt, boolean normalize)
	{
		IntObjectHashMap<List<PBInstance>> mProp = getPBInstanceMap(parseFile, parseExt, propExt);
		PrintStream fout = IOUtils.createBufferedPrintStream(parseFile+"."+outputExt);
		CTReader reader = new CTReader(IOUtils.createFileInputStream(parseFile));
		List<PBInstance> instances = null;
		CTTree  cTree;
		DEPTree dTree;
		int n;
		
		for (n=0; (cTree = reader.nextTree()) != null; n++)
		{
			if (normalize) cTree.normalizeIndices();
			if (mProp != null && (instances = mProp.get(n)) != null) initPropBank(cTree, instances);
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
				System.err.println("No token in the tree "+(n+1));
		}
		
		reader.close();
		fout.close();
		
		return n;
	}
	
	private IntObjectHashMap<List<PBInstance>> getPBInstanceMap(String parseFile, String parseExt, String propExt)
	{
		if (parseExt == null || propExt == null) return null;
		String propFile = FileUtils.replaceExtension(parseFile, parseExt, propExt);
		if (propFile == null || !new File(propFile).isFile()) return null;
		return new PBReader(IOUtils.createFileInputStream(propFile)).getInstanceMap();
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
	
	public static void main(String[] args)
	{
		try
		{
			new C2DConvert(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}