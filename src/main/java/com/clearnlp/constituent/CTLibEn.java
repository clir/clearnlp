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
package com.clearnlp.constituent;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.clearnlp.constant.StringConst;
import com.clearnlp.constituent.matcher.CTNodeMatcher;
import com.clearnlp.constituent.matcher.CTNodeMatcherC;
import com.clearnlp.constituent.matcher.CTNodeMatcherCF;
import com.clearnlp.constituent.matcher.CTNodeMatcherF;
import com.clearnlp.constituent.matcher.CTNodeMatcherPrefix;
import com.clearnlp.constituent.matcher.CTNodeMatcherSet;
import com.clearnlp.pos.POSLibEn;
import com.clearnlp.pos.POSTagEn;
import com.clearnlp.util.PatternUtils;
import com.clearnlp.util.StringUtils;
import com.clearnlp.util.language.UtilEn;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class CTLibEn implements CTTagEn, POSTagEn
{
	static final public Pattern P_PASSIVE_NULL = PatternUtils.createClosedORPattern("\\*","\\*-\\d+");
	
	static final public CTNodeMatcher M_NP			= new CTNodeMatcherC(C_NP);
	static final public CTNodeMatcher M_VP			= new CTNodeMatcherC(C_VP);
	static final public CTNodeMatcher M_QP			= new CTNodeMatcherC(C_QP);
	static final public CTNodeMatcher M_ADVP		= new CTNodeMatcherC(C_ADVP);
	static final public CTNodeMatcher M_SBAR		= new CTNodeMatcherC(C_SBAR);
	static final public CTNodeMatcher M_EDITED		= new CTNodeMatcherC(C_EDITED);
	
	static final public CTNodeMatcher M_NOM			= new CTNodeMatcherF(F_NOM);
	static final public CTNodeMatcher M_PRD			= new CTNodeMatcherF(F_PRD);
	
	static final public CTNodeMatcher M_NP_SBJ		= new CTNodeMatcherCF(C_NP, F_SBJ);
	
	static final public CTNodeMatcher M_NNx			= new CTNodeMatcherPrefix(POS_NN);
	static final public CTNodeMatcher M_VBx			= new CTNodeMatcherPrefix(POS_VB);
	static final public CTNodeMatcher M_WHx			= new CTNodeMatcherPrefix("WH");
	static final public CTNodeMatcher M_SBARx		= new CTNodeMatcherPrefix(C_SBAR);
	
	static final public CTNodeMatcher M_NP_NML		= new CTNodeMatcherSet(Sets.newHashSet(C_NP, C_NML));
	static final public CTNodeMatcher M_VBD_VBN		= new CTNodeMatcherSet(Sets.newHashSet(POS_VBD, POS_VBN));
	static final public CTNodeMatcher M_VP_RRC_UCP	= new CTNodeMatcherSet(Sets.newHashSet(C_VP, C_RRC, C_UCP));
	
	static final private Set<String> S_LGS_PHRASE		= Sets.newHashSet(C_PP, C_SBAR);
	static final private Set<String> S_MAIN_CLAUSE		= Sets.newHashSet(C_S, C_SQ, C_SINV);
	static final private Set<String> S_EDITED_PHRASE	= Sets.newHashSet(C_EDITED, C_EMBED);
	static final private Set<String> S_NOMINAL_PHRASE	= Sets.newHashSet(C_NP, C_NML, C_NX, C_NAC);
	static final private Set<String> S_WH_LINK			= Sets.newHashSet(C_WHNP, C_WHPP, C_WHADVP);
	static final private Set<String> S_SEPARATOR		= Sets.newHashSet(POS_COMMA, POS_COLON);
	static final private Set<String> S_CONJUNCTION		= Sets.newHashSet(POS_CC, C_CONJP);
	
	private CTLibEn() {}
	
	/**
	 * Fixes inconsistent function tags.
	 * Links antecedents of reduced passive nulls ({@code *}) and relativizers.
	 * @see #fixFunctionTags(CTTree)
	 * @see #linkReducedPassiveNulls(CTTree)
	 * @see #linkRelativizers(CTTree)	 
	 */
	static public void preprocess(CTTree tree)
	{
		fixFunctionTags(tree);
		linkReducedPassiveNulls(tree);
		linkRelativizers(tree);
	}
	
//	======================== Fix function tags ========================

	/**
	 * Fixes inconsistent function tags in the specific tree.
	 * @see CTLibEn#fixSBJ(CTNode)
	 * @see CTLibEn#fixLGS(CTNode)
	 * @see CTLibEn#fixCLF(CTNode)
	 */
	static public void fixFunctionTags(CTTree tree)
	{
		fixFunctionTagsAux(tree.getRoot());
	}
	
	/** Called by {@link CTLibEn#fixFunctionTags(CTTree)}. */
	static private void fixFunctionTagsAux(CTNode node)
	{
		if (!fixSBJ(node) && !fixLGS(node) && !fixCLF(node))
			;	// no error in this node
		
		for (CTNode child : node.getChildrenList())
			fixFunctionTagsAux(child);
	}
	
	/** If the specific node contains the function tag {@link CTTagEn#F_SBJ} and it is the only child of its parent, moves the tag to its parent. */
	static private boolean fixSBJ(CTNode node)
	{
		if (node.hasFunctionTag(F_SBJ))
		{
			CTNode parent = node.getParent();
			
			if (parent.getChildrenSize() == 1 && !parent.isConstituentTagAny(S_EDITED_PHRASE) && parent.hasNoFunctionTag())
			{
				node.removeFunctionTag(F_SBJ);
				parent.addFunctionTag(F_SBJ);
				parent.setConstituentTag(node.getConstituentTag());
				return true;
			}
		}
		
		return false;
	}
	
	/** If the specific node contains the function tag {@link CTTagEn#F_LGS} and it is not a prepositional phrase, moves the tag to its parent. */
	static private boolean fixLGS(CTNode node)
	{
		if (node.hasFunctionTag(F_LGS) && !node.isConstituentTag(C_PP))
		{
			CTNode parent = node.getParent();
			
			if (parent.isConstituentTagAny(S_LGS_PHRASE))
			{
				node.removeFunctionTag(F_LGS);
				parent.addFunctionTag(F_LGS);
				return true;
			}
		}
		
		return false;
	}
	
	/** If the specific node contains the function tag {@link CTTagEn#F_CLF} and it is not a subordinate clause, moves the tag to the subordinate clause. */
	static private boolean fixCLF(CTNode node)
	{
		if (node.hasFunctionTag(F_CLF) && isMainClause(node))
		{
			CTNode desc = node.getFirstDescendant(M_SBARx);
			node.removeFunctionTag(F_CLF);
			
			if (desc != null)
			{
				desc.addFunctionTag(F_CLF);
				return true;
			}
		}
		
		return false;
	}
	
//	======================== Passive nulls ========================
	
	/**
	 * Finds reduced passive empty category ({@code *}) and links them to their antecedents in the specific tree.
	 * This method links most but not all antecedents; especially ones related to parenthetical phrases and topicalization.
	 * @see CTLibEn#isPassiveEmptyCategory(CTNode)
	 */
	static public void linkReducedPassiveNulls(CTTree tree)
	{
		linkReducedPassiveNullsAux(tree, tree.getRoot());
	}
	
	/** Called by {@link #linkReducedPassiveNulls(CTTree)}. */
	static private void linkReducedPassiveNullsAux(CTTree tree, CTNode curr)
	{
		if (isPassiveEmptyCategory(curr) && curr.isWordForm("*"))
		{
			CTNode parent = curr.getParent();	// NP
			int index = parent.getParent().getEmptyCategoryIndex();
			
			if (index != -1)	// VP
			{
				List<CTNode> list = tree.getEmptyCategoryList(index);
				if (list != null) parent = list.get(0);
			}
			
			CTNode vp = parent.getHighestChainedAncestor(M_VP_RRC_UCP);

			if (vp.getParent().matches(M_NP_NML) || vp.getParent().hasFunctionTag(F_NOM))
			{
				curr.setAntecedent(vp.getLeftNearestSibling(M_NP_NML));
				
				if (!curr.hasAntecedent())
					curr.setAntecedent(vp.getLeftNearestSibling(M_NNx));
				
				if (!curr.hasAntecedent())
					curr.setAntecedent(vp.getLeftNearestSibling(M_QP));
				
				if (!curr.hasAntecedent())
					curr.setAntecedent(vp.getLeftNearestSibling(M_NOM));
			}
			else if (isClause(vp.getParent()))
			{
				curr.setAntecedent(vp.getLeftNearestSibling(M_NP_SBJ));
				
				if (!curr.hasAntecedent())	// VP-TPC
					curr.setAntecedent(vp.getRightNearestSibling(M_NP_SBJ));
			}
		}
		
		for (CTNode child : curr.getChildrenList())
			linkReducedPassiveNullsAux(tree, child);
	}
	
	/** @return {@code true} if the specific node represents a passive null ({@code *|*-\d}). */
	static public boolean isPassiveEmptyCategory(CTNode node)
	{
		if (node.isEmptyCategory() && node.matchesWordForm(P_PASSIVE_NULL) && node.hasParent())
		{
			node = node.getParent();
			
			if (node.isConstituentTag(C_NP) && node.hasNoFunctionTag() &&
				node.hasParent() && node.getParent().isConstituentTag(C_VP) &&
				node.hasLeftSibling() && node.getLeftSibling().matches(M_VBD_VBN))
				return true;
		}
		
		return false;
	}
	
//	======================== Complementizers ========================
	
	/**
	 * Finds relativizers and links them to their antecedents in the specific tree.
	 * This method links most but not all antecedents; especially when the relativizers are under {@code *-PRD} phrases.
	 */
	static public void linkRelativizers(CTTree tree)
	{
		linkComlementizersAux(tree, tree.getRoot());
	}
	
	/** Called by {@link #linkRelativizers(CTTree)}. */
	static private void linkComlementizersAux(CTTree tree, CTNode curr)
	{
		if (isWhPhraseLink(curr))
		{
			CTNode comp = getRelativizer(curr);
			CTNode sbar = curr.getHighestChainedAncestor(M_SBAR);
			
			if (comp != null && sbar != null && !sbar.hasFunctionTag(F_NOM) && UtilEn.isLinkingRelativizer(comp.getWordForm()))
			{
				if (sbar.getEmptyCategoryIndex() != -1)
				{
					List<CTNode> ecs = tree.getEmptyCategoryList(sbar.getEmptyCategoryIndex());
					
					if (ecs != null)
					{
						for (CTNode ec : ecs)
						{
							if (ec.getWordForm().startsWith(E_ICH) && ec.getParent().isConstituentTag(C_SBAR))
							{
								sbar = ec.getParent();
								break;
							}
						}						
					}
				}
				else if (sbar.hasParent() && sbar.getParent().isConstituentTag(C_UCP))
					sbar = sbar.getParent();
				
				CTNode p = sbar.getParent(), ante;
				if (p == null)	return;
				
				if (p.isConstituentTag(C_NP))
				{
					if ((ante = sbar.getLeftNearestSibling(M_NP)) != null)
						comp.setAntecedent(ante);
				}
				else if (p.isConstituentTag(C_ADVP))
				{
					if ((ante = sbar.getLeftNearestSibling(M_ADVP)) != null)
						comp.setAntecedent(ante);
				}
				else if (p.isConstituentTag(C_VP))
				{
					if ((ante = sbar.getLeftNearestSibling(M_PRD)) != null)
					{
						if (sbar.hasFunctionTag(F_CLF) ||
						   (curr.isConstituentTag(C_WHNP)   && ante.isConstituentTag(C_NP)) ||
						   (curr.isConstituentTag(C_WHPP)   && ante.isConstituentTag(C_PP)) ||
						   (curr.isConstituentTag(C_WHADVP) && ante.isConstituentTag(C_ADVP)))
							comp.setAntecedent(ante);
					}
				}
				
				ante = comp.getAntecedent();
				
				while (ante != null && ante.isEmptyCategoryTerminal())
					ante = ante.getFirstTerminal().getAntecedent();
				
				comp.setAntecedent(ante);
			}
		}
		else
		{
			for (CTNode child : curr.getChildrenList())
				linkComlementizersAux(tree, child);
		}
	}
	
	/**
	 * @return the first relativizer under the specific node if exists; otherwise, {@code null}.
	 * The specific node must be a wh-phrase.
	 */
	static public CTNode getRelativizer(CTNode node)
	{
		if (!isWhPhrase(node))
			return null;
		
		List<CTNode> terminals = node.getTerminalList();
		
		if (node.isEmptyCategoryTerminal())
			return terminals.get(0);
		
		for (CTNode term : terminals)
		{
			if (isRelativizer(term))
				return term;
		}
		
		for (CTNode term : terminals)
		{
			if (UtilEn.isRelativizer(term.getWordForm()))
				return term;
		}
			
		return null;
	}
	
	static public CTNode getWhPhrase(CTNode node)
	{
		return getNode(node, M_WHx, true);
	}
	
//	======================== Coordination ========================

	/** @return {@code true} if the specific node contains coordination. */
	static public boolean containsCoordination(CTNode node)
	{
		return containsCoordination(node, node.getChildrenList());
	}
	
	/** @return {@code true} if the specific list of children contains coordination. */
	static public boolean containsCoordination(CTNode parent, List<CTNode> siblings)
	{
		if (parent.isConstituentTag(C_UCP))
			return true;
		
		if (parent.matches(M_NP_NML) && containsEtc(siblings))
			return true;
		
		for (CTNode child : siblings)
		{
			if (isConjunction(child))
				return true;
		}

		return false;
	}
	
	/** Called by {@link CTLibEn#containsCoordination(CTNode, List)}. */
	static private boolean containsEtc(List<CTNode> children)
	{
		int i, size = children.size();
		CTNode child;
		
		for (i=size-1; i>0; i--)
		{
			child = children.get(i);
			
			if (isPunctuation(child))	continue;
			if (isEtc(child))			return true;
			break;
		}
		
		return false;
	}
	
	/** @return {@code true} if the specific node is et cetera (e.g., etc). */
	static public boolean isEtc(CTNode node)
	{
		return node.hasFunctionTag(F_ETC) || node.getFirstTerminal().isWordFormIgnoreCase("etc.");
	}
	
	/**
	 * @return {@code true} if this node is a conjunction.
	 * @see CTLibEn#isConjunction(CTNode)
	 * @see CTLibEn#isSeparator(CTNode)
	 */
	static public boolean isCoordinator(CTNode node)
	{
		return isConjunction(node) || isSeparator(node);
	}
	
	/** @return {@code true} if this node is a conjunction. */
	static public boolean isConjunction(CTNode node)
	{
		return node.isConstituentTagAny(S_CONJUNCTION);
	}
	
	/** @return {@code true} if this node is a separator. */
	static public boolean isSeparator(CTNode node)
	{
		return node.isConstituentTagAny(S_SEPARATOR);
	}
	
	/** @return {@code true} if this node is a correlative conjunction. */
	static public boolean isCorrelativeConjunction(CTNode node)
	{
		if (node.isConstituentTag(POS_CC))
		{
			return UtilEn.isCorrelativeConjunction(node.getWordForm());
		}
		else if (node.isConstituentTag(C_CONJP))
		{
			String form = StringUtils.toLowerCase(node.toWordForms(false, StringConst.SPACE));
			return form.equals("not only");
		}
		
		return false;
	}
	
//	======================== Constituent ========================
	
	static public boolean isClause(CTNode node)
	{
		return isMainClause(node) || isSubordinateClause(node);
	}
	
	/** @return {@code true} if "S|SQ|SINV". */
	static public boolean isMainClause(CTNode node)
	{
		return node.isConstituentTagAny(S_MAIN_CLAUSE);
	}
	
	static public boolean isSubordinateClause(CTNode node)
	{
		return node.getConstituentTag().startsWith(C_SBAR);
	}

	static public boolean isNominalPhrase(CTNode node)
	{
		return node.isConstituentTagAny(S_NOMINAL_PHRASE);
	}
	
	static public boolean isWhPhraseLink(CTNode node)
	{
		return node.isConstituentTagAny(S_WH_LINK);
	}
	
	static public boolean isWhPhrase(CTNode node)
	{
		return M_WHx.matches(node);
	}
	
	static public boolean isEditedPhrase(CTNode node)
	{
		return getNode(node, M_EDITED, true) != null;
	}
	
	static public boolean isDiscontinuousConstituent(CTNode node)
	{
		String tag = node.getWordForm();
		return tag.startsWith(E_ICH) || tag.startsWith(E_PPA) || isRNR(node);
	}
	
	static public boolean isRNR(CTNode node)
	{
		return node.getWordForm().startsWith(E_RNR);
	}
	
	static public CTNode getNode(CTNode node, CTNodeMatcher matcher, boolean recursive)
	{
		if (matcher.matches(node))
			return node;
		
		if (recursive && node.getChildrenSize() == 1)
			return getNode(node.getFirstChild(), matcher, recursive);
		
		return null;
	}
	
//	======================== Part-of-speech ========================
	
	static public boolean isNoun(CTNode node)
	{
		return POSLibEn.isNoun(node.getConstituentTag());
	}
	
	static public boolean isVerb(CTNode node)
	{
		return POSLibEn.isVerb(node.getConstituentTag());
	}
	
	static public boolean isAdjective(CTNode node)
	{
		return POSLibEn.isAdjective(node.getConstituentTag());
	}
	
	static public boolean isAdverb(CTNode node)
	{
		return POSLibEn.isAdverb(node.getConstituentTag());
	}
	
	static public boolean isRelativizer(CTNode node)
	{
		return POSLibEn.isRelativizer(node.getConstituentTag());
	}
	
	static public boolean isPunctuation(CTNode node)
	{
		return POSLibEn.isPunctuation(node.getConstituentTag());
	}
}