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

import java.io.InputStream;

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.component.utils.AbstractState;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor;
import edu.emory.clir.clearnlp.util.StringUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CommonFeatureExtractor<StateType extends AbstractState<?,?>> extends AbstractFeatureExtractor<CommonFeatureTemplate,CommonFeatureToken,StateType>
{
	private static final long serialVersionUID = -3522042349865325347L;

	public CommonFeatureExtractor(InputStream in)
	{
		super(in);
	}
	
	@Override
	protected CommonFeatureTemplate createFeatureTemplate(Element eFeature)
	{
		return new CommonFeatureTemplate(eFeature);
	}

	@Override
	protected String getFeature(CommonFeatureToken token, StateType state, DEPNode node)
	{
		boolean includeForm = state.extractWordFormFeature(node);
		
		switch (token.getField())
		{
		case f : return includeForm ? node.getWordForm() : null;
		case f2: return includeForm ? node.getSimplifiedWordForm() : null;
		case f3: return includeForm ? node.getLowerSimplifiedWordForm() : null;
		case m : return includeForm ? node.getLemma() : null;
		case f4: return node.getWordShape(2);
		case pf: return StringUtils.getPrefix(node.getSimplifiedWordForm(), (int)token.getValue());
		case sf: return StringUtils.getSuffix(node.getSimplifiedWordForm(), (int)token.getValue());
		
		case p : return node.getPOSTag();
		case n : return node.getNamedEntityTag();
		case d : return node.getLabel();
		case v : return node.getValency();
		case lv: return Integer.toString(node.getLeftValency());
		case rv: return Integer.toString(node.getRightValency());
		case b : return getBooleanFeatureValue(token, state, node);
		case ft: return node.getFeat((String)token.getValue());
		default: return null;
		}
	}
	
	@Override
	protected String[] getFeatures(CommonFeatureToken token, StateType state, DEPNode node)
	{
		switch (token.getField())
		{
		case ds  : return toLabelArray(node.getDependentList());
		case ds2 : return toLabelArray(node.getGrandDependentList());
		case orth: return getOrthographicFeatures(state, node);
		default  : return null;
		}
	}
	
	protected String getBooleanFeatureValue(CommonFeatureToken token, StateType state, DEPNode node)
	{
		int field = (int)token.getValue();
		boolean b = false;
		
		switch (field)
		{
		case  0: b = state.isFirstNode(node); break;
		case  1: b = state.isLastNode(node);  break;
		default: throw new IllegalArgumentException("Unsupported feature: b"+token.getValue());
		}
		
		return b ? token.getBinaryFeatureKey() : null;
	}
}
