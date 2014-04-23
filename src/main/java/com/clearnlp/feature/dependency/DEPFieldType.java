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

import java.util.regex.Pattern;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public enum DEPFieldType
{
	f,		// word-form
	p,		// pos tag
	m,		// lemma
	d,		// dependency label
	n,		// named entity tag
	a,		// ambiguity class
	t,		// distance
	sf,		// simplified word-form
	lsf,	// lowercase simplified word-form
	ds,		// dependency relation set
	ds2,	// grand-dependency relation set
	lv,		// left valency
	rv;		// right valency
	
	static public final Pattern P_BOOLEAN  	= Pattern.compile("^b(\\d+)$");
	static public final Pattern P_PREFIX  	= Pattern.compile("^pf(\\d+)$");
	static public final Pattern P_SUFFIX  	= Pattern.compile("^sf(\\d+)$");
	static public final Pattern P_FEAT		= Pattern.compile("^ft=(.+)$");		
	static public final Pattern P_SUBCAT 	= Pattern.compile("^sc(["+DEPFieldType.p+DEPFieldType.d+"])(\\d+)$");
	static public final Pattern P_PATH	 	= Pattern.compile("^pt(["+DEPFieldType.p+DEPFieldType.d+DEPFieldType.t+"])(\\d+)$");
	static public final Pattern P_ARGN 	 	= Pattern.compile("^argn(\\d+)$");
}