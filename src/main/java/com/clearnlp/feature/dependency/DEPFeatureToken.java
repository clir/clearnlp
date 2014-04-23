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

import com.clearnlp.feature.FeatureToken;

// TODO: Auto-generated Javadoc
/**
 * The Class DEPFeatureToken.
 *
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 * @since 3.0.0
 */
public class DEPFeatureToken extends FeatureToken<DEPSourceType, DEPRelationType, DEPFieldType>
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4611193578450290459L;

	/**
	 * Instantiates a new DEP feature token.
	 *
	 * @param source the source
	 * @param relation the relation
	 * @param field the field
	 * @param offset the offset
	 */
	public DEPFeatureToken(DEPSourceType source, DEPRelationType relation, DEPFieldType field, int offset)
	{
		super(source, relation, field, offset);
	}
}