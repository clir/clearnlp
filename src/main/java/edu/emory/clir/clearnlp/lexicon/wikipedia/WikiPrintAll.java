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
package edu.emory.clir.clearnlp.lexicon.wikipedia;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiPrintAll
{
	PrintStream f_out;
	AbstractTokenizer tokenizer;
	
	public WikiPrintAll(String zipFile, String outputFile) throws Exception
	{
		f_out = IOUtils.createBufferedPrintStream(outputFile);
		tokenizer = NLPUtils.getTokenizer(TLanguage.ENGLISH);
		@SuppressWarnings("resource")
		ZipFile file = new ZipFile(zipFile);
		
		Enumeration<? extends ZipEntry> entries = file.entries();
		ZipEntry entry;
		
		while (entries.hasMoreElements())
		{
			entry = entries.nextElement();
			System.out.println(entry.getName());
			print(IOUtils.createBufferedReader(file.getInputStream(entry)));
	    }
		
		f_out.close();
	}

//	=================================== addIndices ===================================

	public void print(BufferedReader reader) throws Exception
	{
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			if (line.startsWith(WikiIndexMap.NEW_PAGE) || line.startsWith(WikiIndexMap.NEW_PARAGRAPH))
				continue;

			line = Joiner.join(tokenizer.tokenize(line), " ");
			f_out.println(line);
		}
	}

	static public void main(String[] args) throws Exception
	{
		final String inputPath  = args[0];
		final String outputFile = args[1];
		new WikiPrintAll(inputPath, outputFile);
	}
}
