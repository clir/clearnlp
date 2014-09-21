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
package edu.emory.clir.clearnlp.feature.common;

import java.io.FileInputStream;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.mode.pos.POSState;
import edu.emory.clir.clearnlp.component.state.SeqState;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureExtractor;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CommonFeatureTemplateTest
{
//	@Test
	public void test() throws Exception
	{
		String featureFile = "src/test/resources/feature/common/feature_common.xml";
		String deptreeFile = "src/test/resources/feature/common/dependency.txt";
		
		CommonFeatureExtractor fe = new CommonFeatureExtractor(IOUtils.createFileInputStream(featureFile));
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7, 8);
		reader.open(new FileInputStream(deptreeFile));
		StringFeatureVector vector;
		SeqState state;
		
		state = new POSState(reader.next(), true);
		
		while (!state.isTerminate())
		{
			vector = fe.createStringFeatureVector(state);
			System.out.println(vector.toString());
			state.shift();
		}
	}
}
