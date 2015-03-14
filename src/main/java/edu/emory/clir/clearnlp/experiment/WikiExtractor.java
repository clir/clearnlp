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
package edu.emory.clir.clearnlp.experiment;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.lexicon.wikipedia.WikiMap;
import edu.emory.clir.clearnlp.lexicon.wikipedia.WikiPage;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.StringUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiExtractor
{
	public void extractNames(String[] args) throws Exception
	{
		List<String> filelist = FileUtils.getFileList(args[0], args[1], false);
		Set<String> names = getNameSet(args[2]);
		Collections.sort(filelist);
		PrintStream out;
		WikiPage page;
		WikiMap map;
		
		for (String filename : filelist)
		{
			System.out.println(filename);
			map = new WikiMap(IOUtils.createFileInputStream(filename));
			
			for (String title : map.getTitles())
			{
				page = map.getPage(title);
				
				if (page != null)
				{
					out = IOUtils.createBufferedPrintStream(args[3]+"/"+StringUtils.toLowerCase(title).replaceAll(" ","_")+".txt");
					out.print(page.toString());
					out.close();
				}
			}
			
			if (names.isEmpty()) break;
		}
	}
	
	Set<String> getNameSet(String filename) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(filename);
		Set<String> set = new HashSet<>();
		String line;
		
		while ((line = reader.readLine()) != null)
			set.add(line.trim());
		
		return set;
	}
	
	static public void main(String[] args) throws Exception
	{
//		String filepath  = args[0];
//		String extension = args[1];
	}
}
