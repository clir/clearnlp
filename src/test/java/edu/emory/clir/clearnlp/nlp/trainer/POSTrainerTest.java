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

import java.io.InputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
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
		List<String> trainFiles = Lists.newArrayList("src/test/resources/nlp/trainer/dependency.cnlp");
		
		InputStream configuration = IOUtils.createFileInputStream(configurationFile);
		InputStream[] features = {IOUtils.createFileInputStream(featureFile)};
		AbstractNLPTrainer trainer = new POSTrainer(configuration, features);
		AbstractStatisticalComponent<?,?,?,?> component;
		
		component = trainer.collect(trainFiles);
		Object[] lexicons = component.getLexicons();
		
		component = trainer.train(trainFiles, trainFiles, lexicons);
	}
}
