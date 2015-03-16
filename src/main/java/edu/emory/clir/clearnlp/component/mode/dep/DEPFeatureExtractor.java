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
package edu.emory.clir.clearnlp.component.mode.dep;

import java.io.InputStream;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureExtractor;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureToken;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPFeatureExtractor extends CommonFeatureExtractor<DEPState>
{
	private static final long serialVersionUID = -7336596053366459297L;

	public DEPFeatureExtractor(InputStream in)
	{
		super(in);
	}
	
	@Override
	protected String getFeature(CommonFeatureToken token, DEPState state, DEPNode node)
	{
		switch (token.getField())
		{
		case t: return Integer.toString(state.distanceBetweenStackAndInput());
		default: return super.getFeature(token, state, node);
		}
	}
}
