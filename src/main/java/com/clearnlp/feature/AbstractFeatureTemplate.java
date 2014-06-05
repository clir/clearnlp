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
package com.clearnlp.feature;

import java.io.Serializable;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.clearnlp.constant.CharConst;
import com.clearnlp.util.regex.Splitter;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractFeatureTemplate<FeatureTokenType> implements Serializable
{
	private static final long serialVersionUID = 6926688863000363869L;
	private static final String FIELD = "f";
	
	protected FeatureTokenType[] f_tokens;
	
	public AbstractFeatureTemplate(Element eFeature)
	{
		f_tokens = createFeatureTokens(eFeature);
	}
	
	public FeatureTokenType[] getFeatureTokens()
	{
		return f_tokens;
	}
	
	public FeatureTokenType getFeatureToken(int index)
	{
		return f_tokens[index];
	}
	
	/** @return [f="i:f", f="i:p"] */
	protected List<String> getFields(Element eFeature)
	{
		List<String> attributes = Lists.newArrayList();
		NamedNodeMap nodes = eFeature.getAttributes();
		int i, size = nodes.getLength();
		Node node;
		
		for (i=0; i<size; i++)
		{
		    node = nodes.item(i);
		    
		    if (FIELD.equals(node.getNodeName()))
		    	attributes.add(node.getNodeValue());
		}

		return attributes;
	}
	
	/** @param str "l-1_hd:p" */
	protected FeatureTokenType getFeatureToken(String str)
	{
		String[] t0 = Splitter.splitColons(str);		// "l-1_hd:p" -> {"l-1_hd", "p"}
		String[] t1 = Splitter.splitUnderscore(t0[0]);	// "l-1_hd"   -> {"l-1", "hd"} 
		String s = t1[0];
		
		String source = s.substring(0, 1);
		int    offset = 0;
		
		if (s.length() >= 2)
			offset = (s.charAt(1) == CharConst.PLUS) ? Integer.parseInt(s.substring(2)) : Integer.parseInt(s.substring(1));
		
		String relation = (t1.length > 1) ? t1[1] : null;
		String field = t0[1];

		return createFeatureToken(source, relation, field, offset);
	}
	
	abstract protected FeatureTokenType[] createFeatureTokens(Element eFeature);
	abstract protected FeatureTokenType createFeatureToken(String source, String relation, String field, int offset);
}