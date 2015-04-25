/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.lexicon.dbpedia;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import edu.emory.clir.clearnlp.collection.tree.PrefixNode;
import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.ner.NERInfoSet;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PrefixTreeExtender
{
	private PrefixTree<String,NERInfoSet> prefix_tree;
	private AbstractTokenizer tokenizer;
	
	@SuppressWarnings("unchecked")
	public PrefixTreeExtender(InputStream in) throws Exception
	{
		ObjectInputStream oin = IOUtils.createObjectXZBufferedInputStream(in);
		System.out.println("Loading");
		prefix_tree = (PrefixTree<String,NERInfoSet>)oin.readObject();
		tokenizer = NLPUtils.getTokenizer(TLanguage.ENGLISH);
	}

	public void extend(InputStream in, String type) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		PrefixNode<String, NERInfoSet> node;
		NERInfoSet set;
		String[] array;
		String line;
		
		System.out.println("Extending");
		
		while ((line = reader.readLine()) != null)
		{
			line = line.trim();
			if (line.isEmpty()) continue;
			array = DSUtils.toArray(tokenizer.tokenize(line));
			node  = prefix_tree.add(array, 0, array.length, String::toString);
			set   = node.getValue();
			
			if (set == null)
			{
				set = new NERInfoSet();
				node.setValue(set);
			}
			
			set.addCategory(type);
		}	System.out.println();
		
		reader.close();
	}
	
	public void print(OutputStream out) throws Exception
	{
		ObjectOutputStream fout = IOUtils.createObjectXZBufferedOutputStream(out);
		System.out.println("Printing");
		fout.writeObject(prefix_tree);
		fout.close();
	}
	
	static public void main(String[] args) throws Exception
	{
		final String prefixFile = args[0];
		final String inputFile  = args[1];
		final String type       = args[2];
		final String outputFile = args[3];
		
		try
		{
			PrefixTreeExtender ex = new PrefixTreeExtender(IOUtils.createFileInputStream(prefixFile));
			ex.extend(IOUtils.createFileInputStream(inputFile), type);
			ex.print(IOUtils.createFileOutputStream(outputFile));
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
