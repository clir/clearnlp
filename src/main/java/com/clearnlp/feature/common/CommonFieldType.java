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

import java.util.regex.Pattern;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public enum CommonFieldType
{
	f,		// word-form
	f2, 	// simplified word-form
	f3,		// lower simplified word-form (POS tagging) 
	m,		// lemma
	p,		// pos tag
	n,		// named entity tag
	d,		// dependency label to its head
	ft,		// feats
	a,		// ambiguity class (POS tagging)

	pf,		// set of prefixes (POS tagging)
	sf,		// set of suffixes (POS tagging)
	ds,		// set of dependency labels of its dependents
	ds2,	// set of dependency labels of its grand-dependents
	orth,	// set of orthographic features
	
	b;		// boolean
	
	static public final Pattern P_BOOLEAN = Pattern.compile("^"+b+"(\\d+)$");
	static public final Pattern P_FEAT    = Pattern.compile("^"+ft+"=(.+)$");
	static public final Pattern P_PREFIX  = Pattern.compile("^"+pf+"(\\d+)$");
	static public final Pattern P_SUFFIX  = Pattern.compile("^"+sf+"(\\d+)$");
	
	static public boolean isBooleanField(CommonFieldType field)
	{
		return field == b;
	}
	
	static public boolean isSetField(CommonFieldType field)
	{
		return field == pf || field == sf || field == ds || field == ds2 || field == orth;
	}
}
