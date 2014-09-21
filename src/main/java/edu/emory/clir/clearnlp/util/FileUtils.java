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
package edu.emory.clir.clearnlp.util;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.io.FileExtensionFilter;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FileUtils
{
	private FileUtils() {}
	
	/**
	 * @return a list of filenames with the specific extension in the specific directory.
	 * If the path is a file, returns a list of the specific filename.
	 * @see FileExtensionFilter
	 */
	static public List<String> getFileList(String path, String extension, boolean recursive)
	{
		List<String> list = Lists.newArrayList();
		File file = new File(path);
		
		if (file.isFile())
			list.add(path);
		else if (recursive)
			getFileListRec(path, extension, list);
		else
		{
			for (String name : file.list(new FileExtensionFilter(extension)))
			{
				name = path + StringConst.FW_SLASH + name;
				if (new File(name).isFile()) list.add(name);
			}
		}
		
		return list;
	}
	
	/** Called by {@link #getFileList(String, String, boolean)}. */
	static private void getFileListRec(String path, String extension, List<String> list)
	{
		for (String name : new File(path).list())
		{
			name = path + StringConst.FW_SLASH + name;
			
			if (new File(name).isDirectory())
				getFileListRec(name, extension, list);
			else if (name.endsWith(extension))
				list.add(name);
		}
	}
	
	/**
	 * Replaces the extension of a filename with the specific extension.
	 * If the filename does not end with the extension, return {@code null}. 
	 */
	static public String replaceExtension(String filename, String newExt)
	{
		int idx = filename.lastIndexOf(StringConst.PERIOD);
		return (idx >= 0) ? filename.substring(0, idx+1) + newExt : null;
	}
	
	/**
	 * Replaces the old extension to the new extension.
	 * If the filename does not end with the old extension, return {@code null}.
	 */
	static public String replaceExtension(String filename, String oldExt, String newExt)
	{
		return filename.endsWith(oldExt) ? filename.substring(0, filename.length()-oldExt.length()) + newExt : null; 
	}
}