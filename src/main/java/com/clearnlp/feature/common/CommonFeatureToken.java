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
package com.clearnlp.feature.common;

import java.util.regex.Matcher;

import com.clearnlp.feature.AbstractFeatureToken;
import com.clearnlp.feature.type.RelationType;
import com.clearnlp.feature.type.SourceType;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class CommonFeatureToken extends AbstractFeatureToken<CommonFieldType>
{
	private static final long serialVersionUID = 9088239407612336580L;

	public CommonFeatureToken(SourceType source, RelationType relation, String field, int offset)
	{
		super(source, relation, field, offset);
	}

	@Override
	protected void initField(String field)
	{
		Matcher m;
		
		if ((m = CommonFieldType.P_BOOLEAN.matcher(field)).find())
		{
			setField(CommonFieldType.b);
			setValue(Integer.parseInt(m.group(1)));
		}
		else if ((m = CommonFieldType.P_FEAT.matcher(field)).find())
		{
			setField(CommonFieldType.ft);
			setValue(m.group(1));
		}
		else
		{
			setField(CommonFieldType.valueOf(field));
		}
	}
}
