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
package edu.emory.clir.clearnlp.component.mode.ner;

import java.io.InputStream;

import org.w3c.dom.Element;

import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.utils.NLPMode;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERConfiguration extends AbstractConfiguration
{
	private String[] cluster_paths;
	private String dictionary_path;
//	private int collect_cutoff;
//	private Set<String> collect_labels;
	
//	============================== Initialization ==============================
	
	public NERConfiguration()
	{
		super(NLPMode.ner);
	}
	
	public NERConfiguration(InputStream in)
	{
		super(NLPMode.ner, in);
		initXml();
	}
	
	private void initXml()
	{
		Element eMode = getModeElement();
		
		setDictionaryPath(getTextContent(eMode, "dictionary_path"));
//		setCollectCutoff(getIntegerTextContent(eMode, "collect_cutoff"));
//		setCollectLabelSet(DSUtils.toHashSet(Splitter.splitCommas(getTextContent(eMode, "collect_labels"))));
	}
	
	public String getDictionaryPath()
	{
		return dictionary_path;
	}
	
	public String[] getClusterPaths()
	{
		return cluster_paths;
	}
	
	public void setDictionaryPath(String path)
	{
		dictionary_path = path;
	}
	
	public void setClusterPaths(String[] paths)
	{
		cluster_paths = paths;
	}

//	public int getCollectCutoff()
//	{
//		return collect_cutoff;
//	}
//	
//	public Set<String> getCollectLabelSet()
//	{
//		return collect_labels;
//	}
//	
//	public void setCollectCutoff(int dictionaryCutoff)
//	{
//		collect_cutoff = dictionaryCutoff;
//	}
//	
//	public void setCollectLabelSet(Set<String> set)
//	{
//		collect_labels = set;
//	}
}
