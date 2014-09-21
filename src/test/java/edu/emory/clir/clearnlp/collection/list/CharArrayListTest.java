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
package edu.emory.clir.clearnlp.collection.list;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.list.CharArrayList;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CharArrayListTest
{
	@Test
	public void test() throws Exception
	{
		CharArrayList list = new CharArrayList();
		char[] items = {'1', '2', '3'};
		int i, size = items.length;
		
		for (char item : items)
			list.add(item);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
		out.writeObject(list);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bout.toByteArray())));
		list = (CharArrayList)in.readObject();
		in.close();
		
		for (i=0; i<size; i++)
			assertEquals(items[i], list.get(i));
		
		CharArrayList clone = list.clone();
		clone.set(1, '4');
		
		assertEquals('2', list .get(1));
		assertEquals('4', clone.get(1));
		
		clone.remove(0);
		assertEquals(2, clone.size());
		assertEquals('4', clone.get(0));
	}
}