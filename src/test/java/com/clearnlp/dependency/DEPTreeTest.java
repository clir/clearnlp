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

import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

import com.clearnlp.reader.TSVReader;
import com.clearnlp.srl.SRLTree;
import com.clearnlp.util.Joiner;
import com.clearnlp.util.arc.SRLArc;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DEPTreeTest
{
	@Test
	public void test() throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(new FileInputStream("src/test/resources/dependency/dependency.cnlp"));
		DEPTree tree = reader.next();
		DEPTree copy = new DEPTree(tree);
		String str = tree.toStringSRL();
		DEPNode pred = tree.get(0);
		SRLTree sTree;
		
		// srl-tree
		String[] arr = {"buy.01 1:A0 2:AM-TMP 5:A1 6:AM-TMP","be.01 5:A1 7:R-A1 9:A2"};
		int i = 0;
		
		while ((pred = tree.getNextSemanticHead(pred.getID())) != null)
		{
			sTree = tree.getSRLTree(pred);
			assertEquals(arr[i++], sTree.toString());
		}
		
		// insert
		tree.remove(2);
		tree.remove(5);
		assertEquals(reader.next().toStringSRL(), tree.toStringSRL());
		
		// semantic heads
		DEPNode node = new DEPNode(0, "tomorrow", "tomorrow", "NN", null, new DEPFeat());
		node.setHead(tree.get(2), "npadvmod");
		node.initSemanticHeads();
		node.addSemanticHead(new SRLArc(tree.get(2), "AM-TMP"));
		tree.insert(5, node);
		assertEquals(reader.next().toStringSRL(), tree.toStringSRL());
		
		// projectivize
		tree.projectivize("nproj");
		assertEquals(reader.next().toStringSRL(), tree.toStringSRL());
		
		// roots
		tree.get(7).setHead(tree.get(0), "root");
		List<DEPNode> roots = tree.getRoots();
		
		assertEquals(tree.get(2), tree.getFirstRoot());
		assertEquals(tree.get(2), roots.get(0));
		assertEquals(tree.get(7), roots.get(1));

		// clone
		assertEquals(str, copy.toStringSRL());
		
		// argument list
		List<List<SRLArc>> args = copy.getArgumentList();
		
		assertEquals("1:A0 2:AM-TMP 5:A1 6:AM-TMP", Joiner.join(args.get(3), " "));
		assertEquals("5:A1 7:R-A1 9:A2", Joiner.join(args.get(8), " "));
	}
}