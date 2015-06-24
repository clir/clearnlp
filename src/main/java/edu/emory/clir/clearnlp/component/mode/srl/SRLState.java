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
package edu.emory.clir.clearnlp.component.mode.srl;

import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLState extends AbstractState<String, String>
{

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#initOracle()
	 */
	@Override
	protected void initOracle()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#resetOracle()
	 */
	@Override
	public void resetOracle()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#getGoldLabel()
	 */
	@Override
	public String getGoldLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#getNode(edu.emory.clir.clearnlp.feature.AbstractFeatureToken)
	 */
	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#next(java.lang.Object)
	 */
	@Override
	public void next(String label)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.state.AbstractState#isTerminate()
	 */
	@Override
	public boolean isTerminate()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
