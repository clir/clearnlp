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
package com.clearnlp.lexicon.verbnet;

import java.io.Serializable;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clearnlp.util.XmlUtils;
import com.clearnlp.util.constant.StringConst;
import com.google.common.collect.Lists;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNClass implements Serializable
{
	private static final long serialVersionUID = 7613578224540878688L;
	
	private String        s_id;
	private String        s_lemma;
	private List<VNFrame> l_frames;
	
	public VNClass(Element eVNClass)
	{
		init(eVNClass);
	}
	
	private void init(Element eVNClass)
	{
		initID(eVNClass);
		initFrames(XmlUtils.getFirstElementByTagName(eVNClass, VNXml.E_FRAMES));
	}
	
	private void initID(Element eVNClass)
	{
		String id = XmlUtils.getTrimmedAttribute(eVNClass, VNXml.A_ID);
		int idx = id.indexOf('-');
		
		if (idx < 0)
		{
			System.err.println("Error: illegal format - "+id);
			set(id, StringConst.EMPTY);
		}
		else
			set(id.substring(idx+1), id.substring(0, idx));
	}
	
	private void initFrames(Element eFrames)
	{
		NodeList list = eFrames.getElementsByTagName(VNXml.E_FRAME);
		int i, size = list.getLength();
		Element eFrame;
		
		l_frames = Lists.newArrayList();
		
		for (i=0; i<size; i++)
		{
			eFrame = (Element)list.item(i);
			addFrame(new VNFrame(eFrame));
		}
	}
	
	public String getID()
	{
		return s_id;
	}
	
	public String getLemma()
	{
		return s_lemma;
	}
	
	public List<VNFrame> getFrameList()
	{
		return l_frames;
	}
	
	public VNFrame getFrame(int index)
	{
		return l_frames.get(index);
	}
	
	public int getFrameSize()
	{
		return l_frames.size();
	}
	
	/**
	 * @param id e.g., "13.5.1"
	 * @param lemma e.g., "get"
	 */
	public void set(String id, String lemma)
	{
		s_id    = id;
		s_lemma = lemma;
	}
	
	public void addFrame(VNFrame frame)
	{
		l_frames.add(frame);
	}
	
	/** @param id e.g., "26.6-1" */
	public boolean isID(String id)
	{
		return s_id.equals(id);
	}
}