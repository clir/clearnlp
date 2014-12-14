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
package edu.emory.clir.clearnlp.feature;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.common.OrthographicType;
import edu.emory.clir.clearnlp.feature.type.FeatureXml;
import edu.emory.clir.clearnlp.util.CharUtils;
import edu.emory.clir.clearnlp.util.MetaUtils;
import edu.emory.clir.clearnlp.util.XmlUtils;
import edu.emory.clir.clearnlp.util.constant.CharConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractFeatureExtractor<FeatureTemplateType extends AbstractFeatureTemplate<FeatureTokenType>, FeatureTokenType extends AbstractFeatureToken, StateType extends AbstractState<?,?>> implements Serializable, FeatureXml
{
	private static final long serialVersionUID = 1558293248573950051L;
	public  static final String DELIM = StringConst.UNDERSCORE;
	
	private ArrayList<FeatureTemplateType> f_templates;
	
//	====================================== Initialization ======================================
	
	public AbstractFeatureExtractor(InputStream in)
	{	
		init(XmlUtils.getDocumentElement(in));
	}
	
	public void init(Element eRoot)
	{
		NodeList eList = eRoot.getElementsByTagName(E_FEATURE);
		int i, size = eList.getLength();
		FeatureTemplateType template;
		Element eFeature;

		f_templates = Lists.newArrayList();
		
		for (i=0; i<size; i++)
		{
			eFeature = (Element)eList.item(i);
			
			if (isVisible(eFeature))
			{
				template = createFeatureTemplate(eFeature);
				f_templates.add(template);
			}
		}
		
		f_templates.trimToSize();
	}
	
	/** Called by {@link #init(Element)}. */
	private boolean isVisible(Element eFeature)
	{
		String tmp = XmlUtils.getTrimmedAttribute(eFeature, A_VISIBLE);
		return tmp.isEmpty() || Boolean.parseBoolean(tmp);
	}
	
	/** Called by {@link #init(Element)}. */
	abstract protected FeatureTemplateType createFeatureTemplate(Element eFeature);

//	====================================== Feature extraction ======================================
	
	public StringFeatureVector createStringFeatureVector(StateType state)
	{
		StringFeatureVector vector = new StringFeatureVector();
		addFeatures(vector, state);
		return vector;
	}
	
	/** Called by {@link #createStringFeatureVector(AbstractState)}. */
	private void addFeatures(StringFeatureVector vector, StateType state)
	{
		int i, id = 1, size = f_templates.size();
		FeatureTemplateType template;
		
		for (i=0; i<size; i++)
		{
			template = f_templates.get(i);
			
			switch (template.getFeatureType())
			{
			case BINARY: addSimpleFeatures(vector, template, 0   , state); break;
			case SIMPLE: addSimpleFeatures(vector, template, id++, state); break;
			case SET   : addSetFeatures   (vector, template, id++, state); break;
			}
		}
	}
	
	/** Called by {@link #addFeatures(StringFeatureVector, int, AbstractState)}. */
	private void addSimpleFeatures(StringFeatureVector vector, FeatureTemplateType template, int typeID, StateType state)
	{
		FeatureTokenType[] tokens = template.getFeatureTokens();
		StringBuilder build = new StringBuilder();
		int i, size = tokens.length;
		String ftr;

		for (i=0; i<size; i++)
		{
			ftr = getFeature(tokens[i], state);
			if (ftr == null) return;
			
			if (i > 0) build.append(DELIM);
			build.append(ftr);
		}
		
		vector.addFeature(typeID, build.toString());
	}
	
	/** Called by {@link #addSetFeatures(StringFeatureVector, int, AbstractState)}. */
	private void addSetFeatures(StringFeatureVector vector, FeatureTemplateType template, int typeID, StateType state)
	{
		FeatureTokenType[] tokens = template.getFeatureTokens();
		int i, size = tokens.length;
		
		String[][] fields = new String[size][];
		
		for (i=0; i<size; i++)
		{
			fields[i] = getFeatures(tokens[i], state);
			if (fields[i] == null) return;
		}
		
		if (size == 1)	addSetFeaturesAux1(vector, typeID, fields[0]);
		else			addSetFeaturesAuxM(vector, typeID, fields, 0, StringConst.EMPTY);
    }
	
	/** Called by {@link #addSetFeaturesAux(StringFeatureVector, DEPTreeFeatureTemplate, DEPTree, int)}. */
	private void addSetFeaturesAux1(StringFeatureVector vector, int type, String[] fields)
	{
		for (String field : fields)
			vector.addFeature(type, field);
	}
	
	/** Called by {@link #addSetFeaturesAux(StringFeatureVector, DEPTreeFeatureTemplate, DEPTree, int)}. */
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
	
	private String getFeature(FeatureTokenType token, StateType state)
	{
		DEPNode node = state.getNode(token);
		return (node != null) ? getFeature(token, state, node) : null;
	}
	
	private String[] getFeatures(FeatureTokenType token, StateType state)
	{
		DEPNode node = state.getNode(token);
		if (node == null) return null;
		
		String ftr = getFeature(token, state, node);
		if (ftr != null) return new String[]{ftr};
		
		return getFeatures(token, state, node);
	}
	
	abstract protected String   getFeature (FeatureTokenType token, StateType state, DEPNode node);
	abstract protected String[] getFeatures(FeatureTokenType token, StateType state, DEPNode node);
	
//	====================================== Helper methods ======================================
	
	/** @return {@code null} if the specific list is empty. */
	protected String[] toLabelArray(List<DEPNode> nodes)
	{
		if (nodes.isEmpty()) return null;
		
		int i, size = nodes.size();
		String[] array = new String[size];
		
		for (i=0; i<size; i++)
			array[i] = nodes.get(i).getLabel();
		
		return array;
	}
	
	protected String[] toArray(Collection<String> list)
	{
		return list.isEmpty() ? null : list.toArray(new String[list.size()]);
	}
	
	protected String[] getOrthographicFeatures(StateType state, DEPNode node)
	{
		List<String> list = Lists.newArrayList();
		
		if (node.isSimplifiedForm(MetaUtils.META_HYPERLINK))
			list.add(OrthographicType.HYPERLINK);
		else
		{
			char[] cs = node.getWordForm().toCharArray();
			getOrthographicFeautureAux(state, node, list, cs);
		}
		
		return toArray(list);
	}
	
	/** Called by {@link #getOrthographicFeatures(AbstractState, DEPNode)}. */
	private void getOrthographicFeautureAux(StateType state, DEPNode node, List<String> list, char[] cs)
	{
		boolean hasDigit  = false;
		boolean hasPeriod = false;
		boolean hasHyphen = false;
		boolean hasPunct  = false;
		boolean fstUpper  = false;
		boolean allDigit  = true;
		boolean allPunct  = true;
		boolean allUpper  = true;
		boolean allLower  = true;
		boolean noLower   = true;
		boolean allDigitOrPunct = true;
		int countUpper = 0;
		
		boolean upper, lower, punct, digit;
		int i, size = cs.length;
		char c;
		
		for (i=0; i<size; i++)
		{
			c = cs[i];
			
			upper = CharUtils.isUpperCase(c);
			lower = CharUtils.isLowerCase(c);
			digit = CharUtils.isDigit(c);
			punct = CharUtils.isPunctuation(c);
			
			if (upper)
			{
				if (i == 0)	fstUpper = true;
				else		countUpper++;
			}
			else
				allUpper = false;
			
			if (lower)	noLower  = false;	
			else		allLower = false;
			
			if (digit)	hasDigit = true;
			else		allDigit = false;

			if (punct)
			{
				hasPunct = true;
				if (c == CharConst.PERIOD) hasPeriod = true;
				if (c == CharConst.HYPHEN) hasHyphen = true;
			}
			else
				allPunct = false;
			
			if (!digit && !punct)
				allDigitOrPunct = false;
		}
		
		if (allUpper)
			list.add(OrthographicType.ALL_UPPER);
		else if (allLower)
			list.add(OrthographicType.ALL_LOWER);
		else if (allDigit)
			list.add(OrthographicType.ALL_DIGIT);
		else if (allPunct)
			list.add(OrthographicType.ALL_PUNCT);
		else if (allDigitOrPunct)
			list.add(OrthographicType.ALL_DIGIT_OR_PUNCT);
		else if (noLower)
			list.add(OrthographicType.NO_LOWER);
		
		if (!allUpper)
		{
			if (fstUpper && !state.isFirstNode(node))
				list.add(OrthographicType.FST_UPPER);
			if (countUpper == 1)
				list.add(OrthographicType.UPPER_1);
			else if (countUpper > 1)
				list.add(OrthographicType.UPPER_2);
		}
		
		if (!allDigit && hasDigit)
			list.add(OrthographicType.HAS_DIGIT);
		
		if (hasPeriod)	list.add(OrthographicType.HAS_PERIOD);
		if (hasHyphen)	list.add(OrthographicType.HAS_HYPHEN);
		
		if (!allPunct && !hasPeriod && !hasHyphen && hasPunct)
			list.add(OrthographicType.HAS_OTHER_PUNCT);
	}
}