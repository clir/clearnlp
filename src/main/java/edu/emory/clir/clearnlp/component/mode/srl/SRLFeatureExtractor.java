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

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureTemplate;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureToken;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLFeatureExtractor extends AbstractFeatureExtractor<CommonFeatureTemplate, CommonFeatureToken, SRLState>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9213312216673445978L;

	/**
	 * @param in
	 */
	public SRLFeatureExtractor(InputStream in)
	{
		super(in);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor#createFeatureTemplate(org.w3c.dom.Element)
	 */
	@Override
	protected CommonFeatureTemplate createFeatureTemplate(Element eFeature)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor#getFeature(edu.emory.clir.clearnlp.feature.AbstractFeatureToken, edu.emory.clir.clearnlp.component.state.AbstractState, edu.emory.clir.clearnlp.dependency.DEPNode)
	 */
	@Override
	protected String getFeature(CommonFeatureToken token, SRLState state,
			DEPNode node)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor#getFeatures(edu.emory.clir.clearnlp.feature.AbstractFeatureToken, edu.emory.clir.clearnlp.component.state.AbstractState, edu.emory.clir.clearnlp.dependency.DEPNode)
	 */
	@Override
	protected String[] getFeatures(CommonFeatureToken token, SRLState state,
			DEPNode node)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
