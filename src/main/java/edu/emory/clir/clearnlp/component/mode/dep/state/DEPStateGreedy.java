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
package edu.emory.clir.clearnlp.component.mode.dep.state;

import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.mode.dep.DEPTransition;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPStateGreedy extends AbstractDEPState implements DEPTransition
{
//	====================================== Initialization ======================================
	
	public DEPStateGreedy() {super();}
	
	public DEPStateGreedy(DEPTree tree, CFlag flag, DEPConfiguration configuration)
	{
		super(tree, flag, configuration);
	}

//	====================================== BRANCH ======================================

	@Override
	public boolean startBranching() {return false;}

	@Override
	public boolean nextBranch() {return false;}

	@Override
	public void saveBranch(StringPrediction[] ps) {}

	@Override
	public void saveBest(List<StringInstance> instances) {}

	@Override
	public List<StringInstance> setBest() {return null;}

//	====================================== STATE HISTORY ======================================
	
//	private StringBuilder s_states = new StringBuilder();
//	
//	private void saveState(DEPLabel label)
//	{
//		s_states.append(label.toString());
//		s_states.append(StringConst.TAB);
//		s_states.append(getState(d_stack));
//		s_states.append(StringConst.TAB);
//		s_states.append(getState(d_inter));
//		s_states.append(StringConst.TAB);
//		s_states.append(i_input);
//		s_states.append(StringConst.NEW_LINE);
//	}
//	
//	private String getState(List<DEPNode> nodes)
//	{
//		StringBuilder build = new StringBuilder();
//		build.append("[");
//		
//		if (nodes.size() > 0)
//			build.append(nodes.get(0).getID());
//		
//		if (nodes.size() > 1)
//		{
//			build.append(",");
//			build.append(nodes.get(nodes.size()-1).getID());
//		}
//		
//		build.append("]");
//		return build.toString();
//	}
//	
//	public String stateHistory()
//	{
//		return s_states.toString();
//	}
//	
//	private String getSnapshot()
//	{
//		StringBuilder build = new StringBuilder();
//		int i;
//		
//		for (i=i_stack.size()-1; i>0; i--)
//		{
//			build.append(i_stack.get(i));
//			build.append(StringConst.COMMA);
//		}	build.append(StringConst.PIPE);
//		
//		for (i=i_inter.size()-1; i>=0; i--)
//		{
//			build.append(i_inter.get(i));
//			build.append(StringConst.COMMA);
//		}	build.append(StringConst.PIPE);
//		
//		build.append(i_input);
//		return build.toString();
//	}
}