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
import org.w3c.dom.NodeList;

import com.clearnlp.util.XmlUtils;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractFeatureExtractor<FeatreTemplateType> implements Serializable, FeatureXml
{
	private static final long serialVersionUID = 1558293248573950051L;
	
	protected List<FeatreTemplateType> b_templates;
	protected List<FeatreTemplateType> g_templates;
	protected List<FeatreTemplateType> s_templates;

	public AbstractFeatureExtractor(Element eRoot)
	{
		try
		{
			init(eRoot);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public void init(Element eRoot) throws Exception
	{
		NodeList eList = eRoot.getElementsByTagName(E_FEATURE);
		int i, size = eList.getLength();
		FeatreTemplateType template;
		Element eFeature;
		String type;

		b_templates = Lists.newArrayList();
		g_templates = Lists.newArrayList();
		s_templates = Lists.newArrayList();
		
		for (i=0; i<size; i++)
		{
			eFeature = (Element)eList.item(i);
			
			if (isVisible(eFeature))
			{
				template = createFeatureTemplate(eFeature);
				type = getType(eFeature);
				
				switch (type)
				{
				case V_BOOL: b_templates.add(template); break;
				case V_SET : s_templates.add(template); break;
				default    : g_templates.add(template); break;
				}
			}
		}
	}
	
	private boolean isVisible(Element eFeature)
	{
		String tmp = XmlUtils.getTrimmedAttribute(eFeature, A_VISIBLE);
		return tmp.isEmpty() || Boolean.parseBoolean(tmp);
	}
	
	private String getType(Element eFeature)
	{
		return XmlUtils.getTrimmedAttribute(eFeature, A_TYPE);
	}
	
	abstract protected FeatreTemplateType createFeatureTemplate(Element eFeature);
}