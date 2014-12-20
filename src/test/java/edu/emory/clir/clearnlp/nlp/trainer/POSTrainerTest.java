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
package edu.emory.clir.clearnlp.nlp.trainer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.component.mode.pos.DefaultPOSTagger;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTrainerTest
{
	@Test
	@Ignore
	public void test() throws Exception
	{
		String configurationFile = "src/test/resources/nlp/configuration/configure.xml";
		String featureFile = "src/test/resources/nlp/trainer/feature_pos.xml";
		String trainFile = "src/test/resources/nlp/trainer/pos.cnlp";
		List<String> trainFiles   = Lists.newArrayList(trainFile);
		List<String> developFiles = Lists.newArrayList(trainFile);
		
		InputStream configuration = IOUtils.createFileInputStream(configurationFile);
		InputStream[] features = {IOUtils.createFileInputStream(featureFile)};
		AbstractNLPTrainer trainer = new POSTrainer(configuration, features);
		AbstractStatisticalComponent<?,?,?,?> component;
		
		component = trainer.train(trainFiles, developFiles);
		
		byte[] model = component.toByteArray();
		ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(model))));
		component = new DefaultPOSTagger(in);
		in.close();
		
		TSVReader reader = new TSVReader(1);
		reader.open(IOUtils.createFileInputStream(trainFile));
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
		{
			component.process(tree);
			System.out.println(tree.toString()+"\n");
		}
		
		reader.close();
	}
}
