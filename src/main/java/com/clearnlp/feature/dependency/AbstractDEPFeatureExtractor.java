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
package com.clearnlp.feature.dependency;

import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import com.clearnlp.classification.vector.StringFeatureVector;
import com.clearnlp.constant.StringConst;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.feature.AbstractFeatureExtractor;
import com.google.common.collect.Sets;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractDEPFeatureExtractor extends AbstractFeatureExtractor<DEPFeatureTemplate>
{
	private static final long serialVersionUID = 6827890734032631727L;

	public AbstractDEPFeatureExtractor(Element eRoot)
	{
		super(eRoot);
	}

	@Override
	protected DEPFeatureTemplate createFeatureTemplate(Element eFeature)
	{
		return new DEPFeatureTemplate(eFeature);
	}
	
	public StringFeatureVector createStringFeatureVector(DEPTree tree)
	{
		StringFeatureVector vector = new StringFeatureVector();
		int beginID;
		
		beginID = 0;
		addBooleanFeatures(vector, tree, beginID);
		
		if (b_templates.isEmpty()) beginID++;
		addGeneralFeatures(vector, tree, beginID);
		
		beginID += g_templates.size();
		addSetFeatures(vector, tree, beginID);
		
		return vector;
	}
	
//	====================================== Abstract ======================================
	
	abstract protected DEPNode getNode(DEPFeatureToken<?> token, DEPTree tree);
	abstract protected boolean isBooleanFeature(DEPFeatureToken<?> token, DEPTree tree);
	abstract protected String getGeneralFeature(DEPFeatureToken<?> token, DEPNode node);
	abstract protected String[] getSetFeatures (DEPFeatureToken<?> token, DEPTree tree);

//	====================================== Extract ======================================
	
	/**
	 * @param leftBound the leftmost ID (exclusive).
	 * @param rightBound the rightmost ID (exclusive).
	 */
	protected DEPNode getNode(DEPFeatureToken<?> token, DEPTree tree, int nodeID, int leftBound, int rightBound)
	{
		if (token.getOffset() == 0)
			return tree.get(nodeID);

		nodeID += token.getOffset();
		
		if (leftBound < nodeID && nodeID < rightBound)
			return tree.get(nodeID);
		
		return null;
	}
	
	protected DEPNode getNode(DEPFeatureToken<?> token, DEPNode node)
	{
		if (node == null)
			return null;
		
		if (token.hasRelation())
		{
			switch (token.getRelation())
			{
			case h   : return node.getHead();
			case lmd : return node.getLeftMostDependent();
			case rmd : return node.getRightMostDependent();
			case lnd : return node.getLeftNearestDependent();
			case rnd : return node.getRightNearestDependent();
			case lns : return node.getLeftNearestSibling();
			case rns : return node.getRightNearestSibling();
			
			case h2  : return node.getGrandHead();
			case lmd2: return node.getLeftMostDependent(1);
			case rmd2: return node.getRightMostDependent(1);
			case lnd2: return node.getLeftNearestDependent(1);
			case rnd2: return node.getRightNearestDependent(1);
			case lns2: return node.getLeftNearestSibling(1);
			case rns2: return node.getRightNearestSibling(1);
			}
		}
		
		return node;
	}
	
	/** Called by {@link #createStringFeatureVector(DEPTree)}. */
	private void addBooleanFeatures(StringFeatureVector vector, DEPTree tree, int beginID)
	{
		int i, size = b_templates.size();
		DEPFeatureTemplate template;
		
		for (i=0; i<size; i++)
		{
			template = b_templates.get(i);
			
			if (isBooleanFeature(template.getFeatureToken(0), tree))
				vector.addFeature(beginID, Integer.toString(i));
		}
	}
	
	/** Called by {@link #createStringFeatureVector(DEPTree)}. */
	private void addGeneralFeatures(StringFeatureVector vector, DEPTree tree, int beginID)
	{
		int i, size = g_templates.size();
		DEPFeatureTemplate template;
		
		for (i=0; i<size; i++)
		{
			template = g_templates.get(i);
			vector.addFeature(beginID+i, getGeneralFeature(template, tree));
		}
	}
	
	/** Called by {@link #addGeneralFeatures(StringFeatureVector, DEPTree)}. */
	private String getGeneralFeature(DEPFeatureTemplate template, DEPTree tree)
	{
		StringBuilder build = new StringBuilder();
		
		for (DEPFeatureToken<?> token : template.getFeatureTokens())
		{
			build.append(StringConst.SPACE);
			build.append(getGeneralFeature(token, getNode(token, tree)));
		}
		
		return build.substring(1);
	}
	
	/** Called by {@link #createStringFeatureVector(DEPTree)}. */
	private void addSetFeatures(StringFeatureVector vector, DEPTree tree, int beginID)
	{
		int i, type, size = s_templates.size();
		DEPFeatureTemplate template;
		
		for (i=0; i<size; i++)
		{
			template = s_templates.get(i);
			type = beginID + i;
			
			for (String feature : getSetFeatures(template.getFeatureToken(0), tree))
				vector.addFeature(type, feature);
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	protected String getBasicFeature(DEPFeatureToken<?> token, DEPNode node)
	{
		switch (token.getField())
		{
		case f   : return node.getWordForm();
		case m   : return node.getLemma();
		case p   : return node.getPOSTag();
		case n   : return node.getNamedEntityTag();
		case d   : return node.getLabel();
		case lv  : return Integer.toString(node.getLeftValency());
		case rv  : return Integer.toString(node.getRightValency());
		case feat: return node.getFeat((String)token.getValue());
		}
		
		return null;
	}
	
	@SuppressWarnings("incomplete-switch")
	protected String[] getBasicSetFeature(DEPFeatureToken<?> token, DEPNode node)
	{
		switch (token.getField())
		{
		case ds : return getDependencyLabels(node.getDependentList());
		case ds2: return getDependencyLabels(node.getGrandDependentList());
		}
		
		return null;
	}
	
	private String[] getDependencyLabels(List<DEPNode> dependents)
	{
		Set<String> set = Sets.newHashSet();
		
		for (DEPNode dep : dependents)
			set.add(dep.getLabel());
		
		String[] fields = new String[set.size()];
		set.toArray(fields);
		
		return fields;		
	}
}