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

import org.w3c.dom.Element;

import com.clearnlp.feature.AbstractFeatureTemplate;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DEPFeatureTemplate extends AbstractFeatureTemplate<DEPFeatureToken>
{
	private static final long serialVersionUID = 6871298181664963480L;

	public DEPFeatureTemplate(Element eFeature)
	{
		super(eFeature);
	}

	/** @param relation {@code null} if no relation. */
	@Override
	protected DEPFeatureToken createFeatureToken(String source, String relation, String field, int offset)
	{
		try
		{
			DEPSourceType   s = DEPSourceType.valueOf(source);
			DEPRelationType r = (relation != null) ? DEPRelationType.valueOf(relation) : null;
			DEPFieldType    f = DEPFieldType.valueOf(field);
			return new DEPFeatureToken(s, r, f, offset);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
}