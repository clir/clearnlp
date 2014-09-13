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
package com.clearnlp.classification.configuration;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AbstractTrainConfiguration
{
	private byte i_vectorType;
	private boolean b_binary;
	
	public AbstractTrainConfiguration(byte vectorType, boolean binary)
	{
		setVectorType(vectorType);
		setBinary(binary);
	}

	public byte getVectorType()
	{
		return i_vectorType;
	}
	
	public boolean isBinary()
	{
		return b_binary;
	}

	public void setVectorType(byte vectorType)
	{
		i_vectorType = vectorType;
	}
	
	public void setBinary(boolean binary)
	{
		b_binary = binary;
	}
}