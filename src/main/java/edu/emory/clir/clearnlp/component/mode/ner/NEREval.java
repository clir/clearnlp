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
package edu.emory.clir.clearnlp.component.mode.ner;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.component.evaluation.AbstractF1Eval;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NEREval extends AbstractF1Eval<String>
{
	@Override
	public void countCorrect(DEPTree sTree, String[] gLabels)
	{
		IntObjectHashMap<String> gMap = NERState.collectNamedEntityMap(gLabels, String::toString);
		IntObjectHashMap<String> sMap = NERState.collectNamedEntityMap(sTree.toNodeArray(), DEPNode::getNamedEntityTag);
		
		n_correct += count(sMap, gMap);
		p_total   += sMap.size();
		r_total   += gMap.size();
	}
	
	private int count(IntObjectHashMap<String> map1, IntObjectHashMap<String> map2)
	{
		int count = 0;
		String s2;
		
		for (ObjectIntPair<String> p1 : map1)
		{
			s2 = map2.get(p1.i);
			if (s2 != null && s2.equals(p1.o)) count++; 
		}
		
		return count;
	}
}
