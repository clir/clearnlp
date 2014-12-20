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
package edu.emory.clir.clearnlp.component.mode.pos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.clir.clearnlp.collection.map.IncMap2;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.component.ICollector;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.nlp.configuration.POSTrainConfiguration;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSCollector implements ICollector<POSState>, Serializable
{
	private static final long serialVersionUID = -1309316221225281613L;
	private IncMap2<String,String> m_ambiguity_classes;
	private POSTrainConfiguration p_config;
	
	public POSCollector(POSTrainConfiguration configuration)
	{	
		m_ambiguity_classes = new IncMap2<>();
		p_config = configuration;
	}
	
	public void collect(POSState state)
	{
		int i, size = state.getTreeSize();
		DEPNode node;
		
		for (i=1; i<size; i++)
		{
			node = state.getNode(i);
			m_ambiguity_classes.add(node.getSimplifiedWordForm(), node.getPOSTag());
		}
	}
	
	public Map<String,String> finalizeAmbiguityClasses()
	{
		Map<String,String> map = new HashMap<>();
		List<ObjectDoublePair<String>> ps;
		
		for (String key : m_ambiguity_classes.getKeySet1())
		{
			ps = m_ambiguity_classes.toList(key, p_config.getAmbiguityClassThreshold());
			
			if (!ps.isEmpty())
			{
				DSUtils.sortReverseOrder(ps);
				map.put(key, join(ps, StringConst.UNDERSCORE));
			}
		}
		
		return map;
	}
	
	private String join(List<ObjectDoublePair<String>> ps, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (ObjectDoublePair<String> p : ps)
		{
			build.append(delim);
			build.append(p.o);
		}
		
		return build.substring(delim.length());
	}
}


