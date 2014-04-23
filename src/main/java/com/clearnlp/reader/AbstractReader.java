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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import com.clearnlp.util.IOUtils;
import com.clearnlp.util.adapter.Adapter1;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractReader<T>
{
	protected BufferedReader b_reader;
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in));}. */
	public AbstractReader(InputStream in)
	{
		open(in);
	}
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in));}. */
	public void open(InputStream in)
	{
		b_reader = IOUtils.createBufferedReader(in);
	}
	
	public void close()
	{
		try
		{
			b_reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public String readLine()
	{
		try
		{
			return b_reader.readLine();
		}
		catch (IOException e) {e.printStackTrace();}
		
		return null;
	}

	public void applyAll(Adapter1<T> adapter)
	{
		T item;
		
		while ((item = next()) != null)
			adapter.apply(item);
		
		close();
	}
	
	/** @return the next item if exists; otherwise, {@code null}. */
	abstract public T next();
}