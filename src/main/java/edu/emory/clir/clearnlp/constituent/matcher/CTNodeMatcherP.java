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
package edu.emory.clir.clearnlp.constituent.matcher;

import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.constituent.CTNode;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTNodeMatcherP implements CTNodeMatcher
{
	private Pattern p_constituentPattern;
	
	public CTNodeMatcherP(Pattern constituentPattern)
	{
		p_constituentPattern = constituentPattern;
	}
	
	public boolean matches(CTNode node)
	{
		return node.matchesConstituentTag(p_constituentPattern);		
	}
}