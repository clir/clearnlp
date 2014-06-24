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
package com.clearnlp.conversion;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UnknownFormatConversionException;
import java.util.regex.Pattern;

import com.clearnlp.collection.set.IntHashSet;
import com.clearnlp.constituent.CTLibEn;
import com.clearnlp.constituent.CTNode;
import com.clearnlp.constituent.CTTagEn;
import com.clearnlp.constituent.CTTree;
import com.clearnlp.constituent.matcher.CTNodeMatcher;
import com.clearnlp.constituent.matcher.CTNodeMatcherC;
import com.clearnlp.constituent.matcher.CTNodeMatcherCF;
import com.clearnlp.constituent.matcher.CTNodeMatcherF;
import com.clearnlp.constituent.matcher.CTNodeMatcherSet;
import com.clearnlp.conversion.headrule.HeadRule;
import com.clearnlp.conversion.headrule.HeadRuleMap;
import com.clearnlp.dependency.DEPFeat;
import com.clearnlp.dependency.DEPLib;
import com.clearnlp.dependency.DEPLibEn;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTagEn;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.lexicon.propbank.PBLib;
import com.clearnlp.pos.POSTagEn;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.PatternUtils;
import com.clearnlp.util.StringUtils;
import com.clearnlp.util.arc.AbstractArc;
import com.clearnlp.util.arc.PBArc;
import com.clearnlp.util.arc.SRLArc;
import com.clearnlp.util.lang.ENUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * Constituent to dependency converter for English.
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class EnglishC2DConverter extends AbstractC2DConverter
{
	
	private final Set<String> S_NPADVMOD	= Sets.newHashSet(CTTagEn.C_NML, CTTagEn.C_NP, CTTagEn.C_QP);
	private final Set<String> S_ADVCL		= Sets.newHashSet(CTTagEn.C_S, CTTagEn.C_SBAR, CTTagEn.C_SINV);
	private final Set<String> S_NFMOD		= Sets.newHashSet(CTTagEn.C_NML, CTTagEn.C_NP, CTTagEn.C_WHNP);
	private final Set<String> S_CCOMP		= Sets.newHashSet(CTTagEn.C_S, CTTagEn.C_SQ, CTTagEn.C_SINV, CTTagEn.C_SBARQ);
	private final Set<String> S_META		= Sets.newHashSet(CTTagEn.C_EDITED, CTTagEn.C_EMBED, CTTagEn.C_LST, CTTagEn.C_META, CTLibEn.POS_CODE, CTTagEn.C_CAPTION, CTTagEn.C_CIT, CTTagEn.C_HEADING, CTTagEn.C_TITLE);
	private final Set<String> S_MARK		= Sets.newHashSet(CTLibEn.POS_IN, CTLibEn.POS_TO, CTLibEn.POS_DT);
	private final Set<String> S_POSS		= Sets.newHashSet(CTLibEn.POS_PRPS, CTLibEn.POS_WPS);
	private final Set<String> S_INTJ		= Sets.newHashSet(CTTagEn.C_INTJ, CTLibEn.POS_UH);
	private final Set<String> S_PRT 		= Sets.newHashSet(CTTagEn.C_PRT, CTLibEn.POS_RP);
	private final Set<String> S_NUM			= Sets.newHashSet(CTLibEn.POS_CD, CTTagEn.C_QP);
	private final Set<String> S_DET			= Sets.newHashSet(CTLibEn.POS_DT, CTLibEn.POS_WDT, CTLibEn.POS_WP);
	private final Set<String> S_AUX			= Sets.newHashSet(CTLibEn.POS_MD, CTLibEn.POS_TO);
	private final Set<String> S_NN			= Sets.newHashSet(CTTagEn.C_NML, CTTagEn.C_NP, CTLibEn.POS_FW);

	private final Set<String> S_ADJT_PHRASE	= Sets.newHashSet(CTTagEn.C_ADJP, CTTagEn.C_WHADJP);
	private final Set<String> S_NOUN_PHRASE	= Sets.newHashSet(CTTagEn.C_NP, CTTagEn.C_NML);
	private final Set<String> S_PREP_PHRASE	= Sets.newHashSet(CTTagEn.C_PP, CTTagEn.C_WHPP);
	private final Set<String> S_ADVB_PHRASE	= Sets.newHashSet(CTTagEn.C_ADJP, CTTagEn.C_ADVP, CTTagEn.C_PP);
	private final Set<String> S_PREPOSITION	= Sets.newHashSet(CTLibEn.POS_IN, CTLibEn.POS_TO);
	private final Set<String> S_PARTICIPIAL	= Sets.newHashSet(CTLibEn.POS_VBG, CTLibEn.POS_VBN);
	private final Set<String> S_PREP_DET	= Sets.newHashSet(CTLibEn.POS_IN, CTLibEn.POS_DT);
	
	private final Set<String> S_COMP_PARENT_S = Sets.newHashSet(CTTagEn.C_VP, CTTagEn.C_SINV, CTTagEn.C_SQ);
	private final Set<String> S_COMP_PARENT_A = Sets.newHashSet(CTTagEn.C_ADJP, CTTagEn.C_ADVP);
	private final Set<String> S_NMOD_PARENT	  = Sets.newHashSet(CTTagEn.C_NML, CTTagEn.C_NP, CTTagEn.C_NX, CTTagEn.C_WHNP);
	private final Set<String> S_POSS_PARENT	  = Sets.newHashSet(CTTagEn.C_NP, CTTagEn.C_NML, CTTagEn.C_WHNP, CTTagEn.C_QP, CTTagEn.C_ADJP);
	
	private final Set<String> S_COMPLM = Sets.newHashSet("that", "if", "whether");
	private final int SIZE_HEAD_FLAGS = 4;
	
	private Set<String> s_semTags;
	private Set<String> s_synTags;
	
	private Map<CTNode,Deque<CTNode>> m_rnr;
	private Map<CTNode,Deque<CTNode>> m_xsubj;
	private Map<String,Pattern>       m_coord;
	
	private CTNodeMatcher mt_s;
	private CTNodeMatcher mt_to;
	private CTNodeMatcher mt_pos;
	private CTNodeMatcher mt_sbj;
	private CTNodeMatcher mt_prd;
	private CTNodeMatcher mt_none;
	private CTNodeMatcher mt_in_dt;
	private CTNodeMatcher mt_np_prd;
	
	public EnglishC2DConverter(HeadRuleMap headrules)
	{
		super(headrules, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT, ".*"));
		
		initBasic();
		initCoord();
		initMatchers();
	}
	
	@Override
	public DEPTree toDEPTree(CTTree cTree)
	{
		CTLibEn.preprocess(cTree);
		clearMaps();
		if (!mapEmtpyCategories(cTree))	return null;
		setHeads(cTree.getRoot());
		
		return getDEPTree(cTree);
	}
	
// ============================= Initialization ============================= 
	
	private void initBasic()
	{
		s_semTags = Sets.newHashSet(CTTagEn.F_BNF, CTTagEn.F_DIR, CTTagEn.F_EXT, CTTagEn.F_LOC, CTTagEn.F_MNR, CTTagEn.F_PRP, CTTagEn.F_TMP, CTTagEn.F_VOC);
		s_synTags = Sets.newHashSet(CTTagEn.F_ADV, CTTagEn.F_CLF, CTTagEn.F_CLR, CTTagEn.F_DTV, CTTagEn.F_NOM, CTTagEn.F_PUT, CTTagEn.F_PRD, CTTagEn.F_TPC);
		m_rnr     = Maps.newHashMap();
		m_xsubj   = Maps.newHashMap();
	}
	
	private void initCoord()
	{
		m_coord = Maps.newHashMap();
		
		m_coord.put(CTTagEn.C_ADJP	, PatternUtils.createClosedORPattern("ADJP","JJ.*","VBN","VBG"));
		m_coord.put(CTTagEn.C_ADVP	, PatternUtils.createClosedORPattern("ADVP","RB.*"));
		m_coord.put(CTTagEn.C_INTJ	, PatternUtils.createClosedORPattern("INTJ","UH"));
		m_coord.put(CTTagEn.C_PP  	, PatternUtils.createClosedORPattern("PP","IN","VBG"));
		m_coord.put(CTTagEn.C_PRT 	, PatternUtils.createClosedORPattern("PRT","RP"));
		m_coord.put(CTTagEn.C_NAC 	, PatternUtils.createClosedORPattern("NP"));
		m_coord.put(CTTagEn.C_NML 	, PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		m_coord.put(CTTagEn.C_NP  	, PatternUtils.createClosedORPattern("NP","NML","NN.*","PRP"));
		m_coord.put(CTTagEn.C_NX  	, PatternUtils.createClosedORPattern("NX"));
		m_coord.put(CTTagEn.C_VP  	, PatternUtils.createClosedORPattern("VP","VB.*"));
		m_coord.put(CTTagEn.C_S   	, PatternUtils.createClosedORPattern("S","SINV","SQ","SBARQ"));
		m_coord.put(CTTagEn.C_SBAR	, PatternUtils.createClosedORPattern("SBAR.*"));
		m_coord.put(CTTagEn.C_SBARQ	, PatternUtils.createClosedORPattern("SBAR.*"));
		m_coord.put(CTTagEn.C_SINV	, PatternUtils.createClosedORPattern("S","SINV"));
		m_coord.put(CTTagEn.C_SQ	, PatternUtils.createClosedORPattern("S","SQ","SBARQ"));
		m_coord.put(CTTagEn.C_WHNP	, PatternUtils.createClosedORPattern("NN.*","WP"));
		m_coord.put(CTTagEn.C_WHADJP, PatternUtils.createClosedORPattern("JJ.*","VBN","VBG"));
		m_coord.put(CTTagEn.C_WHADVP, PatternUtils.createClosedORPattern("RB.*","WRB","IN"));
	}
	
	private void initMatchers()
	{
		mt_s		= new CTNodeMatcherC(CTTagEn.C_S);
		mt_to		= new CTNodeMatcherC(POSTagEn.POS_TO);
		mt_pos		= new CTNodeMatcherC(POSTagEn.POS_POS);
		mt_none		= new CTNodeMatcherC(CTLibEn.NONE);
		
		mt_sbj  	= new CTNodeMatcherF(CTTagEn.F_SBJ);
		mt_prd  	= new CTNodeMatcherF(CTTagEn.F_PRD);
		mt_np_prd	= new CTNodeMatcherCF(CTTagEn.C_NP, CTTagEn.F_PRD);
		mt_in_dt	= new CTNodeMatcherSet(Sets.newHashSet(POSTagEn.POS_IN, POSTagEn.POS_DT));
	}

	private void clearMaps()
	{
		m_rnr.clear();
		m_xsubj.clear();
	}
	
// ============================= Empty categories ============================= 
	
	/**
	 * Removes, relocates empty categories in the specific tree. 
	 * @param cTree the constituent tree to be processed.
	 * @return {@true} if the constituent tree contains nodes after relocating empty categories.
	 */
	private boolean mapEmtpyCategories(CTTree cTree)
	{
		for (CTNode node : cTree.getTerminalList())
		{
			if (!node.isEmptyCategory())	continue;
			if (node.getParent() == null)	continue;
			
			if      (node.wordFormStartsWith(CTTagEn.E_PRO))
				mapPRO(cTree, node);
			else if (node.wordFormStartsWith(CTTagEn.E_TRACE))
				mapTrace(cTree, node);
			else if (node.matchesWordForm(CTLibEn.P_PASSIVE_NULL))
				mapPassiveNull(cTree, node);
			else if (node.isWordForm(CTTagEn.E_ZERO))
				continue;
			else if (CTLibEn.isDiscontinuousConstituent(node))
				mapDiscontinuousConstituent(cTree, node);
//			else if (node.wordFormStartsWith(CTTagEn.E_EXP))
//				reloateEXP(cTree, node);
			else
				removeCTNode(node);
		}
		
		return cTree.getRoot().getChildrenSize() > 0;
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapPRO(CTTree cTree, CTNode ec)
	{
		CTNode np = ec.getParent();
		CTNode vp = np.getParent().getFirstLowestChainedDescendant(CTLibEn.M_VP);
		
		if (vp == null)		// small clauses
			relocatePRD(np, ec);
		else
		{
			CTNode ante;
			
			if ((ante = ec.getAntecedent()) != null && CTLibEn.isWhPhrase(ante))	// relative clauses
			{
				if (cTree.getEmptyCategoryList(ante.getEmptyCategoryIndex()).size() == 1)
					mapTrace(cTree, ec);
			}
			
			addXSubject(ec, m_xsubj);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapTrace(CTTree cTree, CTNode ec)
	{
		CTNode ante = ec.getAntecedent();
		
		if (ante == null || ec.isDescendantOf(ante))
			removeCTNode(ec);
		else if (ante.hasFunctionTag(CTTagEn.F_TPC))
		{
			if (!ante.hasFunctionTag(CTTagEn.F_SBJ))
			{
				CTNode parent = ec.getParent();
				parent.removeChild(ec);
				replaceEC(parent, ante);
			}
			else
				removeCTNode(ec);
		}
		else	// relative clauses
		{
			CTNode parent = ante.getHighestChainedAncestor(CTLibEn.M_SBAR);
			if (parent != null) parent.addFunctionTag(DEPLibEn.DEP_RCMOD);
			replaceEC(ec, ante);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapPassiveNull(CTTree cTree, CTNode ec)
	{
		CTNode np = ec.getParent();
		
		if (np.hasFunctionTag(CTTagEn.F_SBJ))
		{
			// small clauses
			if (np.getRightNearestSibling(CTLibEn.M_VP) == null)
				relocatePRD(np, ec);
			else
				addXSubject(ec, m_xsubj);
		}
	}
	
	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapDiscontinuousConstituent(CTTree cTree, CTNode ec)
	{
		CTNode parent = ec.getParent();
		CTNode ante   = ec.getAntecedent();
		
		if (ec.wordFormStartsWith(CTTagEn.E_ICH) && parent.getLeftNearestSibling(CTLibEn.M_WHx) != null)
			removeCTNode(ec);
		else if (ante == null || ec.isDescendantOf(ante))
			removeCTNode(ec);
		else
		{
			List<CTNode> list = cTree.getEmptyCategoryList(ante.getEmptyCategoryIndex());
			boolean isRNR = CTLibEn.isRNR(ec);
			int i, size = list.size();
			CTNode node;
			
			Deque<CTNode> dq = isRNR ? new ArrayDeque<CTNode>() : null;
			
			if (ec.getTerminalID() < ante.getFirstTerminal().getTerminalID())
			{		
				for (i=0; i<size-1; i++)
				{
					node = list.get(i);
					if (isRNR)	dq.addLast(node.getParent().getParent());
					removeCTNode(node);
				}
				
				ec = list.get(size-1);
			}
			else
			{
				for (i=size-1; i>0; i--)
				{
					node = list.get(i);
					if (isRNR)	dq.addFirst(node.getParent().getParent());
					removeCTNode(node);
				}
				
				ec = list.get(0);
			}
			
			if (isRNR && !dq.isEmpty())
				m_rnr.put(ante, dq);
			
			parent = ec.getParent();
			parent.removeChild(ec);
			replaceEC(parent, ante);
		}
	}
	
	/** Called by {@link #mapPRO(CTTree, CTNode)} and {@link #mapPassiveNull(CTTree, CTNode)}. */
	private void relocatePRD(CTNode np, CTNode ec)
	{
		CTNode s   = np.getParent();
		CTNode prd = s.getFirstChild(mt_prd);
		Set<String> fTags = s.getFunctionTagSet();
		
		if (prd != null && (fTags.isEmpty() || fTags.contains(CTTagEn.F_CLR)))
		{
			fTags.clear();
			fTags.add(DEPLibEn.DEP_OPRD);
		}

		removeCTNode(ec);
	}
	
/*	private void reloateEXP(CTTree cTree, CTNode ec)
	{
		int idx = ec.form.lastIndexOf("-");
		
		if (idx != -1)
		{
			int coIndex = Integer.parseInt(ec.form.substring(idx+1));
			CTNode ante = cTree.getCoIndexedAntecedent(coIndex);
			if (ante != null)	ante.addFTag(DEPLibEn.CONLL_EXTR);
		}
		
		removeCTNode(ec);
	}*/
	
	/**
	 * @param ec empty subject.
	 * @param map key: antecedent, value: list of clauses containing empty subjects.
	 */
	private void addXSubject(CTNode ec, Map<CTNode, Deque<CTNode>> map)
	{
		CTNode ante = ec.getAntecedent();
		
		while (ante != null && ante.isEmptyCategoryTerminal())
		{
			if (CTLibEn.isWhPhrase(ante)) return;
			ante = ante.getFirstTerminal().getAntecedent();
		}
		
		if (ante != null)
		{
			CTNode s = ec.getNearestAncestor(mt_s);
			
			if (s != null)
			{
				Deque<CTNode> dq = map.get(ante);
				if (dq == null)	dq = new ArrayDeque<CTNode>();
				
				dq.add(s);
				map.put(ante, dq);
			}
		}
	}
	
	private void removeCTNode(CTNode node)
	{
		CTNode parent = node.getParent();
	
		if (parent != null)
		{
			parent.removeChild(node);
			
			if (parent.getChildrenSize() == 0)
				removeCTNode(parent);			
		}
	}
	
	private void replaceEC(CTNode ec, CTNode ante)
	{
		removeCTNode(ante);
		ec.getParent().replaceChild(ec, ante);
	}
	
// ============================= Find heads =============================
	
	@Override
	protected void setHeadsAux(HeadRule rule, CTNode curr)
	{
		if (findHeadsCoordination(rule, curr))	return;
		
		findHyphens(curr);
		findHeadsApposition(curr);
		findHeadsSmallClause(curr);

		CTNode head = getHead(rule, curr.getChildrenList(), SIZE_HEAD_FLAGS);
		if (head.getC2DInfo().getLabel() != null) head.getC2DInfo().setLabel(null); 
		curr.setC2DInfo(new C2DInfo(head));
	}
	
	/**
	 * If the specific node contains a coordination structure, find the head of each coordination.
	 * @param curr the specific node to be compared. 
	 * @return {@code true} if this node contains a coordination structure.
	 */
	private boolean findHeadsCoordination(HeadRule rule, CTNode curr)
	{
		// skip pre-conjunctions and punctuation
		int i, sId, size = curr.getChildrenSize();
		CTNode node;
		
		for (sId=0; sId<size; sId++)
		{
			node = curr.getChild(sId);
			
			if (!CTLibEn.isPunctuation(node) && !CTLibEn.isConjunction(node) && !node.isEmptyCategoryTerminal())
				break;
		}
		
		if (!CTLibEn.containsCoordination(curr, curr.getChildrenList(sId)))
			return false;
		
		// find conjuncts
		Pattern rTags = getConjunctPattern(curr, sId, size);
		CTNode prevHead = null, mainHead = null;
		boolean isFound = false;
		int bId = 0, eId = sId;
		
		for (; eId<size; eId++)
		{
			node = curr.getChild(eId);
			
			if (CTLibEn.isCoordinator(node))
			{
				if (isFound)
				{
					prevHead = findHeadsCoordinationAux(rule, curr, bId, eId, prevHead);
					setHeadCoord(node, prevHead, getDEPLabel(node, curr, prevHead));
					if (mainHead == null)	mainHead = prevHead;
					isFound = false;
			
					bId = eId + 1;
				}
				else if (prevHead != null)
				{
					for (i=bId; i<=eId; i++)
					{
						node = curr.getChild(i);
						setHeadCoord(node, prevHead, getDEPLabel(node, curr, prevHead));
					}
					
					bId = eId + 1;
				}
			}
			else if (isConjunct(node, curr, rTags))
				isFound = true;
		}
		
		if (mainHead == null)	return false;
		
		if (eId - bId > 0)
			findHeadsCoordinationAux(rule, curr, bId, eId, prevHead);
		
		curr.setC2DInfo(new C2DInfo(mainHead));
		return true;
	}
	
	/** Called by {@link #findHeadsCoordination(HeadRule, CTNode)}. */
	private Pattern getConjunctPattern(CTNode curr, int sId, int size)
	{
		Pattern rTags = m_coord.get(curr.getConstituentTag());
		
		if (rTags != null)
		{
			boolean b = false;
			int i;
			
			for (i=sId; i<size; i++)
			{
				if (curr.getChild(i).matchesConstituentTag(rTags))
				{
					b = true;
					break;
				}
			}
			
			if (!b)	rTags = Pattern.compile(".*");
		}
		else
			rTags = Pattern.compile(".*");
		
		return rTags;
	}
	
	/** Called by {@link #findHeadsCoordination(HeadRule, CTNode)}. */
	private boolean isConjunct(CTNode C, CTNode P, Pattern rTags)
	{
		if (P.isConstituentTag(CTTagEn.C_SBAR) && C.isConstituentTagAny(S_PREP_DET))
			return false;
		else if (rTags.pattern().equals(".*"))
			return getSpecialLabel(C) == null;
		else if (rTags.matcher(C.getConstituentTag()).find())
		{
			if (P.isConstituentTag(CTTagEn.C_VP) && getAuxLabel(C) != null)
				return false;
			
			if (CTLibEn.isMainClause(P) && C.isConstituentTag(CTTagEn.C_S) && hasAdverbialTag(C))
				return false;
			
			return true;
		}
		else if (P.isConstituentTag(CTTagEn.C_NP))
		{
			return C.hasFunctionTag(CTTagEn.F_NOM);
		}
		
		return false;
	}
	
	/** Called by {@link #findHeadsCoordination(HeadRule, CTNode)}. */
	private CTNode findHeadsCoordinationAux(HeadRule rule, CTNode curr, int bId, int eId, CTNode lastHead)
	{
		CTNode currHead = (eId - bId == 1) ? curr.getChild(bId) : getHead(rule, curr.getChildrenList(bId, eId), SIZE_HEAD_FLAGS);
		
		if (lastHead != null)
		{
			String label = DEPLibEn.DEP_CONJ;
			
			if (isIntj(currHead))						label = DEPLibEn.DEP_INTJ;
			else if (CTLibEn.isPunctuation(currHead))	label = DEPLibEn.DEP_PUNCT;

			setHeadCoord(currHead, lastHead, label);
		}
		
		return currHead;
	}
	
	private void setHeadCoord(CTNode node, CTNode head, String label)
	{
		node.getC2DInfo().setHead(head, label, head.isTerminal());
	}
	
	private boolean findHyphens(CTNode node)
	{
		int i, size = node.getChildrenSize();
		CTNode prev, hyph, next;
		boolean isFound = false;
		boolean isVP = node.isConstituentTag(CTTagEn.C_VP);
		
		for (i=0; i<size-2; i++)
		{
			prev = node.getChild(i);
			hyph = node.getChild(i+1);
			next = node.getChild(i+2);
			
			if (hyph.isConstituentTag(CTLibEn.POS_HYPH))
			{
				if (isVP)
				{
					prev.getC2DInfo().setLabel(DEPLibEn.DEP_HMOD);
					hyph.getC2DInfo().setLabel(DEPLibEn.DEP_HYPH);
					next.getC2DInfo().setLabel(DEPLibEn.DEP_HMOD);
				}
				else
				{
					prev.getC2DInfo().setHead(next, DEPLibEn.DEP_HMOD);
					hyph.getC2DInfo().setHead(next, DEPLibEn.DEP_HYPH);
				}
				
				isFound = true;
				i++;
			}
		}
		
		return isFound;
	}
	
	
	/**
	 * Finds the head of appositional modifiers.
	 * @param curr the constituent node to be processed.
	 * @return {@code true} if the specific node contains appositional modifiers. 
	 */
	private boolean findHeadsApposition(CTNode curr)
	{
		if (!curr.isConstituentTagAny(S_NOUN_PHRASE) || curr.containsChild(CTLibEn.M_NNx))
			return false;
		
		CTNode fst = curr.getFirstChild(CTLibEn.M_NP_NML);
		while (fst != null && fst.containsChild(mt_pos))
			fst = fst.getRightNearestSibling(CTLibEn.M_NP_NML);
		
		if (fst == null || fst.getC2DInfo().hasHead())	return false;

		boolean hasAppo = false;
		CTNode snd = fst;
		
		while ((snd = snd.getRightSibling()) != null)
		{
			if (snd.getC2DInfo().hasHead())	continue;
			
			if ((snd.isConstituentTagAny(S_NOUN_PHRASE) && !hasAdverbialTag(snd)) ||
				(snd.hasFunctionTagAny(CTTagEn.F_HLN, CTTagEn.F_TTL)) ||
				(snd.isConstituentTag(CTTagEn.C_RRC) && snd.containsChild(mt_np_prd)))
			{
				snd.getC2DInfo().setHead(fst, DEPLibEn.DEP_APPOS);
				hasAppo = true;
			}
		}
		
		return hasAppo;
	}

	private boolean findHeadsSmallClause(CTNode node)
	{
		CTNode parent = node.getParent();
		
		if (node.isConstituentTag(CTTagEn.C_S) && !node.containsChild(CTLibEn.M_VP))
		{
			CTNode sbj = node.getFirstChild(mt_sbj);
			CTNode prd = node.getFirstChild(mt_prd);
			
			if (sbj != null && prd != null)
			{
				if (parent.isConstituentTag(CTTagEn.C_SQ))
				{
					CTNode vb = parent.getFirstChild(CTLibEn.M_VBx);
					
					if (vb != null)
					{
						sbj.getC2DInfo().setHead(vb, getDEPLabel(sbj, parent, vb));
						node.setConstituentTag(prd.getConstituentTag());
						node.addFunctionTag(CTTagEn.F_PRD);
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected int getHeadFlag(CTNode child)
	{
		C2DInfo info = child.getC2DInfo();
		
		if (info.hasHead())// && info.getTerminalHead() != info.getNonTerminalHead())
			return -1;
		
		if (hasAdverbialTag(child))
			return 1;
		
		if (isMeta(child))
			return 2;
		
		if (child.isEmptyCategoryTerminal() || CTLibEn.isPunctuation(child))
			return 3;
		
		return 0;
	}
	
	// ============================= Get labels ============================= 
	
	@Override
	protected String getDEPLabel(CTNode C, CTNode P, CTNode p)
	{
		CTNode c = C.getC2DInfo().getNonTerminalHead();
		CTNode d = C.getC2DInfo().getTerminalHead();
		String label;
		
		// function tags
		if (hasAdverbialTag(C))
		{
			if (C.isConstituentTagAny(S_ADVCL))
				return DEPLibEn.DEP_ADVCL;
			
			if (C.isConstituentTagAny(S_NPADVMOD))
				return DEPLibEn.DEP_NPADVMOD;
		}
		
		if ((label = getSubjectLabel(C, d)) != null)
			return label;
		
		// coordination
		if (C.isConstituentTag(CTTagEn.C_UCP))
		{
			c.addFunctionTags(C.getFunctionTagSet());
			return getDEPLabel(c, P, p);
		}
		
		// complements
		if (P.isConstituentTagAny(S_COMP_PARENT_S))
		{
			if (isAcomp(C))	return DEPLibEn.DEP_ACOMP;
			if ((label = getObjectLabel(C)) != null) return label;
			if (isOprd(C))	return DEPLibEn.DEP_OPRD;
			if (isXcomp(C))	return DEPLibEn.DEP_XCOMP;
			if (isCcomp(C))	return DEPLibEn.DEP_CCOMP;
			if ((label = getAuxLabel(C)) != null) return label;
		}
		
		if (P.isConstituentTagAny(S_COMP_PARENT_A))
		{
			if (isXcomp(C))	return DEPLibEn.DEP_XCOMP;
			if (isCcomp(C))	return DEPLibEn.DEP_CCOMP;
		}
		
		if (P.isConstituentTagAny(S_NFMOD))
		{
			if (isNfmod(C))	return isInfMod(C) ? DEPLibEn.DEP_INFMOD : DEPLibEn.DEP_PARTMOD;
			if (isRcmod(C))	return DEPLibEn.DEP_RCMOD;
			if (isCcomp(C))	return DEPLibEn.DEP_CCOMP;
		}
		
		if (isPoss(C, P))
			return DEPLibEn.DEP_POSS;
		
		// simple labels
		if ((label = getSimpleLabel(C)) != null)
			return label;
			
		// default
		if (P.isConstituentTagAny(S_PREP_PHRASE))
		{
			if (p.getParent() == C.getParent())	// p and C are siblings
			{
				if (p.isLeftSiblingOf(C))
					return getPmodLabel(C, d);
			}
			else								// UCP
			{
				if (p.getFirstTerminal().getTerminalID() < C.getFirstTerminal().getTerminalID())
					return getPmodLabel(C, d);
			}
		}
		
		if (C.isConstituentTag(CTTagEn.C_SBAR) || isXcomp(C) || (P.isConstituentTag(CTTagEn.C_PP) && CTLibEn.isClause(C)))
			return DEPLibEn.DEP_ADVCL;
		
		if (C.isConstituentTagAny(S_CCOMP))
			return DEPLibEn.DEP_CCOMP;
		
		if (P.isConstituentTag(CTTagEn.C_QP))
		{
			if (C.isConstituentTag(CTLibEn.POS_CD))
				return DEPLibEn.DEP_NUMBER;
			else
				return DEPLibEn.DEP_QUANTMOD;
		}
		
		if (P.isConstituentTagAny(S_NMOD_PARENT) || CTLibEn.isNoun(p))
			return getNmodLabel(C);
		
		if (c != null)
		{
			if ((label = getSimpleLabel(c)) != null)
				return label;
			
			if (d.isConstituentTag(CTLibEn.POS_IN))
				return DEPLibEn.DEP_PREP;
			
			if (CTLibEn.isAdverb(d))
				return DEPLibEn.DEP_ADVMOD;
		}
		
		if ((P.isConstituentTagAny(S_ADVB_PHRASE) || CTLibEn.isAdjective(p) || CTLibEn.isAdverb(p)))
		{
			if (C.isConstituentTagAny(S_NPADVMOD) || CTLibEn.isNoun(C))
				return DEPLibEn.DEP_NPADVMOD;
			
			return DEPLibEn.DEP_ADVMOD;
		}
		
		if (d.hasC2DInfo() && (label = d.getC2DInfo().getLabel()) != null)
			return label;
		
		return DEPLibEn.DEP_DEP;
	}
	
	private boolean hasAdverbialTag(CTNode node)
	{
		return node.hasFunctionTag(CTTagEn.F_ADV) || DSUtils.hasIntersection(node.getFunctionTagSet(), s_semTags);
	}
	
	private String getObjectLabel(CTNode node)
	{
		if (node.isConstituentTagAny(S_NOUN_PHRASE))
		{
			if (node.hasFunctionTag(CTTagEn.F_PRD))
				return DEPLibEn.DEP_ATTR;
			else
				return DEPLibEn.DEP_DOBJ;
		}
		
		return null;
	}
	
	private String getSubjectLabel(CTNode C, CTNode d)
	{
		if (C.hasFunctionTag(CTTagEn.F_SBJ))
		{
			if (CTLibEn.isClause(C))
				return DEPLibEn.DEP_CSUBJ;
			else if (d.isConstituentTag(CTLibEn.POS_EX))
				return DEPLibEn.DEP_EXPL;
			else
				return DEPLibEn.DEP_NSUBJ;
		}
		else if (C.hasFunctionTag(CTTagEn.F_LGS))
			return DEPLibEn.DEP_AGENT;
		
		return null;
	}
	
	private String getSimpleLabel(CTNode C)
	{
		String label;
		
		if (isHyph(C))
			return DEPLibEn.DEP_HYPH;
		
		if (isAmod(C))
			return DEPLibEn.DEP_AMOD;
		
		if (C.isConstituentTagAny(S_PREP_PHRASE))
			return DEPLibEn.DEP_PREP;
		
		if (CTLibEn.isCorrelativeConjunction(C))
			return DEPLibEn.DEP_PRECONJ;
		
		if (CTLibEn.isConjunction(C))
			return DEPLibEn.DEP_CC;
		
		if (isPrt(C))
			return DEPLibEn.DEP_PRT;

		if ((label = getSpecialLabel(C)) != null)
			return label;
		
		return null;
	}
	
	private String getSpecialLabel(CTNode C)
	{
		CTNode d = C.getC2DInfo().getTerminalHead();
		
		if (CTLibEn.isPunctuation(C) || CTLibEn.isPunctuation(d))
			return DEPLibEn.DEP_PUNCT;
		
		if (isIntj(C) || isIntj(d))
			return DEPLibEn.DEP_INTJ;
		
		if (isMeta(C))
			return DEPLibEn.DEP_META;
		
		if (isPrn(C))
			return DEPLibEn.DEP_PARATAXIS;

		if (isAdv(C))
			return DEPLibEn.DEP_ADVMOD;
		
		return null;
	}
	
	private String getAuxLabel(CTNode node)
	{
		if (node.isConstituentTagAny(S_AUX))
			return DEPLibEn.DEP_AUX;

		CTNode vp;
		
		if (CTLibEn.isVerb(node) && (vp = node.getRightNearestSibling(CTLibEn.M_VP)) != null)
		{
			if (ENUtils.isPassiveAuxiliaryVerb(node.getWordForm()))
			{
				if (vp.containsChild(CTLibEn.M_VBD_VBN))
					return DEPLibEn.DEP_AUXPASS;
				
				if (!vp.containsChild(CTLibEn.M_VBx) && (vp = vp.getFirstChild(CTLibEn.M_VP)) != null && vp.containsChild(CTLibEn.M_VBD_VBN))
					return DEPLibEn.DEP_AUXPASS;
			}
			
			return DEPLibEn.DEP_AUX;
		}
		
		return null;
	}
	
	private String getNmodLabel(CTNode node)
	{
		if (node.isConstituentTagAny(S_PARTICIPIAL))
			return DEPLibEn.DEP_AMOD;
		
		if (node.isConstituentTagAny(S_DET))
			return DEPLibEn.DEP_DET;
		
		if (node.isConstituentTagAny(S_NN) || node.matches(CTLibEn.M_NNx))
			return DEPLibEn.DEP_NN;
		
		if (node.isConstituentTagAny(S_NUM))
			return DEPLibEn.DEP_NUM;

		if (node.isConstituentTag(CTLibEn.POS_POS))
			return DEPLibEn.DEP_POSSESSIVE;
		
		if (node.isConstituentTag(CTLibEn.POS_PDT))
			return DEPLibEn.DEP_PREDET;
		
		return DEPLibEn.DEP_NMOD;
	}
	
	private String getPmodLabel(CTNode C, CTNode d)
	{
		if (C.isConstituentTagAny(S_NOUN_PHRASE) || CTLibEn.isRelativizer(d))
			return DEPLibEn.DEP_POBJ;
		else
			return DEPLibEn.DEP_PCOMP;	
	}
	
	private boolean isHyph(CTNode node)
	{
		return node.isConstituentTag(CTLibEn.POS_HYPH);
	}
	
	private boolean isAmod(CTNode node)
	{
		return node.isConstituentTagAny(S_ADJT_PHRASE) || CTLibEn.isAdjective(node);
	}
	
	private boolean isAdv(CTNode C)
	{
		if (C.isConstituentTag(CTTagEn.C_ADVP) || CTLibEn.isAdverb(C))
		{
			CTNode P = C.getParent();
			
			if (P.isConstituentTagAny(S_PREP_PHRASE) && C.getRightSibling() == null && C.getLeftSibling().isConstituentTagAny(S_PREPOSITION))
				return false;

			return true;
		}
		
		return false;
	}
	
	private boolean isIntj(CTNode node)
	{
		return node.isConstituentTagAny(S_INTJ);
	}
	
	private boolean isMeta(CTNode node)
	{
		return node.isConstituentTagAny(S_META);
	}
	
	private boolean isPrn(CTNode node)
	{
		return node.isConstituentTag(CTTagEn.C_PRN);
	}
	
	private boolean isPrt(CTNode curr)
	{
		return curr.isConstituentTagAny(S_PRT);
	}
	
	private boolean isAcomp(CTNode node)
	{
		return node.isConstituentTag(CTTagEn.C_ADJP);
	}
	
	private boolean isOprd(CTNode curr)
	{
		if (curr.hasFunctionTag(DEPLibEn.DEP_OPRD))
			return true;
		
		if (curr.isConstituentTag(CTTagEn.C_S) && !curr.containsChild(CTLibEn.M_VP) && curr.containsChild(mt_prd))
		{
			CTNode sbj = curr.getFirstChild(mt_sbj);
			return sbj != null && sbj.isEmptyCategoryTerminal();
		}
		
		return false;
	}
	
	private boolean isPoss(CTNode curr, CTNode parent)
	{
		if (curr.isConstituentTagAny(S_POSS))
			return true;
		
		if (parent.isConstituentTagAny(S_POSS_PARENT))
			return curr.containsChild(mt_pos);
		
		return false;
	}
	
	private boolean isXcomp(CTNode node)
	{
		if (node.isConstituentTag(CTTagEn.C_S))
		{
			CTNode sbj = node.getFirstChild(mt_sbj);
			
			if (node.containsChild(CTLibEn.M_VP) && (sbj == null || sbj.isEmptyCategoryTerminal()))
				return true;
		}
		else if (node.hasFunctionTag(DEPLibEn.DEP_RCMOD))
		{
			CTNode s = node.getFirstChild(mt_s);
			if (s != null)	return isXcomp(s);
		}

		return false;
	}
	
	private boolean isCcomp(CTNode node)
	{
		if (node.isConstituentTagAny(S_CCOMP))
			return true;
		
		if (node.isConstituentTag(CTTagEn.C_SBAR))
		{
			CTNode comp;
			
			if ((comp = node.getFirstChild(mt_none)) != null && comp.isWordForm(CTTagEn.E_ZERO))
				return true;
			
			if ((comp = node.getFirstChild(mt_in_dt)) != null)
			{
				if (isComplm(comp))
				{
					comp.getC2DInfo().setLabel(DEPLibEn.DEP_COMPLM);
					return true;
				}
			}
			
			if (node.hasFunctionTag(DEPLibEn.DEP_RCMOD) || node.containsChild(CTLibEn.M_WHx))
				return true;
		}
		
		return false;
	}
	
	private boolean isNfmod(CTNode curr)
	{
		return isXcomp(curr) || curr.isConstituentTag(CTTagEn.C_VP);
	}
	
	private boolean isInfMod(CTNode curr)
	{
		CTNode vp = curr.isConstituentTag(CTTagEn.C_VP) ? curr : curr.getFirstDescendant(CTLibEn.M_VP);
		
		if (vp != null)
		{
			CTNode vc = vp.getFirstChild(CTLibEn.M_VP);
			
			while (vc != null)
			{
				vp = vc;
				
				if (vp.getLeftNearestSibling(mt_to) != null)
					return true;
				
				vc = vp.getFirstChild(CTLibEn.M_VP);
			}
			
			return vp.containsChild(mt_to);
		}
		
		return false;
	}
	
	private boolean isRcmod(CTNode curr)
	{
		return curr.isConstituentTag(CTTagEn.C_RRC) || curr.hasFunctionTag(DEPLibEn.DEP_RCMOD) || (curr.isConstituentTag(CTTagEn.C_SBAR) && curr.containsChild(CTLibEn.M_WHx));
	}
	
	private boolean isComplm(CTNode curr)
	{
		return S_COMPLM.contains(StringUtils.toLowerCase(curr.getWordForm()));
	}
	
	// ============================= Get a dependency tree =============================
	
	private DEPTree getDEPTree(CTTree cTree)
	{
		DEPTree dTree = initDEPTree(cTree);
		addDEPHeads(dTree, cTree);
		
		if (dTree.containsCycle())
			throw new UnknownFormatConversionException("Cyclic depedency relation.");

		DEPLibEn.enrichLabels(dTree);
		addSecondaryHeads(dTree);
		addFeats(dTree, cTree, cTree.getRoot());
		
		if (cTree.hasPropBank())
			addSemanticHeads(dTree, cTree);
		
		return getDEPTreeWithoutEdited(cTree, dTree);
	}
	
	/** Adds dependency heads. */
	private int addDEPHeads(DEPTree dTree, CTTree cTree)
	{
		int currId, headId, size = dTree.size(), rootCount = 0;
		CTNode cNode, ante;
		DEPNode dNode;
		String label;
		
		for (currId=1; currId<size; currId++)
		{
			dNode  = dTree.get(currId);
			cNode  = cTree.getToken(currId-1);
			headId = cNode.getC2DInfo().getTerminalHead().getTokenID() + 1;
			
			if (currId == headId)	// root
			{
				dNode.setHead(dTree.get(DEPLib.ROOT_ID), DEPLibEn.DEP_ROOT);
				rootCount++;
			}
			else
			{
				label = cNode.getC2DInfo().getLabel();
				
				if (cNode.isConstituentTagAny(S_MARK) && cNode.getParent().isConstituentTag(CTTagEn.C_SBAR) && !label.equals(DEPLibEn.DEP_COMPLM))
					label = DEPLibEn.DEP_MARK;
				
				dNode.setHead(dTree.get(headId), label);
			}
			
			if ((ante = cNode.getAntecedent()) != null)
				dNode.addSecondaryHead(getDEPNode(dTree, ante), DEPTagEn.DEP2_REF);
		}
		
		return rootCount;
//		if (rootCount > 1)	System.err.println("Warning: multiple roots exist");
	}
	
	/** Called by {@link #getDEPTree(CTTree)}. */
	private void addSecondaryHeads(DEPTree dTree)
	{
		for (CTNode curr : m_xsubj.keySet())
		{
			if (curr.hasC2DInfo())
				addSecondaryHeadsAux(dTree, curr, m_xsubj.get(curr), DEPTagEn.DEP2_XSUBJ);
		}
		
		for (CTNode curr : m_rnr.keySet())
		{
			if (curr.getParent() == null)
				continue;
			
			if (curr.getParent().getC2DInfo().getNonTerminalHead() != curr)
				addSecondaryHeadsAux(dTree, curr, m_rnr.get(curr), DEPTagEn.DEP2_RNR);
			else
				addSecondaryChildren(dTree, curr, m_rnr.get(curr), DEPTagEn.DEP2_RNR);
		}
	}
	
	/** Called by {@link #addSecondaryHeads(DEPTree)}. */
	private void addSecondaryHeadsAux(DEPTree dTree, CTNode cNode, Deque<CTNode> dq, String label)
	{
		if (cNode.isEmptyCategoryTerminal()) return;
		DEPNode node = getDEPNode(dTree, cNode);
		DEPNode head;
		
		for (CTNode cHead : dq)
		{
			head = getDEPNode(dTree, cHead);
			if (!node.isDependentOf(head)) node.addSecondaryHead(head, label);
			
			if (label.equals(DEPTagEn.DEP2_XSUBJ) && head.isLabel(DEPLibEn.DEP_CCOMP))
				head.setLabel(DEPLibEn.DEP_XCOMP);
		}
	}
	
	/** Called by {@link #addSecondaryHeads(DEPTree)}. */
	private void addSecondaryChildren(DEPTree dTree, CTNode cHead, Deque<CTNode> dq, String label)
	{
		DEPNode head = getDEPNode(dTree, cHead);
		DEPNode node;
		
		for (CTNode cNode : dq)
		{
			node = getDEPNode(dTree, cNode);
			node.addSecondaryHead(head, label);			
		}
	}
	
	/** Called by {@link #getDEPTree(CTTree)}. */
	private void addFeats(DEPTree dTree, CTTree cTree, CTNode cNode)
	{
		CTNode ante;
		String feat;
		
		if (!cNode.isEmptyCategoryTerminal() && cNode.getGappingRelationIndex() != -1 && cNode.getParent().getGappingRelationIndex() == -1 && (ante = cTree.getAntecedent(cNode.getGappingRelationIndex())) != null)
		{
			DEPNode dNode = getDEPNode(dTree, cNode);
			dNode.addSecondaryHead(getDEPNode(dTree, ante), DEPTagEn.DEP2_GAP);
		}
		
		if ((feat = getFunctionTags(cNode, s_semTags)) != null)
			cNode.getC2DInfo().putFeat(DEPLib.FEAT_SEM, feat);
		
		if ((feat = getFunctionTags(cNode, s_synTags)) != null)
			cNode.getC2DInfo().putFeat(DEPLib.FEAT_SYN, feat);

		for (CTNode child : cNode.getChildrenList())
			addFeats(dTree, cTree, child);
	}
	
	/** Called by {@link #addFeats(DEPTree, CTTree, CTNode)}. */
	private String getFunctionTags(CTNode node, Set<String> sTags)
	{
		List<String> tags = Lists.newArrayList();
		
		for (String tag : node.getFunctionTagSet())
		{
			if (sTags.contains(tag))
				tags.add(tag);
		}
		
		if (tags.isEmpty())	return null;
		Collections.sort(tags);

		StringBuilder build = new StringBuilder();
		
		for (String tag : tags)
		{
			build.append(DEPFeat.DELIM_VALUES);
			build.append(tag);
		}
		
		return build.substring(DEPFeat.DELIM_VALUES.length());
	}
	
	private DEPNode getDEPNode(DEPTree dTree, CTNode cNode)
	{
		if (cNode.isConstituentTag(CTTagEn.TOP)) return null;
		CTNode cHead = cNode.isTerminal() ? cNode : cNode.getC2DInfo().getTerminalHead();
		return cHead.isEmptyCategory() ? null : dTree.get(cHead.getTokenID()+1);
//		return cNode.isTerminal() ? dTree.get(cNode.getTokenID()+1) : dTree.get(cNode.getC2DInfo().getTerminalHead().getTokenID()+1);
	}
	
// ============================= Edited phrases =============================
	
	public DEPTree getDEPTreeWithoutEdited(CTTree cTree, DEPTree dTree)
	{
		IntHashSet set = new IntHashSet();
		DEPTree tree = new DEPTree();
		int id = 1;
			
		addEditedTokensAux(cTree.getRoot(), set);
			
		for (DEPNode node : dTree)
		{
			if (!set.contains(node.getID()))
			{
				removeEditedHeads(node.getSecondaryHeadArcList(), set);
				removeEditedHeads(node.getSemanticHeadArcList() , set);
				node.setID(id++);
				tree.add(node);
			}
		}
			
		return (tree.size() > 1) ? tree : null;
	}
		
	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private void addEditedTokensAux(CTNode curr, IntHashSet set)
	{
		for (CTNode child : curr.getChildrenList())
		{
			if (CTLibEn.isEditedPhrase(child))
			{
				for (CTNode sub : child.getTokenList())
					set.add(sub.getTokenID()+1);
			}
			else if (!child.isTerminal())
				addEditedTokensAux(child, set);
		}
	}
		
	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private <T extends AbstractArc<DEPNode>>void removeEditedHeads(List<T> heads, IntHashSet set)
	{
		if (heads == null) return;
		List<T> remove = Lists.newArrayList();
		
		for (T arc : heads)
		{
			if (set.contains(arc.getNode().getID()))
			remove.add(arc);
		}
		
		heads.removeAll(remove);
	}	
	
	// ============================= Add PropBank arguments =============================
	
	private void addSemanticHeads(DEPTree dTree, CTTree cTree)
	{
		dTree.initSemanticHeads();
		initPropBank(dTree, cTree.getRoot());
		arrangePropBank(dTree);
		relabelNumberedArguments(dTree);
	}
	
	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
	private void initPropBank(DEPTree dTree, CTNode cNode)
	{
		DEPNode dNode = getDEPNode(dTree, cNode);
		
		if (dNode != null)
		{
			if (cNode.isPBHead())
				dNode.setRolesetID(cNode.getPBRolesetID());
			
			DEPNode sHead, d;
			String  label;
			CTNode  c;
			
			for (PBArc p : cNode.getPBHeads())
			{
				sHead = getDEPNode(dTree, p.getNode());
				label = PBLib.getShortLabel(p.getLabel());
				
				if ((c = getReferentArgument(cNode)) != null)
				{
					if ((c = CTLibEn.getRelativizer(c)) != null && (c = c.getAntecedent()) != null)
					{
						d = getDEPNode(dTree, c);
						
						if (d != null && d.getSemanticHeadArc(sHead) == null)
							d.addSemanticHead(new SRLArc(sHead, label));
					}
					
					label = PBLib.PREFIX_REFERENT + label;
				}
				
				if (!dNode.isArgumentOf(sHead) && dNode != sHead)
					dNode.addSemanticHead(sHead, label);
			}	
		}
		
		for (CTNode child : cNode.getChildrenList())
			initPropBank(dTree, child);
	}
	
	/** Called by {@link #initPropBank(DEPTree, CTNode)}. */
	private CTNode getReferentArgument(CTNode node)
	{
		CTNode ref;
		
		if ((ref = CTLibEn.getWhPhrase(node)) != null)
			return ref;
		
		if (node.isConstituentTag(CTTagEn.C_PP))
		{
			for (CTNode child : node.getChildrenList()) 
			{
				if ((ref = CTLibEn.getWhPhrase(child)) != null)
					return ref;
			}
		}

		return null;
	}
	
	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
	private void arrangePropBank(DEPTree tree)
	{
		List<SRLArc> remove;
		DEPNode head;
		String label;
		
		for (DEPNode node : tree)
		{
			remove = Lists.newArrayList();
			
			for (SRLArc arc : node.getSemanticHeadArcList())
			{
				head  = arc.getNode();
				label = arc.getLabel();
				
				if (ancestorHasSemanticHead(node, head, label))
					remove.add(arc);
			//	else if (rnrHasSHead(node, head, label))
			//		remove.add(arc);
			}
			
			node.removeSemanticHeads(remove);
		}
	}
	
	/** Called by {@link #arrangePropBank(DEPTree)}. */
	private boolean ancestorHasSemanticHead(DEPNode dNode, DEPNode sHead, String label)
	{
		DEPNode dHead = dNode.getHead();
		
		while (dHead.getID() != DEPLib.ROOT_ID)
		{
			if (dHead.isArgumentOf(sHead, label))
				return true;
			
			dHead = dHead.getHead();
		}
		
		return false;
	}
	
//	private boolean rnrHasSHead(DEPNode dNode, DEPNode sHead, String label)
//	{
//		for (DEPArc rnr : dNode.getSecondaryHeadList(DEPTagEn.DEP2_RNR))
//		{
//			if (rnr.getNode().isArgumentOf(sHead, label))
//				return true;
//		}
//		
//		return false;
//	}
	
	/** Called by {@link #addSemanticHeads(DEPTree, CTTree)}. */
	private void relabelNumberedArguments(DEPTree tree)
	{
		Map<String,DEPNode> map = Maps.newHashMap();
		String key;
		
		for (DEPNode node : tree)
		{
			for (SRLArc arc : node.getSemanticHeadArcList())
			{
				if (PBLib.isReferentArgument(arc.getLabel()))
					continue;
								
				if (PBLib.isModifier(arc.getLabel()))
					continue;
				
				key = arc.toString();
				
				if (map.containsKey(key))
					arc.setLabel(PBLib.PREFIX_CONCATENATION + arc.getLabel());
				else
					map.put(key, node);
			}
		}
	}
}