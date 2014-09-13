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

import com.clearnlp.constituent.CTNode;
import com.clearnlp.lexicon.propbank.PBArgument;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class PBArc extends AbstractArc<CTNode>
{
	public PBArc(CTNode node, String label)
	{
		set(node, label);
	}

	@Override
	public String toString()
	{
		return n_node.getTerminalID() + PBArgument.DELIM + s_label;
	}
	
	@Override
	public int compareTo(AbstractArc<CTNode> arc)
	{
		return n_node.compareTo(arc.getNode());
	}
}