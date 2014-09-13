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
package com.clearnlp.collection.map;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.junit.Test;

import com.clearnlp.collection.pair.ObjectCharPair;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CharObjectHashMapTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void test() throws Exception
	{
		List<ObjectCharPair<String>> items = Lists.newArrayList(new ObjectCharPair<String>("A",'A'),new ObjectCharPair<String>("B",'B'),new ObjectCharPair<String>("C",'C'));
		CharObjectHashMap<String> map = new CharObjectHashMap<>();
		
		for (ObjectCharPair<String> item : items)
			map.put(item.c, item.o);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
		out.writeObject(map);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bout.toByteArray())));
		map = (CharObjectHashMap<String>)in.readObject();
		in.close();
		
		for (ObjectCharPair<String> item : items)
			assertEquals(item.o, map.get(item.c));
	}
}