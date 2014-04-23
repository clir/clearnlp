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
package com.clearnlp.dictionary;

import java.util.Set;

import com.clearnlp.util.DSUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DTCurrency
{
	protected final Set<String> SET_CURRENCIES;
	
	public DTCurrency()
	{
		SET_CURRENCIES = DSUtils.createStringSetFromClasspath("com/clearnlp/dictionary/universal/currencies.txt");
	}
	
	public boolean isCurrency(String lower)
	{
		return SET_CURRENCIES.contains(lower);
	}
}