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
package edu.emory.clir.clearnlp.component.mode.srl.state;

import edu.emory.clir.clearnlp.component.mode.srl.SRLConfiguration;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSLibEn;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishSRLState extends AbstractSRLState
{
	public EnglishSRLState(DEPTree tree, CFlag flag, SRLConfiguration configuration)
	{
		super(tree, flag, configuration);
	}

	@Override
	protected boolean isPredicate(DEPNode node)
	{
		return isVerbPredicate(node);
	}
	
	private boolean isVerbPredicate(DEPNode node)
	{
		if (!node.getLabel().startsWith(DEPLibEn.DEP_AUX))
		{
			String pos;
			
			return POSLibEn.isVerb(node.getPOSTag()) ||
			       ((pos = node.getFeat(DEPLib.FEAT_POS2)) != null && POSLibEn.isVerb(pos));
		}
		
		return false;
	}
}
