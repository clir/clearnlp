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
package com.clearnlp.nlp.configure;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.clearnlp.util.XmlUtils;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DecodeConfiguration extends AbstractConfiguration
{
	private final Path p_model;
	
	public DecodeConfiguration(InputStream in)
	{
		super(in);
		p_model = initModelPath();
	}

//	=================================== MODEL PATH ===================================

	private Path initModelPath()
	{
		String path = XmlUtils.getTrimmedTextContentFromFirstElement(x_top, E_MODEL);
		return path != null ? Paths.get(path) : null;
	}
	
	public Path getModelPath()
	{
		return p_model;
	}
}
