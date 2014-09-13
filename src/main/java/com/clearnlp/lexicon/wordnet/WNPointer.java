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
package com.clearnlp.lexicon.wordnet;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WNPointer
{
	protected String	s_pointerSymbol;
	protected int		n_synsetOffset;
	protected char		c_posTag;
	protected short		n_source;
	protected short		n_target;
	
	public String getPointerSymbol()
	{
		return s_pointerSymbol;
	}
	
	public int getSynsetOffset()
	{
		return n_synsetOffset;
	}
	
	public char getPOSTag()
	{
		return c_posTag;
	}
	
	public short getSource()
	{
		return n_source;
	}
	
	public short getTarget()
	{
		return n_target;
	}
	
	public void setPointerSymbol(String symbol)
	{
		s_pointerSymbol = symbol;
	}
	
	public void setSynsetOffset(int synsetOffset)
	{
		n_synsetOffset = synsetOffset;
	}
	
	public void setPOSTag(char c)
	{
		c_posTag = c;
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