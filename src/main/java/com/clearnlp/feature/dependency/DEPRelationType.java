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


// TODO: Auto-generated Javadoc
/**
 * The Enum DEPRelationType.
 *
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 * @since 3.0.0
 */
public enum DEPRelationType
{
 	
	 /** The h. */
	 h,		// head
	/** The lmd. */
		lmd,	// left-most dependent
	/** The rmd. */
	rmd,	// right-most dependent
	/** The lnd. */
	lnd,	// left-nearest dependent
	/** The rnd. */
	rnd,	// right-nearest dependent
	/** The lns. */
	lns,	// left-nearest sibling
	/** The rns. */
	rns,	// right-nearest sibling
	
	/** The h2. */
	h2,		// grand head
	/** The lmd2. */
		lmd2,	// 2nd left-most dependent
	/** The rmd2. */
	rmd2,	// 2nd right-most dependent
	/** The lnd2. */
	lnd2,	// 2nd left-nearest dependent
	/** The rnd2. */
	rnd2,	// 2nd right-nearest dependent
	/** The lns2. */
	lns2,	// 2nd left-nearest sibling
	/** The rns2. */
	rns2;	// 2nd right-nearest sibling
}