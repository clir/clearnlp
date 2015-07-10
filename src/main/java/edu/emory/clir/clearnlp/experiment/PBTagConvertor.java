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

import java.io.PrintStream;
import java.util.Set;

import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.lexicon.propbank.PBLib;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFMap;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFRole;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFRoleset;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFType;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.srl.SRLTree;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.arc.SRLArc;

/**
 * @since 3.2.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBTagConvertor
{
	final Set<String> ARGM = DSUtils.toHashSet("ADJ","ADV","CAU","COM","DIR","DIS","DSP","EXT","GOL","LOC","MNR","MOD","NEG","PRD","PRP","PRR","REC","TMP"); 
	
	public void convert(String frameDir, String inputFile, String outputFile)
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
//		ObjectIntHashMap<String> count = new ObjectIntHashMap<>();
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(IOUtils.createFileInputStream(inputFile));
		PBFMap map = new PBFMap(frameDir);
		String pb, lemma, label;
		PBFRoleset roleset;
		DEPTree tree;
		SRLTree srl;
		int c;
		
		while ((tree = reader.next()) != null)
		{
			c = 0;
			
			for (DEPNode node : tree)
			{
				pb = node.getFeat(DEPLib.FEAT_PB);
				if (pb == null) continue;
				srl = tree.getSRLTree(node);
				lemma = pb.substring(0, pb.length()-3);
				roleset = map.getRoleset(PBFType.VERB, lemma, pb);
				
				for (SRLArc arc : srl.getArgumentArcList())
				{
					label = arc.getLabel();
					
					if (label.endsWith("PRX"))
						convert(arc, "PRR");
					else if (label.endsWith("PNC"))
						convert(arc, "PRP");
					else if (PBLib.isNumberedArgument(label))
						convertNumberedArgument(arc, roleset, pb);
					else	// AM
						convert(arc, label.substring(label.lastIndexOf('-')+1, label.length()));
					
					arc.getNode().getSemanticHeadArc(node).setLabel(arc.getLabel());
					c++;
//					count.add(arc.getLabel());
				}
			}
			
			if (c > 0) fout.println(tree.toString(DEPNode::toStringSRL)+"\n");
		}
		
		fout.close();
//		for (ObjectIntPair<String> p : count)
//			System.out.println(p.o+"\t"+p.i);
	}
	
	private void convert(SRLArc arc, String label)
	{
		String s = arc.getLabel();
		
		if (s.startsWith("C-") || s.startsWith("R-"))
			label = s.substring(0,2) + label;
		
		arc.setLabel(label);
	}
	
	private void convertNumberedArgument(SRLArc arc, PBFRoleset roleset, String pb)
	{
		String s = arc.getLabel(), label;
		String n = (s.startsWith("C-") || s.startsWith("R-")) ? s.substring(3,4) : s.substring(1,2);
		PBFRole role;
		
		if (!n.equals("0") && !n.equals("1") && roleset != null && !pb.endsWith("LV") && (role = roleset.getRole(n)) != null && ARGM.contains(label = role.getFunctionTag().toUpperCase()))
			convert(arc, label);
		
		if ((s = arc.getLabel()).endsWith("DSP"))
			arc.setLabel(s.substring(0, s.length()-4));
	}

	static public void main(String[] args)
	{
		String frameDir   = "/home/jdchoi/lib/frames";
		String inputFile  = args[0];
		new PBTagConvertor().convert(frameDir, inputFile, inputFile+".new");
	}
}
