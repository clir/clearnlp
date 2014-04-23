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
package com.clearnlp.verbnet;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clearnlp.util.FileUtils;
import com.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class VNLib
{
	private VNLib() {}
	
	/**
	 * @param dirPath a directory containing VerbNet XML files.
	 * @param discardNoFrame if {@code true}, discard classes with no frames.
	 */
	static public VNMap getVerbNetMap(String dirPath, boolean discardNoFrame)
	{
		List<String> filelist = FileUtils.getFileList(dirPath, ".xml", false);
		VNMap map = new VNMap();
		InputStream in;
		
		try
		{
			for (String filename : filelist)
			{
				in = new BufferedInputStream(new FileInputStream(filename));
				putVerbNetClasses(in, map, discardNoFrame);
			}
		}
		catch (Exception e) {e.printStackTrace();}
		
		return map;
	}
	
	/** Called by {@link #getVerbNetMap(String)}. */
	static private void putVerbNetClasses(InputStream in, VNMap map, boolean discardNoFrame)
	{
		Element eVNClass = XmlUtils.getDocumentElement(in);
		NodeList list = eVNClass.getElementsByTagName(VNXml.E_VNSUBCLASS);
		int i, size = list.getLength();
		VNClass vn; 
		
		vn = new VNClass(eVNClass);
		if (discardNoFrame && vn.getFrameSize() > 0) map.put(vn);
		
		for (i=0; i<size; i++)
		{
			eVNClass = (Element)list.item(i);
			vn = new VNClass(eVNClass);
			if (discardNoFrame && vn.getFrameSize() > 0) map.put(vn);
		}
	}
}