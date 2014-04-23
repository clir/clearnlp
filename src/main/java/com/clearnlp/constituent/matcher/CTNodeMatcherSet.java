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
package com.clearnlp.constituent.matcher;

import java.util.Set;

import com.clearnlp.constituent.CTNode;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class CTNodeMatcherSet implements CTNodeMatcher
{
	private Set<String> s_tags;
	
	public CTNodeMatcherSet(Set<String> constituentTags)
	{
		s_tags = constituentTags;
	}
	
	public boolean matches(CTNode node)
	{
		return s_tags.contains(node.getConstituentTag());		
	}
}