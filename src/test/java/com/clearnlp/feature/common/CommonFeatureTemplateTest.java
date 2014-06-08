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
package com.clearnlp.feature.common;

import java.io.FileInputStream;

import com.clearnlp.classification.vector.StringFeatureVector;
import com.clearnlp.component.state.CommonTaggingState;
import com.clearnlp.dependency.DEPReader;
import com.clearnlp.util.IOUtils;
import com.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class CommonFeatureTemplateTest
{
//	@Test
	public void test() throws Exception
	{
		String featureFile = "src/test/resources/feature/common/feature_common.xml";
		String deptreeFile = "src/test/resources/feature/common/dependency.txt";
		
		CommonFeatureExtractor fe = new CommonFeatureExtractor(XmlUtils.getDocumentElement(IOUtils.createFileInputStream(featureFile)));
		DEPReader reader = new DEPReader(0, 1, 2, 3, 4, 5, 6, 7, 8);
		reader.open(new FileInputStream(deptreeFile));
		StringFeatureVector vector;
		CommonTaggingState state;
		
		state = new CommonTaggingState(reader.next());
		
		while (!state.isTerminate())
		{
			vector = fe.createStringFeatureVector(state);
			System.out.println(vector.toString());
			state.shift();
		}
	}
}
