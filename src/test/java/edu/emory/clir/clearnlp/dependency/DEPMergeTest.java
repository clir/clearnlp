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
package edu.emory.clir.clearnlp.dependency;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.component.mode.dep.merge.DEPMerge;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPMergeTest
{
	@Test
	@Ignore
	public void test1()
	{
		DEPTree  t = getDEPTree(7);
		DEPMerge m = new DEPMerge(t);

		m.addEdge(t.get(1), t.get(0), "", 1);
		m.addEdge(t.get(2), t.get(0), "", 1);
		m.addEdge(t.get(4), t.get(1), "", 1);
		m.addEdge(t.get(6), t.get(2), "", 1);
		
		m.addEdge(t.get(3), t.get(1), "", 2);
		m.addEdge(t.get(5), t.get(2), "", 2);
		
		m.addEdge(t.get(4), t.get(0), "", 3);
		m.addEdge(t.get(3), t.get(4), "", 3);
		m.addEdge(t.get(6), t.get(0), "", 3);
		m.addEdge(t.get(5), t.get(6), "", 3);
		
		m.addEdge(t.get(6), t.get(4), "", 4);
		m.addEdge(t.get(1), t.get(2), "", 4);
		
		m.merge();
		System.out.println(t.toString(DEPNode::toStringDEP));
	}
	
	@Test
	@Ignore
	public void test2()
	{
		DEPTree  t = getDEPTree(4);
		DEPMerge m = new DEPMerge(t);

		m.addEdge(t.get(1), t.get(0), "", 4);
		m.addEdge(t.get(2), t.get(0), "", 4);
		m.addEdge(t.get(3), t.get(0), "", 4);
		
		m.addEdge(t.get(3), t.get(1), "", 1);
		m.addEdge(t.get(2), t.get(3), "", 3);
		m.addEdge(t.get(1), t.get(2), "", 2);
		
		m.merge();
		System.out.println(t.toString(DEPNode::toStringDEP));
	}

	@Test
	@Ignore
	public void test3()
	{
		DEPTree  t = getDEPTree(5);
		DEPMerge m = new DEPMerge(t);

		m.addEdge(t.get(1), t.get(0), "", 20);
		m.addEdge(t.get(2), t.get(0), "", 15);
		m.addEdge(t.get(3), t.get(0), "", 4);
		m.addEdge(t.get(4), t.get(0), "", 10);
		
		m.addEdge(t.get(2), t.get(1), "", 28);
		m.addEdge(t.get(3), t.get(1), "", 3);
		m.addEdge(t.get(4), t.get(1), "", 12);
		
		m.addEdge(t.get(1), t.get(2), "", 5);
		m.addEdge(t.get(3), t.get(2), "", 8);
		m.addEdge(t.get(4), t.get(2), "", 6);
		
		m.addEdge(t.get(1), t.get(3), "", 2);
		m.addEdge(t.get(2), t.get(3), "", 4);
		m.addEdge(t.get(4), t.get(3), "", 20);
		
		m.addEdge(t.get(1), t.get(4), "", 5);
		m.addEdge(t.get(2), t.get(4), "", 7);
		m.addEdge(t.get(3), t.get(4), "", 30);
		
		m.merge();
		System.out.println(t.toString(DEPNode::toStringDEP));
	}
	
	DEPTree getDEPTree(int size)
	{
		DEPTree tree = new DEPTree(7);
		
		for (int i=1; i<size; i++)
			tree.add(new DEPNode(i, Integer.toString(i)));
		
		return tree;
	}
}
