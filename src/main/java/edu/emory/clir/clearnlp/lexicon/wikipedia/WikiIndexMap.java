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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiIndexMap implements Serializable
{
	private static final long serialVersionUID = -1930430749823956245L;
	public static final String NEW_PAGE        = "<New Page";
	public static final String NEW_PARAGRAPH   = "<New Paragraph";
	private Map<String,WikiIndex> title_pointer_map;
	
	public WikiIndexMap()
	{
		title_pointer_map = new HashMap<>();
	}
	
//	=================================== addIndices ===================================

	public void addIndices(String filename) throws Exception
	{
		RandomAccessFile in = new RandomAccessFile(filename, "r");
		long beginPointer = 0, prevPointer;
		String line, title = null;
		
		for (prevPointer = in.getFilePointer(); (line = in.readLine()) != null; prevPointer = in.getFilePointer())
		{
			if (line.startsWith(NEW_PAGE))
			{
				if (title != null) addIndex(title, filename, beginPointer);
				String[] s = Splitter.splitTabs(line);
				beginPointer = prevPointer;
				title = s[1].trim();
			}
		}
		
		addIndex(title, filename, beginPointer);
		in.close();
	}
	
	private void addIndex(String title, String filename, long beginPointer)
	{
		title_pointer_map.put(title, new WikiIndex(FileUtils.getBaseName(filename), beginPointer));
	}
	
//	=================================== getPage ===================================
	
	public WikiPage getPage(ZipFile zip, String title) throws Exception
	{
		WikiIndex index = title_pointer_map.get(title);

		if (index != null)
		{
			InputStream in = zip.getInputStream(zip.getEntry(index.getEntryName()));
			in.skip(index.getBeginPointer());
			return getPage(in, index);
		}
		
		return null;
	}
	
	public WikiPage getPage(InputStream in, WikiIndex index) throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		String[] s = Splitter.splitTabs(line);
		String title = s[1].trim();
		WikiPage page = new WikiPage(title);
		addParagraphs(reader, page);
		reader.close(); 
		return page;
	}
	
	private void addParagraphs(BufferedReader reader, WikiPage page) throws Exception
	{
		WikiParagraph paragraph = null;
		String line;
		
		while ((line = reader.readLine()) != null)
		{		
			line = line.trim();
			
			if (line.startsWith(NEW_PARAGRAPH))
			{
				paragraph = new WikiParagraph();
				page.addParagraph(paragraph);
			}
			else if (line.startsWith(NEW_PAGE))
				break;
			else
				paragraph.addSentence(line);
		}
	}
	
	public WikiIndex getIndex(String title)
	{
		return title_pointer_map.get(title);
	}
	
	public int size()
	{
		return title_pointer_map.size();
	}

	static public void main(String[] args)
	{
		WikiIndexMap map  = new WikiIndexMap();
		final String inputPath  = args[0];
		final String outputFile = args[1];
		
		try
		{
			for (String filename : FileUtils.getFileList(inputPath, "out", false))
			{
				System.out.println(filename);
				map.addIndices(filename);
			}
			
			ObjectOutputStream out = new ObjectOutputStream(IOUtils.createXZBufferedOutputStream(outputFile));
			out.writeObject(map);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
