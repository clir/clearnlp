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
import java.util.List;

import edu.emory.clir.clearnlp.collection.ngram.Unigram;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.constituent.CTLib;
import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.constituent.CTNode;
import edu.emory.clir.clearnlp.constituent.CTReader;
import edu.emory.clir.clearnlp.constituent.CTTree;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MWEExtractor
{
	public void extract(String filename)
	{
		CTReader reader = new CTReader(IOUtils.createFileInputStream(filename));
		Unigram<String> qpPre  = new Unigram<>();
		Unigram<String> qpPost = new Unigram<>();
		CTTree tree;
		
		while ((tree = reader.nextTree()) != null)
			extract(tree.getRoot(), qpPre, qpPost);

		printMap(qpPre , filename+".qp_pre");
		printMap(qpPost, filename+".qp_post");
	}
	
	private void printMap(Unigram<String> map, String outputFile)
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
		List<ObjectIntPair<String>> list = map.toList(0);
		DSUtils.sortReverseOrder(list);
		
		for (ObjectIntPair<String> p : list)
			fout.println(p.o+" "+p.i);
		
		fout.close();
	}
	
	public void extract(CTNode node, Unigram<String> qpPre, Unigram<String> qpPost)
	{
		if (node.isConstituentTag(CTLibEn.C_QP))
			extractQP(node, qpPre, qpPost);
		else
		{
			for (CTNode child : node.getChildrenList())
				extract(child, qpPre, qpPost);			
		}
	}
	
	private void extractQP(CTNode node, Unigram<String> pre, Unigram<String> post)
	{
		List<CTNode> tokens = node.getTokenList();
		int i, size = tokens.size();
		CTNode token;
		
		for (i=0; i<size; i++)
		{
			token = tokens.get(i);
			
			if (token.isConstituentTagAny(POSLibEn.POS_CD, POSLibEn.POS_DOLLAR))
			{
				if (i > 0) pre.add(StringUtils.toLowerCase(CTLib.toForms(tokens, 0, i, StringConst.SPACE)));
				break;
			}
		}
		
		for (i=size-1; i>=0; i--)
		{
			token = tokens.get(i);
			
			if (token.isConstituentTag(POSLibEn.POS_CD))
			{
				if (i+1 < size) post.add(StringUtils.toLowerCase(CTLib.toForms(tokens, i+1, size, StringConst.SPACE)));
				break;
			}
		}
	}
	
	static public void main(String[] args)
	{
		MWEExtractor mwe = new MWEExtractor();
		mwe.extract("/Users/jdchoi/Documents/Data/ontonotes/data/english/onto.parse");
	}
}
