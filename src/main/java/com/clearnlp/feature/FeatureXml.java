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
package com.clearnlp.feature;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public interface FeatureXml
{
	String E_FEATURE	= "feature";
	String E_CUTOFF		= "cutoff";

	String A_TYPE		= "t";
	String A_LABEL		= "label";
	String A_FEATURE	= "feature";
	String A_VISIBLE	= "visible";
	
	String V_SET		= "s";
	String V_BOOL		= "b";
}