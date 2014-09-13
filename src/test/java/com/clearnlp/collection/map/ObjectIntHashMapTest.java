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

import com.clearnlp.collection.pair.ObjectIntPair;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ObjectIntHashMapTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void test() throws Exception
	{
		List<ObjectIntPair<String>> items = Lists.newArrayList(new ObjectIntPair<String>("A",1),new ObjectIntPair<String>("B",2),new ObjectIntPair<String>("C",3));
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		
		for (ObjectIntPair<String> item : items)
			map.put(item.o, item.i);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
		out.writeObject(map);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bout.toByteArray())));
		map = (ObjectIntHashMap<String>)in.readObject();
		in.close();
		
		for (ObjectIntPair<String> item : items)
			assertEquals(item.i, map.get(item.o));
		
		assertEquals("[(C,3), (B,2), (A,1)]", map.toList().toString());
	}
	
	@Test
	public void testAdd() throws Exception
	{
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		
		map.add("A");
		map.add("B");
		map.add("A");
		map.add("C");
		map.add("B");
		
		assertEquals(2, map.get("A"));
		assertEquals(2, map.get("B"));
		assertEquals(1, map.get("C"));
		assertEquals(0, map.get("D"));
	}
}