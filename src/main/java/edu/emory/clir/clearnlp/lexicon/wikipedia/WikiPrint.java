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

import java.io.ObjectInputStream;
import java.util.zip.ZipFile;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WikiPrint
{
	static public void main(String[] args)
	{
		WikiIndexMap map  = new WikiIndexMap();
		final String wikiFile  = args[0];
		final String indexFile = args[1];
		final String title = Joiner.join(args, " ", 2, args.length);
		
		try
		{
			ObjectInputStream in = new ObjectInputStream(IOUtils.createXZBufferedInputStream(indexFile));
			ZipFile zip = new ZipFile(wikiFile);
			map = (WikiIndexMap)in.readObject();
			System.out.println(map.getPage(zip, title));
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
