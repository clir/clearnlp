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
}