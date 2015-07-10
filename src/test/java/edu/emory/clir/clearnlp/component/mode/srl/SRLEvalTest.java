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
package edu.emory.clir.clearnlp.component.mode.srl;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @since 3.2.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLEvalTest
{
	@Test
	public void test()
	{
		String inputFile = "src/test/resources/dependency/dependency.cnlp";
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(IOUtils.createFileInputStream(inputFile));
		DEPTree tree = reader.next();
		System.out.println(tree.toString()+"\n");
		
		for (Pair<DEPNode,DEPNode> p : tree.get(3).getArgumentCandidateList(3, 2))
		{
			System.out.println(p.o1.getWordForm()+" "+p.o2.getWordForm());
		}
		
//		DEPNode n1 = tree.get(4);
//		DEPNode n2 = tree.get(7);
//		System.out.println(n1.getPath(n2, FieldType.f));
//		System.out.println(n1.getPath(n2, FieldType.p));
//		System.out.println(n1.getPath(n2, FieldType.d));
//		System.out.println(n1.getPath(n2, FieldType.t));
//		
//		System.out.println(n2.getPath(n1, FieldType.f));
//		System.out.println(n2.getPath(n1, FieldType.p));
//		System.out.println(n2.getPath(n1, FieldType.d));
//		System.out.println(n2.getPath(n1, FieldType.t));
//		
//		n1 = tree.get(3);
//		System.out.println(n1.getPath(n2, FieldType.f));
//		System.out.println(n1.getPath(n2, FieldType.p));
//		System.out.println(n1.getPath(n2, FieldType.d));
//		System.out.println(n1.getPath(n2, FieldType.t));
//		
//		System.out.println(n2.getPath(n1, FieldType.f));
//		System.out.println(n2.getPath(n1, FieldType.p));
//		System.out.println(n2.getPath(n1, FieldType.d));
//		System.out.println(n2.getPath(n1, FieldType.t));
//		
//		SRLArc[][] gold = tree.getSemanticHeads();
//		SRLEval eval = new SRLEval();
//		
//		eval.countCorrect(tree, gold);
//		System.out.println(eval.toString());
//		
//		tree.clearSemanticHeads();
//		eval.clear();
//		eval.countCorrect(tree, gold);
//		System.out.println(eval.toString());
//		
//		tree.setSemanticHeads(gold);
//		eval.clear();
//		eval.countCorrect(tree, gold);
//		System.out.println(eval.toString());
		

	}
}
