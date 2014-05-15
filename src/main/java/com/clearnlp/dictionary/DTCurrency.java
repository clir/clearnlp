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

import java.io.InputStream;
import java.util.Set;

import com.clearnlp.util.DSUtils;
import com.clearnlp.util.IOUtils;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DTCurrency
{
	private Set<String> s_currency;
	private Set<String> s_dollar;
	
	public DTCurrency()
	{
		InputStream currency = IOUtils.getInputStreamsFromClasspath(DTPath.PATH_CURRENCY);
		InputStream dollar   = IOUtils.getInputStreamsFromClasspath(DTPath.PATH_CURRENCY_DOLLAR);

		init(currency, dollar);
	}
	
	public DTCurrency(InputStream currency, InputStream dollar)
	{
		init(currency, dollar);
	}
	
	public void init(InputStream currency, InputStream dollar)
	{
		s_currency = DSUtils.createStringHashSet(currency, true);
		s_dollar   = DSUtils.createStringHashSet(dollar  , true);
	}
	
	public boolean isCurrencyDollar(String lower)
	{
		return s_dollar.contains(lower);
	}
	
	public boolean isCurrency(String lower)
	{
		return s_currency.contains(lower);
	}
}