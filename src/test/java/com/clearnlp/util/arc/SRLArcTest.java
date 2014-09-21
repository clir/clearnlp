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
package com.clearnlp.util.arc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.util.arc.SRLArc;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLArcTest
{
	@Test
	public void test()
	{
		DEPNode node = new DEPNode(1, "A");
		SRLArc arc = new SRLArc(node, "A0");
		assertEquals("1:A0", arc.toString());
		
		arc.setNumberedArgumentTag("PRD");
		assertEquals("1:A0", arc.toString());
		assertEquals("1:A0", arc.toString(false));
		assertEquals("1:A0-PRD", arc.toString(true));
	}
}