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
package edu.emory.clir.clearnlp.morphology.english;

import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.morphology.AbstractAffixMatcher;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishDerivation
{
	String      base_pos;
	Set<String> base_set;
	List<AbstractAffixMatcher> suffix_matchers;
	
	public EnglishDerivation(String basePOS, Set<String> baseSet, List<AbstractAffixMatcher> affixMatchers)
	{
		init(basePOS, baseSet, affixMatchers);
	}
	
	private void init(String basePOS, Set<String> baseSet, List<AbstractAffixMatcher> affixMatchers)
	{
		base_pos        = basePOS;
		base_set        = baseSet;
		suffix_matchers = affixMatchers;
		
		if      (base_set == null)
			throw new IllegalArgumentException("The base set must not be null.");
		else if (suffix_matchers == null)
			throw new IllegalArgumentException("The suffix matcher list must not be null.");
	}
	
	public String getBasePOS()
	{
		return base_pos;
	}
	
	public Set<String> getBaseSet()
	{
		return base_set;
	}
	
	public List<AbstractAffixMatcher> getSuffixMatchers()
	{
		return suffix_matchers;
	}
	
	public boolean isBaseForm(String form)
	{
		return base_set.contains(form);
	}
	
	/** @param form the word-form in lower-case. */
	public String getBaseForm(String form, String pos)
	{
		String token;
		
		if ((token = getBaseFormFromSuffixes(form, pos)) != null)
			return token;
			
		return null;
	}

	public String getBaseFormFromSuffixes(String form, String pos)
	{
		String base;
		
		for (AbstractAffixMatcher matcher : suffix_matchers)
		{
			base = matcher.getBaseForm(base_set, form, pos);
			if (base != null) return base;
		}
		
		return null;
	}
}
