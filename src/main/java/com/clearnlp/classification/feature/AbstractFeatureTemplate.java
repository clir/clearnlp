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
package com.clearnlp.classification.feature;

import java.io.Serializable;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.clearnlp.classification.feature.type.FeatureType;
import com.clearnlp.classification.feature.type.FeatureXml;
import com.clearnlp.classification.feature.type.RelationType;
import com.clearnlp.classification.feature.type.SourceType;
import com.clearnlp.util.Splitter;
import com.clearnlp.util.constant.CharConst;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractFeatureTemplate<FeatureTokenType extends AbstractFeatureToken<?>> implements Serializable, FeatureXml
{
	private static final long serialVersionUID = 6926688863000363869L;
	private FeatureTokenType[] f_tokens;
	private FeatureType        f_type;
	
	public AbstractFeatureTemplate(Element eFeature)
	{
		f_tokens = createFeatureTokens(eFeature);
		f_type   = initFeatureType();
	}
	
//	====================================== Helper methods ======================================
	
	/** Called by {@link #AbstractFeatureTemplate(Element)}. */
	private FeatureTokenType[] createFeatureTokens(Element eFeature)
	{
		List<String> fields = getFields(eFeature);
		int i, size = fields.size();
		
		FeatureTokenType[] tokens = createFeatureTokens(size);
		
		for (i=0; i<size; i++)
			tokens[i] = getFeatureToken(fields.get(i));
		
		return tokens;
	}
	
	/**
	 * Called by {@link #createFeatureTokens(Element)}.
	 * @return [f="i:f", f="i:p"]
	 */
	private List<String> getFields(Element eFeature)
	{
		List<String> attributes = Lists.newArrayList();
		NamedNodeMap nodes = eFeature.getAttributes();
		int i, size = nodes.getLength();
		Node node;
		
		for (i=0; i<size; i++)
		{
		    node = nodes.item(i);
		    
		    if (A_FIELD.matcher(node.getNodeName()).find())
		    	attributes.add(node.getNodeValue());
		}

		return attributes;
	}
	
	/**
	 * Called by {@link #createFeatureTokens(Element)}.
	 * @param str "l-1_hd:p".
	 */
	protected FeatureTokenType getFeatureToken(String str)
	{
		String[] t0 = Splitter.splitColons(str);		// "l-1_hd:p" -> {"l-1_hd", "p"}
		String[] t1 = Splitter.splitUnderscore(t0[0]);	// "l-1_hd"   -> {"l-1", "hd"} 
		String   s  = t1[0];
		
		SourceType source = SourceType.valueOf(s.substring(0, 1));
		int offset = 0;
		
		if (s.length() >= 2)
			offset = (s.charAt(1) == CharConst.PLUS) ? Integer.parseInt(s.substring(2)) : Integer.parseInt(s.substring(1));
		
		RelationType relation = (t1.length > 1) ? RelationType.valueOf(t1[1]) : null;
		String field = t0[1];
		
		return createFeatureToken(source, relation, field, offset);
	}
	
	abstract protected FeatureType initFeatureType();
	abstract protected FeatureTokenType[] createFeatureTokens(int size);
	abstract protected FeatureTokenType createFeatureToken(SourceType source, RelationType relation, String field, int offset);

//	====================================== Public methods ======================================

	public FeatureTokenType[] getFeatureTokens()
	{
		return f_tokens;
	}
	
	public FeatureTokenType getFeatureToken(int index)
	{
		return f_tokens[index];
	}

	public FeatureType getFeatureType()
	{
		return f_type;
	}
}
