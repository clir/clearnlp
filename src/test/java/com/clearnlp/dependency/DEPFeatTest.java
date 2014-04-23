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

import org.junit.Test;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DEPFeatTest
{
	@Test
	public void testDEPFeat()
	{
		DEPFeat feat = new DEPFeat();
		assertEquals(DEPReader.BLANK, feat.toString());
		
		feat = new DEPFeat(DEPReader.BLANK);
		assertEquals(DEPReader.BLANK, feat.toString());
		
		feat.add("lst=choi|fst=jinho");
		assertEquals("fst=jinho|lst=choi", feat.toString());
		
		assertEquals("choi" , feat.get("lst"));
		assertEquals("jinho", feat.get("fst"));
		assertEquals(null   , feat.get("mid"));
		
		feat.add(DEPReader.BLANK);
		assertEquals("fst=jinho|lst=choi", feat.toString());
	}
}