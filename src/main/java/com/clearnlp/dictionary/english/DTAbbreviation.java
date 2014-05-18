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
package com.clearnlp.dictionary.english;

import java.io.InputStream;
import java.util.Set;

import com.clearnlp.dictionary.DTPath;
import com.clearnlp.util.DSUtils;
import com.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DTAbbreviation
{
	private Set<String> SET_ABBR_PERIOD;
	
	public DTAbbreviation()
	{
		init(IOUtils.getInputStreamsFromClasspath(DTPath.EN_ABBREVIATION_PERIOD));
	}
	
	public DTAbbreviation(InputStream abbreviationPeriod)
	{
		init(abbreviationPeriod);
	}
	
	public void init(InputStream abbreviationPeriod)
	{
		SET_ABBR_PERIOD = DSUtils.createStringHashSet(abbreviationPeriod, true);
	}
	
	public boolean isAbbreviationEndingWithPeriod(String lower)
	{
		return SET_ABBR_PERIOD.contains(lower);
	}
}
