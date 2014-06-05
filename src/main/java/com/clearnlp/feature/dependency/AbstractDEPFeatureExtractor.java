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
	
	public StringFeatureVector createStringFeatureVector(DEPTree tree)
	{
		StringFeatureVector vector = new StringFeatureVector();
		int beginID;
		
		beginID = 0;
		addBooleanFeatures(vector, tree, beginID);
		
		if (!b_templates.isEmpty()) beginID++;
		addGeneralFeatures(vector, tree, beginID);
		
		beginID += g_templates.size();
		addSetFeatures(vector, tree, beginID);
		
		return vector;
	}

	@Override
	protected DEPFeatureTemplate createFeatureTemplate(Element eFeature)
	{
		return new DEPFeatureTemplate(eFeature);
	}

//	====================================== getNode ======================================
	
	abstract protected DEPNode getNode(DEPFeatureToken<?> token, DEPTree tree);
	
	/**
	 * @param leftBound  the leftmost  ID (inclusive).
	 * @param rightBound the rightmost ID (exclusive).
	 */
	protected DEPNode getNode(DEPFeatureToken<?> token, DEPTree tree, int nodeID, int leftBound, int rightBound)
	{
		if (token.getOffset() == 0)
			return getNode(token, tree, nodeID);

		nodeID += token.getOffset();
		
		if (leftBound <= nodeID && nodeID < rightBound)
			return getNode(token, tree, nodeID);
		
		return null;
	}
	
	/** Called by {@link #getNode(DEPFeatureToken, DEPTree, int, int, int)}. */
	private DEPNode getNode(DEPFeatureToken<?> token, DEPTree tree, int nodeID)
	{
		DEPNode node = tree.get(nodeID);
		
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
	
//	====================================== Boolean features ======================================
	
	/** Called by {@link #createStringFeatureVector(DEPTree)}. */
	private void addBooleanFeatures(StringFeatureVector vector, DEPTree tree, int beginID)
	{
		int i, size = b_templates.size();
		
		for (i=0; i<size; i++)
		{
			if (isBooleanFeature(b_templates.get(i).getFeatureToken(0), tree))
				vector.addFeature(beginID, Integer.toString(i));
		}
	}
	
	/** Called by {@link #addBooleanFeatures(StringFeatureVector, DEPTree, int)}. */
	abstract protected boolean isBooleanFeature(DEPFeatureToken<?> token, DEPTree tree);
	
//	====================================== General features ======================================
	
	/** Called by {@link #createStringFeatureVector(DEPTree)}. */
	private void addGeneralFeatures(StringFeatureVector vector, DEPTree tree, int beginID)
	{
		int i, size = g_templates.size();
		
		for (i=0; i<size; i++)
			vector.addFeature(beginID+i, getGeneralFeature(g_templates.get(i), tree));
	}
	
	/** Called by {@link #addGeneralFeatures(StringFeatureVector, DEPTree)}. */
	private String getGeneralFeature(DEPFeatureTemplate template, DEPTree tree)
	{
		DEPFeatureToken<?>[] tokens = template.getFeatureTokens();
		StringBuilder build = new StringBuilder();
		int i, size = tokens.length;

		for (i=0; i<size; i++)
		{
			if (i > 0) build.append(DELIM);
			build.append(getGeneralFeature(tokens[i], getNode(tokens[i], tree)));
		}
		
		return build.substring(1);
	}
	
	/** Called by {@link #getGeneralFeature(DEPFeatureTemplate, DEPTree)}. */
	abstract protected String getGeneralFeature(DEPFeatureToken<?> token, DEPNode node);
	
	protected String getGeneralFeatureDefault(DEPFeatureToken<?> token, DEPNode node)
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
		default  : return null;
		}
	}
	
//	====================================== Set features ======================================
	
	/** Called by {@link #createStringFeatureVector(DEPTree)}. */
	private void addSetFeatures(StringFeatureVector vector, DEPTree tree, int beginID)
	{
		int i, size = s_templates.size();
		
		for (i=0; i<size; i++)
			addSetFeaturesAux(vector, s_templates.get(i), tree, beginID+i);
	}
	
	/** Called by {@link #addSetFeatures(StringFeatureVector, DEPTree, int)}. */
	private void addSetFeaturesAux(StringFeatureVector vector, DEPFeatureTemplate template, DEPTree tree, int type)
	{
		DEPFeatureToken<?>[] tokens = template.getFeatureTokens();
		int i, size = tokens.length;
		
		String[][] fields = new String[size][];
		
		for (i=0; i<size; i++)
		{
			fields[i] = getSetFeatures(tokens[i], getNode(tokens[i], tree));
			if (fields[i] == null) return;
		}
		
		if (size == 1)	addSetFeaturesAux1(vector, type, fields[0]);
		else			addSetFeaturesAuxM(vector, type, fields, 0, StringConst.EMPTY);
    }
	
	/** Called by {@link #addSetFeaturesAux(StringFeatureVector, DEPFeatureTemplate, DEPTree, int)}. */
	private void addSetFeaturesAux1(StringFeatureVector vector, int type, String[] fields)
	{
		for (String field : fields)
			vector.addFeature(type, field);
	}
	
	/** Called by {@link #addSetFeaturesAux(StringFeatureVector, DEPFeatureTemplate, DEPTree, int)}. */
	private void addSetFeaturesAuxM(StringFeatureVector vector, int type, String[][] fields, int index, String prev)
	{
		if (index < fields.length)
		{
			for (String field : fields[index])
			{
				if (prev.isEmpty())
					addSetFeaturesAuxM(vector, type, fields, index+1, field);
				else
					addSetFeaturesAuxM(vector, type, fields, index+1, prev + DELIM + field);
			}
		}
		else
			vector.addFeature(type, prev);
	}
	
	/** Called by {@link #addSetFeaturesAux(StringFeatureVector, DEPFeatureTemplate, DEPTree, int)}. */
	abstract protected String[] getSetFeatures(DEPFeatureToken<?> token, DEPNode node);
	
	protected String[] getSetFeaturesDefault(DEPFeatureToken<?> token, DEPNode node)
	{
		switch (token.getField())
		{
		case ds : return getDependencyLabels(node.getDependentList());
		case ds2: return getDependencyLabels(node.getGrandDependentList());
		default : return null;
		}
	}
	
	/** Called by {@link #getSetFeatures(DEPFeatureToken, DEPNode)}. */
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