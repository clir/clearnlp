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
package com.clearnlp.util.arc;

import java.util.regex.Pattern;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractArc<NodeType> implements Comparable<AbstractArc<NodeType>>
{
	/** The delimiter between node and label. */
	static public final String DELIM  = ":";
	
	protected NodeType n_node;
	protected String   s_label;
	
	public NodeType getNode()
	{
		return n_node;
	}
	
	public String getLabel()
	{
		return s_label;
	}
	
	public void setNode(NodeType node)
	{
		n_node = node;
	}
	
	public void setLabel(String label)
	{
		s_label = label;
	}
	
	public void clear()
	{
		set(null, null);
	}
	
	public void set(NodeType node, String label)
	{
		n_node  = node;
		s_label = label;
	}
	
	public boolean isNode(NodeType node)
	{
		return n_node == node;
	}
	
	public boolean isLabel(String label)
	{
		return label.equals(s_label);
	}
	
	public boolean isLabel(Pattern pattern)
	{
		return s_label != null && pattern.matcher(s_label).find();
	}
	
	public boolean equals(NodeType node, String label)
	{
		return isNode(node) && isLabel(label);
	}
	
	public boolean equals(NodeType node, Pattern pattern)
	{
		return isNode(node) && isLabel(pattern);
	}
}