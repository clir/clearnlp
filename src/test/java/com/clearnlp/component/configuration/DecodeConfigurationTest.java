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
package com.clearnlp.component.configuration;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Test;

import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.configuration.DecodeConfiguration;
import edu.emory.clir.clearnlp.reader.TReader;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;


/** @author Jinho D. Choi ({@code jinho.choi@emory.edu}) */
public class DecodeConfigurationTest
{
	@Test
	public void test() throws IOException
	{
		String filename = "src/test/resources/nlp/configure/configure.xml";
		DecodeConfiguration config = new DecodeConfiguration(IOUtils.createFileInputStream(filename));
		
		assertEquals(TLanguage.ENGLISH, config.getLanguage());
		assertEquals("com/clearnlp/model/english/general", config.getModelPath().toString());
		
		filename = "src/test/resources/dependency/dependency.cnlp";
		TSVReader reader = (TSVReader)config.getReader();
		reader.open(IOUtils.createFileInputStream(filename));
		assertEquals(TReader.TSV, reader.getReaderType());
		
		StringBuilder b1 = new StringBuilder();
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
		{
			b1.append(tree.toStringSRL());
			b1.append("\n\n");
		}
		
		BufferedReader in = IOUtils.createBufferedReader(filename);
		StringBuilder b2 = new StringBuilder();
		String line;
		
		while ((line = in.readLine()) != null)
		{
			b2.append(line);
			b2.append("\n");
		}
		
		assertEquals(b2.toString().trim(), b1.toString().trim());
	}
}