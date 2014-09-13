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
package com.clearnlp.reader;

import java.io.IOException;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class RawReader extends AbstractReader<String>
{
	public RawReader()
	{
		super(TReader.RAW);
	}
	
	@Override
	public String next()
	{
		try
		{
			StringBuilder build = new StringBuilder();
			char[] buffer = new char[1024 * 4];
			int n = 0;
			
			while ((n = b_reader.read(buffer)) != -1)
				build.append(buffer, 0, n);
			
			return build.toString();
		}
		catch (IOException e) {e.printStackTrace();}
		
		return null;
	}
}
