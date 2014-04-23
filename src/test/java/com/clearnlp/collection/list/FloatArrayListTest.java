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
package com.clearnlp.collection.list;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class FloatArrayListTest
{
	@Test
	public void test() throws Exception
	{
		FloatArrayList list = new FloatArrayList();
		float[] items = {0.1f, 2.3f, 4.5f};
		int i, size = items.length;
		
		for (float item : items)
			list.add(item);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
		out.writeObject(list);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bout.toByteArray())));
		list = (FloatArrayList)in.readObject();
		in.close();
		
		for (i=0; i<size; i++)
			assertEquals(items[i], list.get(i), 0);
		
		FloatArrayList clone = list.clone();
		clone.set(1, 6.7f);
		
		assertEquals(2.3f, list .get(1), 0);
		assertEquals(6.7f, clone.get(1), 0);
		
		clone.remove(0);
		assertEquals(2, clone.size());
		assertEquals(6.7f, clone.get(0), 0);
	}
}