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
package com.clearnlp.util.regex;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.clearnlp.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class Joiner
{
	static public <T extends Comparable<T>>String join(List<T> list, String delim, boolean sort)
	{
		if (sort) Collections.sort(list);
		return join(list, delim);
	}
	
	static public <T>String join(Collection<T> collection, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (T item : collection)
		{
			build.append(delim);
			build.append(item.toString());
		}
		
		return collection.isEmpty() ? StringConst.EMPTY : build.substring(delim.length());
	}
	
	static public <T>String join(T[] array, String delim)
	{
		StringBuilder build = new StringBuilder();
		
		for (T item : array)
		{
			build.append(delim);
			build.append(item.toString());
		}
		
		return array.length == 0 ? StringConst.EMPTY : build.substring(delim.length());
	}
}