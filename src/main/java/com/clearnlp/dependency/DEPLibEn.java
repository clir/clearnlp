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
package com.clearnlp.dependency;

import java.util.List;
import java.util.Set;

import com.clearnlp.constituent.CTLibEn;
import com.clearnlp.pos.POSLibEn;
import com.clearnlp.pos.POSTagEn;
import com.clearnlp.propbank.PBLib;
import com.clearnlp.util.arc.SRLArc;
import com.clearnlp.util.language.UtilEn;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DEPLibEn implements DEPTagEn
{
	private DEPLibEn() {}
	
	static public boolean isNoun(DEPNode node)
	{
		return POSLibEn.isNoun(node.getPOSTag());
	}
	
	static public boolean isVerb(DEPNode node)
	{
		return POSLibEn.isVerb(node.getPOSTag());
	}
	
	/** Enriches certain dependency labels into finer-grained labels. */
	static public void enrichLabels(DEPTree tree)
	{
		Set<String> subj = Sets.newHashSet(DEP_CSUBJ, DEP_NSUBJ);
		List<DEPNode> list;

		for (DEPNode node : tree)
		{
			if (node.isLabel(DEP_ADVMOD) && UtilEn.isNegation(node.getWordForm()))
				node.setLabel(DEP_NEG);
			
			if (node.containsDependent(DEPLibEn.DEP_AUXPASS))
			{
				for (DEPNode child : node.getDependentListByLabel(subj))
					child.setLabel(child.getLabel()+DEPLibEn.DEP_PASS);
			}
			
			if ((list = node.getDependentListByLabel(DEPLibEn.DEP_DOBJ)).size() > 1)
				list.get(0).setLabel(DEPLibEn.DEP_IOBJ);
		}
	}
	
	static public void postLabel(DEPTree tree)
	{
		List<List<SRLArc>> argLists;
		int i, size = tree.size();
		List<SRLArc> list;
		DEPNode node;
		
		argLists = tree.getArgumentList();
		
		for (i=1; i<size; i++)
		{
			node = tree.get(i);
			list = argLists.get(i);
			
			if (node.isLabel(DEP_PREP))
			{
				relinkPreposition(node);
			}
			else if (POSLibEn.isVerb(node.getPOSTag()))
			{
				labelReferentOfRelativeClause(node, list);
			}
		}
	}

	/**
	 * Re-links PP to a verb predicate.
	 * Called by {@link #postLabel(DEPTree)}.
	 * @param prep the dependency label of this node is {@link DEPTagEn#DEP_PREP}.
	 */
	static private void relinkPreposition(DEPNode prep)
	{
		DEPNode head = prep.getHead();
		
		if (head.isLabel(DEPLibEn.DEP_POBJ))
			head = head.getHead();
		
		if (isNoun(head) || head.isPOSTag(POSTagEn.POS_IN) || head.isPOSTag(POSTagEn.POS_RP))
		{
			DEPNode gHead = head.getHead();		// verb predicate	
			SRLArc  sp;
			
			if (gHead != null && (sp = prep.getSemanticHeadArc(gHead)) != null)
			{
				if (head.getSemanticHeadArc(gHead) == null)
				{
					prep.removeSemanticHead(sp);
					head.addSemanticHead(gHead, sp.getLabel());
				}
			}
		}
	}
	
	/**
	 * Called by {@link #postLabel(DEPTree)}.
	 * Add the argument label to the head of a referent.
	 * @param verb the POS tag of this node is a verb.
	 */
	static private void labelReferentOfRelativeClause(DEPNode verb, List<SRLArc> argList)
	{
		DEPNode top  = getHeightVerbInChain(verb);
		DEPNode head = top.getHead();
		
		if (top.isLabel(DEP_RCMOD) && !head.isArgumentOf(verb))
		{
			for (SRLArc arc : argList)
			{
				if (PBLib.isReferentArgument(arc.getLabel()) && isReferentArgument(arc.getNode()))
				{
					head.addSemanticHead(verb, PBLib.getBaseLabel(arc.getLabel()));
					return;
				}
			}
		}
	}
	
	/** Called by {@link #labelReferentOfRelativeClause(DEPNode, List)}. */
	static private boolean isReferentArgument(DEPNode node)
	{
		return node.getFirstDependentByLabel(DEP_POBJ) != null || node.isLemma("that") || node.isLemma("which");
	}
	
	/**
	 * @return get the highest verb in the chain.
	 * @param verb the POS tag of this node is a verb. 
	 */
	static public DEPNode getHeightVerbInChain(DEPNode verb)
	{
		while (isVerb(verb.getHead()) && (verb.isLabel(DEP_CONJ) || verb.isLabel(DEP_XCOMP)))
			verb = verb.getHead();
			
		return verb;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param node the POS tag of this node is a noun.
	 * @return the trimmed sequence of lemmas for the specific node.
	 */
	static public String getSubLemmasForNP(DEPNode node, String delim)
	{
		StringBuilder build = new StringBuilder();
		boolean add = true;
		
		for (DEPNode dep : node.getDependentList())
		{
			// insert the node's lemma at the right position.
			if (add && dep.getID() > node.getID())
			{
				build.append(delim);
				build.append(node.getLemma());
				add = false;
			}
			
			if (dep.isLabel(DEP_NN) || dep.isPOSTag(CTLibEn.POS_PRPS))
			{
				build.append(delim);
				build.append(dep.getLemma());
			}
		}
		
		if (add)
		{
			build.append(delim);
			build.append(node.getLemma());
		}
		
		return build.substring(delim.length());
	}
	
	/**
	 * @param node the POS tag of this node is a preposition.
	 * @return the trimmed sequence of lemmas for the specific node.
	 */
	static public String getSubLemmasForPP(DEPNode node, String delim)
	{
		StringBuilder build = new StringBuilder();
		build.append(node.getLemma());

		DEPNode pobj = node.getFirstDependentByLabel(DEP_POBJ);
		
		if (pobj != null)
		{
			build.append(delim);
			
			if (POSLibEn.isNoun(pobj.getPOSTag()))
				build.append(getSubLemmasForNP(pobj, delim));
			else
				build.append(pobj.getLemma());
		}
		
		return build.toString();
	}
}