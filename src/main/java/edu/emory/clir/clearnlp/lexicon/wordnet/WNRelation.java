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
package edu.emory.clir.clearnlp.lexicon.wordnet;



/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WNRelation
{
	private WNSynset w_synset;
	private short    n_source;
	private short    n_target;
	
	public WNSynset getWNSynset()
	{
		return w_synset;
	}
	
	public short getSource()
	{
		return n_source;
	}
	
	public short getTarget()
	{
		return n_target;
	}

	public void setWNSynset(WNSynset synset)
	{
		w_synset = synset;
	}
	
	public void setSource(short source)
	{
		n_source = source;
	}
	
	public void setTarget(short target)
	{
		n_target = target;
	}
}