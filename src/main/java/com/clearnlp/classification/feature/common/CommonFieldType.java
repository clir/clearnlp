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
package com.clearnlp.classification.feature.common;

import java.util.regex.Pattern;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public enum CommonFieldType
{
	f,		// word-form
	f2, 	// simplified word-form
	m,		// lemma
	p,		// pos tag
	n,		// named entity tag
	d,		// dependency label to its head
	ds,		// set of dependency labels of its dependents
	ds2,	// set of dependency labels of its grand-dependents
	orth,	// set of orthographic features
	
	b,		// boolean
	ft;		// feats
	
	static public final Pattern P_BOOLEAN = Pattern.compile("^"+b+"(\\d+)$");
	static public final Pattern P_FEAT    = Pattern.compile("^"+ft+"=(.+)$");
	
	static public boolean isSetField(CommonFieldType field)
	{
		return field == ds || field == ds2 || field == orth;
	}
}
