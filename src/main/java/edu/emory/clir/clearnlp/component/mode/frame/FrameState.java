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
package edu.emory.clir.clearnlp.component.mode.frame;

import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFFrameset;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFMap;
import edu.emory.clir.clearnlp.lexicon.propbank.frameset.PBFType;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FrameState extends AbstractState<String,String>
{
	static public String UNKNOWN_ROLESET_ID = ".XX";
	private RoleLexicon role_lexicon;
	private PBFMap frame_map;
	private int i_input;
	
//	====================================== INITIALIZATION ======================================
	
	public FrameState(DEPTree tree, CFlag flag, RoleLexicon lexicon, PBFMap frameMap)
	{
		super(tree, flag);
		init(lexicon, frameMap);
	}
	
	private void init(RoleLexicon lexicon, PBFMap frameMap)
	{
		role_lexicon = lexicon;
		frame_map = frameMap; 
		setInput(0);
	}
	
//	====================================== ORACLE/LABEL ======================================
	
	@Override
	protected void initOracle()
	{
		g_oracle = d_tree.getFeatureTags(DEPLib.FEAT_PB);
		d_tree.clearFeatureTags(DEPLib.FEAT_PB);
	}

	@Override
	public void resetOracle()
	{
		d_tree.setFeatureTags(DEPLib.FEAT_PB, g_oracle);
	}

	@Override
	public String getGoldLabel()
	{
		return g_oracle[i_input];
	}
	
//	====================================== NODE ======================================

	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		int id = i_input + token.getOffset();
		return (0 < id) ? getNodeRelation(token, getNode(id)) : null;
	}
	
	public DEPNode getInput()
	{
		return getNode(i_input);
	}
	
	public void setInput(int id)
	{
		i_input = id;
	}
	
//	====================================== TRANSITION ======================================
	
	@Override
	public void next(String label)
	{
		getInput().putFeat(DEPLib.FEAT_PB, label);
		shift();
	}
	
	public void shift()
	{
		PBFFrameset frameset;
		String rolesetID;
		DEPNode node;
		
		for (++i_input; i_input<t_size; i_input++)
		{
			node = getInput();
			
			if (role_lexicon.isVerbPredicate(node))
			{
				frameset = frame_map.getFrameset(PBFType.VERB, node.getLemma());
				
				if (frameset == null)
					node.putFeat(DEPLib.FEAT_PB, node.getLemma()+UNKNOWN_ROLESET_ID);
				else if ((rolesetID = frameset.getMonosemousRolesetID()) != null)
					node.putFeat(DEPLib.FEAT_PB, rolesetID);
				else
					break;
			}
		}
	}

	@Override
	public boolean isTerminate()
	{
		return i_input >= t_size;
	}
	
//	====================================== FEATURES ======================================

	@Override
	public boolean extractWordFormFeature(DEPNode node)
	{
		return true;
	}
}