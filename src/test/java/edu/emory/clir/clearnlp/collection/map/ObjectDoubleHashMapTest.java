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
package edu.emory.clir.clearnlp.collection.map;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ObjectDoubleHashMapTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void test() throws Exception
	{
		List<ObjectDoublePair<String>> items = DSUtils.toArrayList(new ObjectDoublePair<String>("A",1.5),new ObjectDoublePair<String>("B",2.5),new ObjectDoublePair<String>("C",3.5));
		ObjectDoubleHashMap<String> map = new ObjectDoubleHashMap<>();
		
		for (ObjectDoublePair<String> item : items)
			map.put(item.o, item.d);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
		out.writeObject(map);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bout.toByteArray())));
		map = (ObjectDoubleHashMap<String>)in.readObject();
		in.close();
		
		for (ObjectDoublePair<String> item : items)
			assertEquals(item.d, map.get(item.o), 0);
		
		assertEquals("[(C,3.5), (B,2.5), (A,1.5)]", map.toList().toString());
	}
	
	@Test
	public void testAdd() throws Exception
	{
		ObjectDoubleHashMap<String> map = new ObjectDoubleHashMap<>();
		
		map.add("A", 1.2);
		map.add("B", 2.3);
		map.add("A", 1.5);
		map.add("C", 3.1);
		map.add("B", 2.4);
		
		assertEquals(2.7, map.get("A"), 0);
		assertEquals(4.7, map.get("B"), 0.01);
		assertEquals(3.1, map.get("C"), 0);
		assertEquals(0.0, map.get("D"), 0);
	}
}