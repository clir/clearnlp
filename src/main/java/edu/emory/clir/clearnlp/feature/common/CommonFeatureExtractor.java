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

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor;
import edu.emory.clir.clearnlp.feature.type.DirectionType;
import edu.emory.clir.clearnlp.feature.type.FieldType;
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
	@SuppressWarnings("unchecked")
	protected String getFeature(CommonFeatureToken token, StateType state, DEPNode node)
	{
		switch (token.getField())
		{
		case f : return node.getWordForm();
		case f2: return node.getSimplifiedWordForm();
		case f3: return node.getLowerSimplifiedWordForm();
		case f4: return node.getWordShape(2);
		case pf: return StringUtils.getPrefix(node.getSimplifiedWordForm(), (int)token.getValue());
		case sf: return StringUtils.getSuffix(node.getSimplifiedWordForm(), (int)token.getValue());
		case m : return node.getLemma();
		case p : return node.getPOSTag();
		case n : return node.getNamedEntityTag();
		case d : return node.getLabel();
		case v : return node.getValency((DirectionType)token.getValue());
		case ft: return node.getFeat((String)token.getValue());
		case sc: Pair<DirectionType,FieldType> p = (Pair<DirectionType,FieldType>)token.getValue(); 
				 return node.getSubcategorization(p.o1, p.o2);
		case b : return getBooleanFeatureValue(token, state, node);
		default: return null;
		}
	}
	
	@Override
	protected String[] getFeatures(CommonFeatureToken token, StateType state, DEPNode node)
	{
		switch (token.getField())
		{
		case ds  : return toLabelArray(node.getDependentList(), (FieldType)token.getValue());
		case ds2 : return toLabelArray(node.getGrandDependentList(), (FieldType)token.getValue());
		case dsw : return GlobalLexica.getDistributionalSemanticFeatures((int)token.getValue(), node.getWordForm());
		case dsls: return GlobalLexica.getDistributionalSemanticFeatures((int)token.getValue(), node.getLowerSimplifiedWordForm());
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
	
	protected String getFormFeature(CommonFeatureToken token, DEPNode node)
	{
		switch (token.getField())
		{
		case f : return node.getWordForm();
		case m : return node.getLemma();
		case f2: return node.getSimplifiedWordForm();
		case f3: return node.getLowerSimplifiedWordForm();
		default: return null;
		}
	}
}
