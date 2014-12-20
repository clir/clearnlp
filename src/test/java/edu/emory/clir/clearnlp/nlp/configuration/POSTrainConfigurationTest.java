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
package edu.emory.clir.clearnlp.nlp.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jdk.nashorn.internal.ir.annotations.Ignore;

import org.junit.Test;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTrainConfigurationTest
{
	@Test
	@Ignore
	public void test()
	{
		String filename = "src/test/resources/nlp/configuration/configure.xml";
		POSTrainConfiguration config = new POSTrainConfiguration(IOUtils.createFileInputStream(filename));
		
		assertEquals(TLanguage.ENGLISH, config.getLanguage());
		assertEquals(0.4, config.getAmbiguityClassThreshold(), 1e-15);
		assertTrue(config.getProperNounTagset().contains("NNP"));
		assertTrue(config.getProperNounTagset().contains("NNPS"));
	}
}
