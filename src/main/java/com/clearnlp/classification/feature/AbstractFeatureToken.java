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

import com.clearnlp.classification.feature.type.RelationType;
import com.clearnlp.classification.feature.type.SourceType;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractFeatureToken<FieldType> implements Serializable
{
	static private final long serialVersionUID  = 4470851888237339877L;
	
	private SourceType		t_source;
	private RelationType	t_relation;
	private FieldType		t_field;
	private Object			t_value;
	private int				i_offset;
	
	public AbstractFeatureToken(SourceType source, RelationType relation, String field, int offset)
	{
		setSource(source);
		setRelation(relation);
		initField(field);
		setOffset(offset);
	}
	
	abstract protected void initField(String field);
	
	public SourceType getSource()
	{
		return t_source;
	}
	
	public RelationType getRelation()
	{
		return t_relation;
	}
	
	public FieldType getField()
	{
		return t_field;
	}
	
	public Object getValue()
	{
		return t_value;
	}
	
	public int getOffset()
	{
		return i_offset;
	}
	
	public void setSource(SourceType source)
	{
		t_source = source;
	}
	
	public void setRelation(RelationType relation)
	{
		t_relation = relation;
	}
	
	public void setField(FieldType field)
	{
		t_field = field;
	}
	
	public void setValue(Object value)
	{
		t_value = value;
	}
	
	public void setOffset(int offset)
	{
		i_offset = offset;
	}
	
	public boolean hasRelation()
	{
		return t_relation != null;
	}
}