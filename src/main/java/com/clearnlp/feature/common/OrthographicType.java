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

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface OrthographicType
{
	String HYPERLINK			= "0";
	String ALL_UPPER			= "1";
	String ALL_LOWER			= "2";
	String ALL_DIGIT			= "3";
	String ALL_PUNCT			= "4";
	String ALL_DIGIT_OR_PUNCT	= "5";
	String HAS_DIGIT			= "6";
	String HAS_PUNCT			= "7";
	String NO_LOWER				= "8";
	String FST_UPPER			= "9";
	String UPPER_1				= "10";
	String UPPER_2				= "11";
}
