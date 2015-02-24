/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.constituent;

import java.util.List;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTLib
{
	static public String toForms(List<CTNode> tokens, int beginIndex, int endIndex, String delim)
	{
		StringBuilder build = new StringBuilder();
		int i;
		
		for (i=beginIndex; i<endIndex; i++)
		{
			build.append(delim);
			build.append(tokens.get(i).getWordForm());
		}
		
		return build.substring(delim.length());
	}
}
