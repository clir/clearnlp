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
	m,		// lemma
	p,		// pos tag
	n,		// named entity tag
	d,		// dependency label
	ds,		// dependency relation set
	ds2,	// grand-dependency relation set
	lv,		// left valency
	rv,		// right valency
	a,		// ambiguity class
	t,		// distance
	f1,		// simplified word-form
	f2,		// lowercase simplified word-form
	b,		// boolean
	prefix,
	suffix,
	feat,
	subcat,
	path,
	argn;
	
	static public final Pattern P_BOOLEAN  	= Pattern.compile("^"+b+"(\\d+)$");
	static public final Pattern P_PREFIX  	= Pattern.compile("^"+prefix+"(\\d+)$");
	static public final Pattern P_SUFFIX  	= Pattern.compile("^"+suffix+"(\\d+)$");
	static public final Pattern P_FEAT		= Pattern.compile("^"+feat+"=(.+)$");		
	static public final Pattern P_SUBCAT 	= Pattern.compile("^"+subcat+"(["+p+d+"])(\\d+)$");
	static public final Pattern P_PATH	 	= Pattern.compile("^"+path+"(["+p+d+t+"])(\\d+)$");
	static public final Pattern P_ARGN 	 	= Pattern.compile("^"+argn+"(\\d+)$");
	
	static public final int PATH_ALL		= 0;
	static public final int PATH_UP			= 1;
	static public final int PATH_DOWN		= 2;
	static public final int SUBCAT_ALL		= 0;
	static public final int SUBCAT_LEFT		= 1;
	static public final int SUBCAT_RIGHT	= 2;
}

//static public final String F_SIMPLIFIED_FORM			= "sf";
//static public final String F_LOWER_SIMPLIFIED_FORM	= "lsf";
//static public final String F_AMBIGUITY_CLASS			= "a";
//static public final String F_DIRECTION				= "dir";
//static public final String F_DISTANCE					= "n";
