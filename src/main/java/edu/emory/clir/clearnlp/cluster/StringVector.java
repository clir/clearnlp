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
package edu.emory.clir.clearnlp.cluster;

import edu.emory.clir.clearnlp.collection.map.ObjectDoubleHashMap;

/**
 * @since 3.1.2
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringVector
{
	private ObjectDoubleHashMap<String> term_map;
	
	public StringVector()
	{
		term_map = new ObjectDoubleHashMap<>();
	}
	
	public void add(String term)
	{
		term_map.add(term, 1);
	}

}
