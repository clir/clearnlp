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

import java.util.regex.Matcher;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;
import edu.emory.clir.clearnlp.feature.type.DirectionType;
import edu.emory.clir.clearnlp.feature.type.FieldType;
import edu.emory.clir.clearnlp.feature.type.RelationType;
import edu.emory.clir.clearnlp.feature.type.SourceType;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CommonFeatureToken extends AbstractFeatureToken
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
		
		if ((m = FieldType.P_BOOLEAN.matcher(field)).find())
		{
			setField(FieldType.b);
			setValue(Integer.parseInt(m.group(1)));
		}
		else if ((m = FieldType.P_CLUSTER.matcher(field)).find())
		{
			setField(FieldType.dsw);
			setValue(Integer.parseInt(m.group(1)));
		}
		else if ((m = FieldType.P_FEAT.matcher(field)).find())
		{
			setField(FieldType.ft);
			setValue(m.group(1));
		}
		else if ((m = FieldType.P_PREFIX.matcher(field)).find())
		{
			setField(FieldType.pf);
			setValue(Integer.parseInt(m.group(1)));
		}
		else if ((m = FieldType.P_SUFFIX.matcher(field)).find())
		{
			setField(FieldType.sf);
			setValue(Integer.parseInt(m.group(1)));
		}
		else if ((m = FieldType.P_SUBCAT.matcher(field)).find())
		{
			setField(FieldType.sc);
			setValue(new Pair<DirectionType,FieldType>(DirectionType.valueOf(m.group(1)), FieldType.valueOf(m.group(2))));
		}
		else if ((m = FieldType.P_VALENCY.matcher(field)).find())
		{
			setField(FieldType.v);
			setValue(DirectionType.valueOf(m.group(1)));
		}
		else if ((m = FieldType.P_DEPENDENTS.matcher(field)).find())
		{
			setField(FieldType.ds);
			setValue(FieldType.valueOf(m.group(1)));
		}
		else if ((m = FieldType.P_GRAND_DEPENDENTS.matcher(field)).find())
		{
			setField(FieldType.ds);
			setValue(FieldType.valueOf(m.group(1)));
		}
		else
		{
			setField(FieldType.valueOf(field));
		}
	}
}
