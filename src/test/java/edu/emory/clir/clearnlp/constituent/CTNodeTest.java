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
package edu.emory.clir.clearnlp.constituent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import edu.emory.clir.clearnlp.constituent.CTNode;
import edu.emory.clir.clearnlp.constituent.CTTagEn;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherC;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherCFa;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherCFo;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherCo;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherF;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherFa;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherFo;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherP;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherPFa;
import edu.emory.clir.clearnlp.constituent.matcher.CTNodeMatcherPFo;
import edu.emory.clir.clearnlp.util.PatternUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTNodeTest
{
	@Test
	public void testGetters()
	{
		CTNode curr = new CTNode("NP-PRD-1-LOC=2");
		testGetTags(curr);

		CTNode[] children = new CTNode[4];
		children[0] = new CTNode("NP-LOC");
		children[1] = new CTNode("CC", "and");
		children[2] = new CTNode("NP-TMP");
		children[3] = new CTNode("PP-LOC-PRD");
		for (CTNode child : children) curr.addChild(child);
		
		testGetChildren(curr, children);
		
		CTNode gChild = new CTNode("-NONE-", "*ICH*");
		children[0].addChild(new CTNode("-NONE-", "*"));
		children[2].addChild(new CTNode("NNP", "Jinho"));
		children[2].addChild(gChild);
		children[3].addChild(new CTNode("PP"));
		
		testGetAncestors(curr, children, gChild);
		testGetDescendants(curr, children);
		testGetSiblings(curr, children);
		testGetSubtree(curr);
		testGetEmptyCategories(curr);
		
		assertEquals(2, gChild.getDistanceToTop());
	}
	
	private void testGetTags(CTNode curr)
	{
		assertEquals("NP-LOC-PRD-1=2", curr.getTags());
		assertEquals("[LOC, PRD]", curr.getFunctionTagSet().toString());
		assertEquals(1, curr.getEmptyCategoryIndex());
		assertEquals(2, curr.getGappingRelationIndex());
	}
	
	private void testGetChildren(CTNode curr, CTNode[] children)
	{
		Pattern p = PatternUtils.createClosedORPattern("PP","CC");
		List<CTNode> list;
		
		list = curr.getChildrenList(new CTNodeMatcherC("NP"));
		assertEquals("[(NP-LOC null), (NP-TMP null)]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherCo("PP", "CC"));
		assertEquals("[(CC and), (PP-LOC-PRD null)]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherP(p));
		assertEquals("[(CC and), (PP-LOC-PRD null)]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherFa("LOC", "PRD"));
		assertEquals("[(PP-LOC-PRD null)]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherFo("TMP", "PRD"));
		assertEquals("[(NP-TMP null), (PP-LOC-PRD null)]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherCFa("NP","LOC","PRD"));
		assertEquals("[]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherCFo("NP","LOC","PRD"));
		assertEquals("[(NP-LOC null)]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherPFa(p, "TMP","PRD"));
		assertEquals("[]", list.toString());
		
		list = curr.getChildrenList(new CTNodeMatcherPFo(p, "TMP","PRD"));
		assertEquals("[(PP-LOC-PRD null)]", list.toString());
		
		assertEquals(null, curr.getChild(-1));
		assertEquals(null, curr.getChild(4));
		assertEquals(children[0], curr.getFirstChild());
		assertEquals(children[3], curr.getLastChild());
	}
	
	private void testGetAncestors(CTNode curr, CTNode[] children, CTNode gChild)
	{
		CTNode node;
		
		node = children[0].getParent();
		assertEquals(curr, node);
		
		node = gChild.getNearestAncestor(new CTNodeMatcherF("TMP"));
		assertEquals(children[2], node);
		
		node = gChild.getNearestAncestor(new CTNodeMatcherF("LOC"));
		assertEquals(curr, node);
		
		node = gChild.getNearestAncestor(new CTNodeMatcherF("BNF"));
		assertEquals(null, node);
		
		node = gChild.getHighestChainedAncestor(new CTNodeMatcherF("LOC"));
		assertEquals(null, node);
		
		node = gChild.getHighestChainedAncestor(new CTNodeMatcherF("TMP"));
		assertEquals(children[2], node);
		
		node = gChild.getHighestChainedAncestor(new CTNodeMatcherFo("LOC","TMP"));
		assertEquals(curr, node);

		node = children[1].getLowestCommonAncestor(gChild);
		assertEquals(curr, node);
		
		node = curr.getLowestCommonAncestor(gChild);
		assertEquals(curr, node);
		
		node = gChild.getLowestCommonAncestor(curr);
		assertEquals(curr, node);
	}
	
	private void testGetSiblings(CTNode curr, CTNode[] children)
	{
		CTNode node;
		
		node = children[3].getLeftNearestSibling(new CTNodeMatcherFo("TMP"));
		assertEquals(children[2], node);
		
		node = children[3].getLeftNearestSibling(new CTNodeMatcherFo("LOC"));
		assertEquals(children[0], node);
		
		node = children[0].getRightNearestSibling(new CTNodeMatcherFo("PRD"));
		assertEquals(children[3], node);
	}
	
	private void testGetDescendants(CTNode curr, CTNode[] children)
	{
		CTNode node;
		
		node = curr.getFirstDescendant(new CTNodeMatcherC("NP"));
		assertEquals(children[0], node);
		
		node = curr.getFirstDescendant(new CTNodeMatcherC("NNP"));
		assertEquals(children[2].getFirstChild(), node);
		
		node = curr.getFirstLowestChainedDescendant(new CTNodeMatcherC("PP"));
		assertEquals(children[3].getFirstChild(), node);
	}
	
	private void testGetSubtree(CTNode curr)
	{
		List<CTNode> nodes;
		CTNode node;
		
		node = curr.getFirstTerminal();
		assertEquals(curr.getFirstChild().getFirstChild(), node);
		
		node = curr.getLastTerminal();
		assertEquals(curr.getLastChild().getLastChild(), node);
		
		nodes = curr.getTerminalList();
		assertEquals("[(-NONE- *), (CC and), (NNP Jinho), (-NONE- *ICH*), (PP null)]", nodes.toString());
	}
	
	private void testGetEmptyCategories(CTNode curr)
	{
		List<CTNode> nodes = curr.getEmptyCategoryListInSubtree(Pattern.compile("\\*ICH\\*"));
		assertEquals("[(-NONE- *ICH*)]", nodes.toString());
	}
	
	@Test
	public void testSetters()
	{
		CTNode curr = new CTNode("NP");
		CTNode A  = new CTNode("A");
		CTNode B  = new CTNode("B");
		CTNode C  = new CTNode("C");
		CTNode D  = new CTNode("D");
		CTNode E  = new CTNode("E");
		CTNode CC = new CTNode("CC");
		CTNode DD = new CTNode("DD");
		CTNode EE = new CTNode("EE");
		
		// add child
		curr.addChild(A);
		curr.addChild(B);
		
		assertEquals(null, A.getLeftSibling());
		assertEquals(B   , A.getRightSibling());
		assertEquals(A   , B.getLeftSibling());
		assertEquals(null, B.getRightSibling());
		assertEquals("(NP (A null) (B null))", curr.toStringLine());
		
		// add child with index
		curr.addChild(0, C);
		curr.addChild(3, D);
		curr.addChild(2, E);
		
		assertEquals(null, C.getLeftSibling());
		assertEquals(A   , C.getRightSibling());
		assertEquals(C   , A.getLeftSibling());
		assertEquals(E   , A.getRightSibling());
		assertEquals(A   , E.getLeftSibling());
		assertEquals(B   , E.getRightSibling());
		assertEquals(E   , B.getLeftSibling());
		assertEquals(D   , B.getRightSibling());
		assertEquals(B   , D.getLeftSibling());
		assertEquals(null, D.getRightSibling());
		assertEquals("(NP (C null) (A null) (E null) (B null) (D null))", curr.toStringLine());
		
		assertTrue(A.isLeftSiblingOf(B));
		assertTrue(A.isLeftSiblingOf(D));
		assertFalse(A.isLeftSiblingOf(C));
		
		assertTrue(B.isRightSiblingOf(A));
		assertTrue(D.isRightSiblingOf(A));
		assertFalse(C.isRightSiblingOf(A));

		// set child
		curr.setChild(0, CC);
		curr.setChild(2, EE);
		curr.setChild(4, DD);
		
		assertEquals(null, CC.getLeftSibling());
		assertEquals(A   , CC.getRightSibling());
		assertEquals(CC  , A.getLeftSibling());
		assertEquals(EE  , A.getRightSibling());
		assertEquals(A   , EE.getLeftSibling());
		assertEquals(B   , EE.getRightSibling());
		assertEquals(EE  , B.getLeftSibling());
		assertEquals(DD  , B.getRightSibling());
		assertEquals(B   , DD.getLeftSibling());
		assertEquals(null, DD.getRightSibling());
		assertEquals("(NP (CC null) (A null) (EE null) (B null) (DD null))", curr.toStringLine());
		
		// remove child
		curr.removeChild(0);
		
		assertEquals(null, A.getLeftSibling());
		assertEquals("(NP (A null) (EE null) (B null) (DD null))", curr.toStringLine());
		
		curr.removeChild(2);
		assertEquals(EE, DD.getLeftSibling());
		assertEquals(DD, EE.getRightSibling());
		assertEquals("(NP (A null) (EE null) (DD null))", curr.toStringLine());
		
		curr.removeChild(2);
		assertEquals(null, EE.getRightSibling());
		assertEquals("(NP (A null) (EE null))", curr.toStringLine());
	}
	
	@Test
	public void testBooleans()
	{
		CTNode node = new CTNode("NP1");
		CTNode none = new CTNode(CTTagEn.NONE);
		CTNode np2  = new CTNode("NP2");
		
		node.addChild(none);
		node.addChild(np2);
		
		assertTrue(none.isTerminal());
		assertTrue(none.isEmptyCategory());
		assertFalse(np2.isEmptyCategory());
		assertFalse(np2.isEmptyCategoryTerminal());
		
		np2.addChild(new CTNode(CTTagEn.NONE));
		assertTrue(np2.isEmptyCategoryTerminal());
	}
}