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
import java.util.regex.Matcher;

import org.w3c.dom.Element;

import com.clearnlp.feature.AbstractFeatureTemplate;
import com.clearnlp.util.pair.ObjectIntPair;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DEPFeatureTemplate extends AbstractFeatureTemplate<DEPFeatureToken<?>>
{
	private static final long serialVersionUID = 6871298181664963480L;

	public DEPFeatureTemplate(Element eFeature)
	{
		super(eFeature);
	}
	
	@Override
	protected DEPFeatureToken<?>[] createFeatureTokens(Element eFeature)
	{
		List<String> fields = getFields(eFeature);
		int i, size = fields.size();
		
		DEPFeatureToken<?>[] tokens = new DEPFeatureToken<?>[size];
		
		for (i=0; i<size; i++)
			tokens[i] = getFeatureToken(fields.get(i));
		
		return tokens;
	}

	/** @param relation {@code null} if no relation. */
	@Override
	protected DEPFeatureToken<?> createFeatureToken(String source, String relation, String field, int offset)
	{
		DEPSourceType   s = DEPSourceType.valueOf(source);
		DEPRelationType r = (relation != null) ? DEPRelationType.valueOf(relation) : null;
		return createFeatureToken(s, r, field, offset); 
	}
	
	private DEPFeatureToken<?> createFeatureToken(DEPSourceType source, DEPRelationType relation, String field, int offset)
	{
		try
		{
			DEPFieldType f = DEPFieldType.valueOf(field);
			return new DEPFeatureToken<Object>(source, relation, f, null, offset);
		}
		catch (IllegalArgumentException e)
		{
			Matcher m;
			
			if ((m = DEPFieldType.P_BOOLEAN.matcher(field)).find())
			{
				int n = Integer.parseInt(m.group(1));
				return new DEPFeatureToken<Integer>(source, relation, DEPFieldType.b, n, offset);
			}
			else if ((m = DEPFieldType.P_FEAT.matcher(field)).find())
			{
				String s = m.group(1);
				return new DEPFeatureToken<String>(source, relation, DEPFieldType.feat, s, offset);
			}
			else if ((m = DEPFieldType.P_SUBCAT.matcher(field)).find())
			{
				DEPFieldType type = DEPFieldType.valueOf(m.group(1));
				int dir = Integer.parseInt(m.group(2));
				return new DEPFeatureToken<ObjectIntPair<DEPFieldType>>(source, relation, DEPFieldType.subcat, new ObjectIntPair<DEPFieldType>(type,dir), offset);
			}
			else if ((m = DEPFieldType.P_PATH.matcher(field)).find())
			{
				DEPFieldType type = DEPFieldType.valueOf(m.group(1));
				int dir = Integer.parseInt(m.group(2));
				return new DEPFeatureToken<ObjectIntPair<DEPFieldType>>(source, relation, DEPFieldType.path, new ObjectIntPair<DEPFieldType>(type,dir), offset);
			}
			else if ((m = DEPFieldType.P_ARGN.matcher(field)).find())
			{
				int n = Integer.parseInt(m.group(1));
				return new DEPFeatureToken<Integer>(source, relation, DEPFieldType.argn, n, offset);
			}
		}
		
		throw new IllegalArgumentException();
	}
}
