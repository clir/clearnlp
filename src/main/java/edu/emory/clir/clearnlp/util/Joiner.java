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
package edu.emory.clir.clearnlp.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Joiner
{
	static public <T>String join(List<T> list, String delim, int beginIndex, int endIndex, Function<T,String> f)
	{
		if (endIndex - beginIndex == 0) return StringConst.EMPTY;
		StringJoiner build = new StringJoiner(delim);
		
		for (int i=beginIndex; i<endIndex; i++)
			build.add(f.apply(list.get(i)));
		
		return build.toString();
	}
	
	static public <T>String join(List<T> list, String delim, int beginIndex, int endIndex)
	{
		return join(list, delim, beginIndex, endIndex, T::toString);
	}
	
	static public <T extends Comparable<T>>String join(List<T> list, String delim, boolean sort)
	{
		if (sort) Collections.sort(list);
		return join(list, delim, 0, list.size());
	}
	
	static public <T>String join(Collection<T> collection, String delim)
	{
		if (collection.isEmpty()) return StringConst.EMPTY;
		StringJoiner build = new StringJoiner(delim);
		
		for (T item : collection)
			build.add(item.toString());
		
		return build.toString();
	}
	
	static public <T>String join(T[] array, String delim, int beginIndex, int endIndex, Function<T,String> f)
	{
		if (endIndex - beginIndex == 0) return StringConst.EMPTY;
		StringJoiner build = new StringJoiner(delim);
		
		for (int i=beginIndex; i<endIndex; i++)
			build.add(f.apply(array[i]));
		
		return build.toString();
	}
	
	static public <T>String join(T[] array, String delim, int beginIndex, int endIndex)
	{
		if (endIndex - beginIndex == 0) return StringConst.EMPTY;
		StringJoiner build = new StringJoiner(delim);
		
		for (int i=beginIndex; i<endIndex; i++)
			build.add(array[i].toString());
		
		return build.toString();
	}
	
	static public <T>String join(T[] array, String delim)
	{
		return join(array, delim, 0, array.length);
	}
	
	static public <T>String joinObject(List<ObjectDoublePair<T>> ps, String delim)
	{
		StringJoiner build = new StringJoiner(delim);
		
		for (ObjectDoublePair<T> p : ps)
			build.add(p.o.toString());
		
		return build.toString();
	}
}