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
package com.clearnlp.propbank;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import com.clearnlp.constituent.CTNode;
import com.clearnlp.constituent.CTTree;
import com.clearnlp.util.IOUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class PBLibTest
{
	@Test
	public void testGetTreeList()
	{
		InputStream treebank = IOUtils.createFileInputStream("src/test/resources/propbank/sample.parse");
		InputStream propbank = IOUtils.createFileInputStream("src/test/resources/propbank/sample.prop");
		List<CTTree> trees = PBLib.getTreeList(treebank, propbank);
		CTNode pNode, aNode;
		CTTree tree;
		
		tree = trees.get(0);
		pNode = tree.getPBHeadList().get(2);
		assertEquals("25:0", pNode.getPBLocation().toString());
		assertEquals("show.02", pNode.getPBRolesetID());
		
		aNode = tree.getNode(22, 1);
		assertEquals("25-ARG1", aNode.getPBHeads().get(0).toString());
		
		aNode = tree.getNode(23, 1);
		assertEquals("25-ARG1", aNode.getPBHeads().get(0).toString());
		
		aNode = tree.getNode(24, 1);
		assertEquals("25-ARG1", aNode.getPBHeads().get(0).toString());
		
		aNode = tree.getNode(26, 1);
		assertEquals("25-rel", aNode.getPBHeads().get(0).toString());
		
		aNode = tree.getNode(27, 2);
		assertEquals("25-ARGM-TMP", aNode.getPBHeads().get(0).toString());
		
		tree = trees.get(1);
		pNode = tree.getPBHeadList().get(1);
		assertEquals("21:0", pNode.getPBLocation().toString());
		assertEquals("be.01", pNode.getPBRolesetID());
		
		aNode = tree.getNode(19, 1);
		assertEquals("21-ARG1", aNode.getPBHeads().get(0).toString());
		
		aNode = tree.getNode(22, 2);
		assertEquals("21-ARG2", aNode.getPBHeads().get(0).toString());
		
		aNode = tree.getNode(0, 2);
		assertEquals("18-ARG1", aNode.getPBHeads().get(0).toString());
		
		aNode = tree.getNode(0, 2);
		assertEquals("21-ARG1", aNode.getPBHeads().get(1).toString());
	}
	
	@Test
	public void testGetNumber()
	{
		assertEquals("0", PBLib.getNumber("A0"));
		assertEquals("A", PBLib.getNumber("AA"));
		assertEquals("0", PBLib.getNumber("C-A0"));
		assertEquals("0", PBLib.getNumber("R-A0"));
		assertEquals("1", PBLib.getNumber("A1-DSP"));
		
		assertEquals("0", PBLib.getNumber("ARG0"));
		assertEquals("A", PBLib.getNumber("ARGA"));
		assertEquals("1", PBLib.getNumber("ARG1-DSP"));
	}
	
	@Test
	public void testGetLinkType()
	{
		assertEquals("SLC", PBLib.getLinkType("LINK-SLC"));
		assertEquals(null , PBLib.getLinkType("ARGM-SLC"));
	}
	
	@Test
	public void testGetModifierType()
	{
		assertEquals("TMP", PBLib.getModifierType("ARGM-TMP"));
		assertEquals(null , PBLib.getModifierType("LINK-TMP"));
	}
	
	@Test
	public void testIsNumberedArgument()
	{
		String label;
		
		label = "ARG0";
		assertEquals(true, PBLib.isNumberedArgument(label));
		
		label = "ARGA";
		assertEquals(true, PBLib.isNumberedArgument(label));

		label = "ARG1-DSP";
		assertEquals(true, PBLib.isNumberedArgument(label));
		
		label = "ARG";
		assertEquals(false, PBLib.isNumberedArgument(label));
		
		label = "ARGM-LOC";
		assertEquals(false, PBLib.isNumberedArgument(label));

		label = "A0";
		assertEquals(true, PBLib.isNumberedArgument(label));
		
		label = "C-A0";
		assertEquals(true, PBLib.isNumberedArgument(label));
		
		label = "R-A0";
		assertEquals(true, PBLib.isNumberedArgument(label));
		
		label = "AA";
		assertEquals(true, PBLib.isNumberedArgument(label));

		label = "A1-DSP";
		assertEquals(true, PBLib.isNumberedArgument(label));
		
		label = "AM-LOC";
		assertEquals(false, PBLib.isNumberedArgument(label));
	}
	
	@Test
	public void testIsCoreNumberedArgument()
	{
		String label;
		
		label = "ARG0";
		assertEquals(true, PBLib.isCoreNumberedArgument(label));
		
		label = "ARGA";
		assertEquals(true, PBLib.isCoreNumberedArgument(label));

		label = "ARG1-DSP";
		assertEquals(true, PBLib.isCoreNumberedArgument(label));
		
		label = "ARG";
		assertEquals(false, PBLib.isCoreNumberedArgument(label));
		
		label = "ARGM-LOC";
		assertEquals(false, PBLib.isCoreNumberedArgument(label));

		label = "A0";
		assertEquals(true, PBLib.isCoreNumberedArgument(label));
		
		label = "AA";
		assertEquals(true, PBLib.isCoreNumberedArgument(label));

		label = "A1-DSP";
		assertEquals(true, PBLib.isCoreNumberedArgument(label));
		
		label = "C-A0";
		assertEquals(false, PBLib.isCoreNumberedArgument(label));
		
		label = "R-A0";
		assertEquals(false, PBLib.isCoreNumberedArgument(label));
		
		label = "AM-LOC";
		assertEquals(false, PBLib.isCoreNumberedArgument(label));
	}
	
	@Test
	public void testIsModifier()
	{
		String label;
		
		label = "ARG0";
		assertEquals(false, PBLib.isModifier(label));
		
		label = "ARGA";
		assertEquals(false, PBLib.isModifier(label));

		label = "ARG1-DSP";
		assertEquals(false, PBLib.isModifier(label));
		
		label = "ARGM-LOC";
		assertEquals(true, PBLib.isModifier(label));
	}
}