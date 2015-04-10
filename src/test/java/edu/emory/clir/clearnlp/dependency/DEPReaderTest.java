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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import org.junit.Test;

import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPReaderTest
{
	@Test
	public void testPOS() throws Exception
	{
		TSVReader reader = new TSVReader(1, 3);
		reader.open(new FileInputStream("src/test/resources/dependency/dependency.cnlp"));
		DEPTree tree = reader.next();
		String str = tree.toString(DEPNode::toStringPOS);
		
		reader = new TSVReader(0, 1);
		reader.open(new ByteArrayInputStream(str.getBytes()));
		tree = reader.next();
		
		assertEquals(str, tree.toString(DEPNode::toStringPOS));
	}
	
	@Test
	public void testDEP() throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6);
		reader.open(new FileInputStream("src/test/resources/dependency/dependency.cnlp"));
		DEPTree tree = reader.next();
		String str = tree.toString(DEPNode::toStringDEP);
		
		reader.open(new ByteArrayInputStream(str.getBytes()));
		tree = reader.next();
		
		assertEquals(str, tree.toString(DEPNode::toStringDEP));
	}
	
	@Test
	public void testSRL() throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(new FileInputStream("src/test/resources/dependency/dependency.cnlp"));
		DEPTree tree = reader.next();
		String str = tree.toString(DEPNode::toStringSRL);
		
		reader.open(new ByteArrayInputStream(str.getBytes()));
		tree = reader.next();
		
		assertEquals(str, tree.toString(DEPNode::toStringSRL));
	}
}