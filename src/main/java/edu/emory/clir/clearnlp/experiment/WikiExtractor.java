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
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
	static public void main(String[] args) throws Exception
	{
		List<String> filelist = FileUtils.getFileList(args[0], args[1], false);
		Set<String> names = getNameSet(args[2]);
		Collections.sort(filelist);
		List<String> remove;
		PrintStream out;
		WikiPage page;
		WikiMap map;
		
		for (String filename : filelist)
		{
			System.out.println(filename);
			map = new WikiMap();
			map.addPages(IOUtils.createFileInputStream(filename));
			remove = Lists.newArrayList();
			
			for (String name : names)
			{
				page = map.getPage(name);
				
				if (page != null)
				{
					out = IOUtils.createBufferedPrintStream(args[3]+"/"+StringUtils.toLowerCase(name).replaceAll(" ","_")+".txt");
					out.print(page.toString());
					out.close();
					remove.add(name);
				}
			}
			
			names.removeAll(remove);
			if (names.isEmpty()) break;
		}
	}
	
	static Set<String> getNameSet(String filename) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(filename);
		Set<String> set = Sets.newHashSet();
		String line;
		
		while ((line = reader.readLine()) != null)
			set.add(line.trim());
		
		return set;
	}
}
