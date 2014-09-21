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
package edu.emory.clir.clearnlp.dictionary.english;

import java.io.InputStream;
import java.util.Set;

import com.clearnlp.dictionary.DTPath;

import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DTAbbreviation
{
	private Set<String> s_period;
	
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
		s_period = DSUtils.createStringHashSet(abbreviationPeriod, true, true);
	}
	
	public boolean isAbbreviationEndingWithPeriod(String lower)
	{
		return s_period.contains(lower);
	}
}
