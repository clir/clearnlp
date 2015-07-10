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
package edu.emory.clir.clearnlp.component.mode.srl;

import java.io.InputStream;

import edu.emory.clir.clearnlp.component.mode.srl.state.AbstractSRLState;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureExtractor;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureToken;
import edu.emory.clir.clearnlp.feature.type.FieldType;

/**
 * @since 3.2.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLFeatureExtractor extends CommonFeatureExtractor<AbstractSRLState>
{
	private static final long serialVersionUID = 7959999194169624654L;

	public SRLFeatureExtractor(InputStream in)
	{
		super(in);
	}
	
	@Override
	protected String getFeature(CommonFeatureToken token, AbstractSRLState state, DEPNode node)
	{
		switch (token.getField())
		{
		case b   : return getBooleanFeatureValue(token, state, node);
		case t   : return state.distanceBetweenPredicateAndArgument();
		case path: return state.getPath((FieldType)token.getValue());
		case argn: return state.getNumberedArgument((Integer)token.getValue());
		default  : return super.getFeature(token, state, node);
		}
	}
	
	protected String getBooleanFeatureValue(CommonFeatureToken token, AbstractSRLState state, DEPNode node)
	{
		int field = (int)token.getValue();
		boolean b = false;
		
		switch (field)
		{
		case  0: b = node.isDependentOf(state.getPredicate());  break;
		case  1: b = state.getPredicate().isDependentOf(node);  break;
		case  2: b = node.isDescendantOf(state.getPredicate()); break;
		case  3: b = state.getPredicate().isDescendantOf(node); break;
		default: throw new IllegalArgumentException("Unsupported feature: b"+token.getValue());
		}
		
		return b ? token.getBinaryFeatureKey() : null;
	}
	
}
