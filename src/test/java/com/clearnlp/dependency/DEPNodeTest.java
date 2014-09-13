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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.clearnlp.reader.TSVReader;
import com.clearnlp.util.PatternUtils;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPNodeTest
{
	@Test
	public void testRoot()
	{
		DEPNode node = new DEPNode();
		node.initRoot();
		
		assertEquals(DEPLib.ROOT_ID , node.getID());
		assertEquals(DEPLib.ROOT_TAG, node.getWordForm());
		assertEquals(DEPLib.ROOT_TAG, node.getLemma());
		assertEquals(DEPLib.ROOT_TAG, node.getPOSTag());
		assertEquals(DEPLib.ROOT_TAG, node.getNamedEntityTag());
		assertEquals(null, node.getLabel());
		assertEquals(null, node.getHead());
	}
	
	@Test
	public void testBasicFields()
	{
		DEPNode node = new DEPNode(1, "Jinho", "jinho", "NNP", "PERSON", new DEPFeat("fst=jinho|lst=choi"));
		
		assertEquals(1       , node.getID());
		assertEquals("Jinho" , node.getWordForm());
		assertEquals("jinho" , node.getLemma());
		assertEquals("NNP"   , node.getPOSTag());
		assertEquals("PERSON", node.getNamedEntityTag());
		assertEquals("jinho" , node.getFeat("fst"));
		assertEquals("choi"  , node.getFeat("lst"));
		
		node = new DEPNode(1, "Jinho");
		
		assertEquals(1      , node.getID());
		assertEquals("Jinho", node.getWordForm());
		
		node = new DEPNode(1, "Jinho", "jinho", "NNP", new DEPFeat("fst=jinho|lst=choi"));
		
		assertEquals(1       , node.getID());
		assertEquals("Jinho" , node.getWordForm());
		assertEquals("jinho" , node.getLemma());
		assertEquals("NNP"   , node.getPOSTag());
		
		node.removeFeat("fst");
		assertEquals(null  , node.getFeat("fst"));
		assertEquals("choi", node.getFeat("lst"));
		
		node.putFeat("fst", "Jinho");
		assertEquals("Jinho", node.getFeat("fst"));
	}
	
	@Test
	public void testDependency() throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(new FileInputStream("src/test/resources/dependency/dependency.cnlp"));
		DEPTree tree = reader.next();
		DEPNode node;
		
		node = tree.get(1);
		assertEquals("nsubj", node.getLabel());
		assertEquals(tree.get(3), node.getHead());
		assertEquals(tree.get(0), node.getGrandHead());
		
		node = tree.get(3);
		assertEquals(tree.get(0), node.getHead());
		assertEquals(null, node.getGrandHead());
		
		assertEquals(tree.get(1), node.getLeftMostDependent());
		assertEquals(tree.get(1), node.getLeftMostDependent(0));
		assertEquals(tree.get(2), node.getLeftMostDependent(1));
		assertEquals(null       , node.getLeftMostDependent(2));
		
		assertEquals(tree.get(10), node.getRightMostDependent());
		assertEquals(tree.get(10), node.getRightMostDependent(0));
		assertEquals(tree.get(6) , node.getRightMostDependent(1));
		assertEquals(tree.get(5) , node.getRightMostDependent(2));
		assertEquals(null        , node.getRightMostDependent(3));
		
		assertEquals(tree.get(2), node.getLeftNearestDependent());
		assertEquals(tree.get(2), node.getLeftNearestDependent(0));
		assertEquals(tree.get(1), node.getLeftNearestDependent(1));
		assertEquals(null       , node.getLeftNearestDependent(2));
		
		assertEquals(tree.get(5) , node.getRightNearestDependent());
		assertEquals(tree.get(5) , node.getRightNearestDependent(0));
		assertEquals(tree.get(6) , node.getRightNearestDependent(1));
		assertEquals(tree.get(10), node.getRightNearestDependent(2));
		assertEquals(null        , node.getRightNearestDependent(3));
		
		Pattern p = PatternUtils.createClosedORPattern("dobj", "nsubj");
		
		assertEquals(tree.get(5), node.getFirstDependentByLabel("dobj"));
		assertEquals(tree.get(1), node.getFirstDependentByLabel(p));
		assertEquals(null       , node.getFirstDependentByLabel("csubj"));
		
		List<DEPNode> list = node.getDependentListByLabel(Sets.newHashSet("nsubj", "dobj"));
		assertEquals(tree.get(1), list.get(0));
		assertEquals(tree.get(5), list.get(1));
		
		list = node.getDependentListByLabel(p);
		assertEquals(tree.get(1), list.get(0));
		assertEquals(tree.get(5), list.get(1));
		
		list = node.getLeftDependentList();
		assertEquals(tree.get(1), list.get(0));
		assertEquals(tree.get(2), list.get(1));
		
		list = node.getLeftDependentListByLabel(p);
		assertEquals(tree.get(1), list.get(0));
		
		list = node.getRightDependentList();
		assertEquals(tree.get(5) , list.get(0));
		assertEquals(tree.get(10), list.get(2));
		
		list = node.getRightDependentListByLabel(p);
		assertEquals(tree.get(5), list.get(0));
		
		list = node.getGrandDependentList();
		assertEquals(tree.get(4), list.get(0));
		assertEquals(tree.get(8), list.get(1));
		
		list = node.getDescendantList(0);
		assertTrue(list.isEmpty());
		
		list = node.getDescendantList(1);
		assertEquals(5, list.size());
		
		list = node.getDescendantList(2);
		assertEquals(tree.get(4), list.get(5));
		assertEquals(tree.get(8), list.get(6));
		assertEquals(tree.get(7), node.getAnyDescendantByPOSTag("WDT"));

		assertEquals(2, node.getLeftValency());
		assertEquals(3, node.getRightValency());
		
		node = tree.get(8);
		list = node.getSubNodeList();
		assertEquals(tree.get(7), list.get(0));
		assertEquals(tree.get(8), list.get(1));
		assertEquals(tree.get(9), list.get(2));
		assertEquals("[8, 7, 9]", node.getSubNodeIDSet().toString());
		
		node = tree.get(3);
		assertTrue(node.hasHead());
		assertFalse(tree.get(0).hasHead());
		
		node = tree.get(1);
		assertTrue(node.isDependentOf(tree.get(3)));
		assertTrue(node.isDependentOf(tree.get(3), "nsubj"));
		assertFalse(node.isDependentOf(tree.get(3), "csubj"));
		assertFalse(node.isDependentOf(tree.get(0)));
		assertTrue(node.isDescendantOf(tree.get(0)));
		assertTrue(node.isSiblingOf(tree.get(5)));
		assertFalse(node.isSiblingOf(tree.get(4)));
		
		node = tree.get(3);
		assertEquals("buy.01", node.getRolesetID());
		
		node.setRolesetID("buy.02");
		assertEquals("buy.02", node.getRolesetID());
		assertTrue(node.isSemanticHead());
		
		node.clearRolesetID();
		assertEquals(null, node.getRolesetID());
		assertFalse(node.isSemanticHead());
		
		node = tree.get(5);
		assertEquals(tree.get(1) , node.getLeftNearestSibling(1));
		assertEquals(tree.get(2) ,node.getLeftNearestSibling());
		assertEquals(tree.get(6) ,node.getRightNearestSibling());
		assertEquals(tree.get(10),node.getRightNearestSibling(1));
	}
	
	@Test
	public void testSetters()
	{
		DEPNode node1 = new DEPNode(1, "He");
		DEPNode node2 = new DEPNode(2, "bought");
		DEPNode node3 = new DEPNode(3, "a");
		DEPNode node4 = new DEPNode(4, "car");
		
		node2.addDependent(node4, "dobj");
		node2.addDependent(node1, "nsubj");
		node4.addDependent(node3, "det");
		
		List<DEPNode> list = node2.getDependentList();
		assertEquals(node1, list.get(0));
		assertEquals(node4, list.get(1));
	}
}


//1	He	he	PRP	_	3	nsubj	3:A0
//2	already	already	RB	_	3	advmod	3:AM-TMP
//3	bought	buy	VBD	p2=VBN|pb=buy.01	0	root	_
//4	a	a	DT	_	5	det	_
//5	car	car	NN	_	3	dobj	3:A1;8:A1
//6	yesterday	yesterday	NN	_	3	npadvmod	3:AM-TMP
//7	that	that	WDT	_	8	nsubj	8:R-A1
//8	is	be	VBZ	pb=be.01	5	rcmod	_
//9	red	red	JJ	p2=VBN	8	acomp	8:A2
//10	.	.	.	_	3	punct	_