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
package edu.emory.clir.clearnlp.feature.type;

import java.util.regex.Pattern;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public enum FieldType
{
	f,		// word-form
	f2, 	// simplified word-form
	f3,		// lower simplified word-form
	f4,		// word-form shape
	pf,		// prefix
	sf,		// suffix
	m,		// lemma
	p,		// pos tag
	n,		// named entity tag
	d,		// dependency label to its head
	v,		// valency
	sc,		// sub-categorization
	ft,		// feats
	a,		// ambiguity class (part-of-speech tagging)
	t,		// distance between i to j (dependency parsing, semantic role labeling)
	pt,		// path between i and j (dependency parsing, semantic role labeling)
	dsw,	// distributional semantics: word
	dsls,	// distributional semantics: lower-case, simplified

	as,		// set of ambiguity classes
	ds,		// set of dependency labels of its dependents
	ds2,	// set of dependency labels of its grand-dependents
	orth,	// set of orthographic features
	
	b;		// boolean #
	
	static public final Pattern P_DSW     = Pattern.compile("^"+dsw+"(\\d+)$");
	static public final Pattern P_DSLS    = Pattern.compile("^"+dsls+"(\\d+)$");
	static public final Pattern P_BOOLEAN = Pattern.compile("^"+b+"(\\d+)$");
	static public final Pattern P_FEAT    = Pattern.compile("^"+ft+"=(.+)$");
	static public final Pattern P_PREFIX  = Pattern.compile("^"+pf+"(\\d+)$");
	static public final Pattern P_SUFFIX  = Pattern.compile("^"+sf+"(\\d+)$");
	static public final Pattern P_SUBCAT  = Pattern.compile("^"+sc+"("+DirectionType.l+"|"+DirectionType.r+"|"+DirectionType.a+")(.)$");
	static public final Pattern P_VALENCY = Pattern.compile("^"+v +"("+DirectionType.l+"|"+DirectionType.r+"|"+DirectionType.a+")$");
	static public final Pattern P_DEPENDENTS       = Pattern.compile("^"+ds +"(.)$");
	static public final Pattern P_GRAND_DEPENDENTS = Pattern.compile("^"+ds2+"(.)$");
	
	static public boolean isBooleanField(FieldType field)
	{
		return field == b;
	}
	
	static public boolean isSetField(FieldType field)
	{
		return field == as || field == ds || field == ds2 || field == orth || field == dsw || field == dsls;
	}
}
