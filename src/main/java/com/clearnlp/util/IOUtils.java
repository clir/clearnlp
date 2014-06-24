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
package com.clearnlp.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import com.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class IOUtils
{
	private IOUtils() {}
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. */
	static public BufferedReader createBufferedReader(InputStream in)
	{
		return new BufferedReader(new InputStreamReader(in));
	}
	
	static public BufferedReader createBufferedReader(String filename)
	{
		return createBufferedReader(createFileInputStream(filename));
	}
	
	/** @param in internally wrapped by {@code new PrintStream(new BufferedOutputStream(out))}. */
	static public PrintStream createBufferedPrintStream(OutputStream out)
	{
		return new PrintStream(new BufferedOutputStream(out));
	}
	
	static public PrintStream createBufferedPrintStream(String filename)
	{
		return createBufferedPrintStream(createFileOutputStream(filename));
	}
	
	static public FileInputStream createFileInputStream(String filename)
	{
		FileInputStream in = null;
		
		try
		{
			in = new FileInputStream(filename);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return in;
	}
	
	static public FileOutputStream createFileOutputStream(String filename)
	{
		FileOutputStream out = null;
		
		try
		{
			out = new FileOutputStream(filename);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return out;
	}
	
	/** @param in internally wrapped by {@code new ByteArrayInputStream(str.getBytes())}. */
	static public ByteArrayInputStream createByteArrayInputStream(String s)
	{
		return new ByteArrayInputStream(s.getBytes());
	}
	
	public static InputStream getInputStreamsFromClasspath(String path)
	{
		return IOUtils.class.getResourceAsStream(StringConst.FW_SLASH+path);
	}
}