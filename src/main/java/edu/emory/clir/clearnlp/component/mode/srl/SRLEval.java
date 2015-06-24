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

import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLEval extends AbstractEval<String>
{

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.evaluation.AbstractEval#countCorrect(edu.emory.clir.clearnlp.dependency.DEPTree, java.lang.Object[])
	 */
	@Override
	public void countCorrect(DEPTree sTree, String[] gLabels)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.evaluation.AbstractEval#getScore()
	 */
	@Override
	public double getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.evaluation.AbstractEval#clear()
	 */
	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		
	}

}
