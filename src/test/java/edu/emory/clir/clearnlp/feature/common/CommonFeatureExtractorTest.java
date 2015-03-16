/**
 * Copyright 2015, Emory University
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

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.mode.pos.POSState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CommonFeatureExtractorTest
{
	public void test()
	{
		String depFile = "";
		String featureFile = "";
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(IOUtils.createFileInputStream(depFile));
		DEPTree tree = reader.next();
		
		CommonFeatureExtractor<POSState> extractor = new CommonFeatureExtractor<>(IOUtils.createFileInputStream(featureFile));
		POSState state = new POSState(tree, CFlag.TRAIN, null);
		StringFeatureVector vector;
		
		vector = extractor.createStringFeatureVector(state);
		
		int i, size = vector.size();
		
		for (i=0; i<size; i++)
			System.out.println(vector.getType(i)+" "+vector.getValue(i));
		
	}
}
