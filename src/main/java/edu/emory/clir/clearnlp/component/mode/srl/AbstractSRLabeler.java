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

import java.util.List;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractSRLabeler extends AbstractStatisticalComponent<String, SRLState, SRLEval, AbstractFeatureExtractor<?,?,?>>
{

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractStatisticalComponent#getLexicons()
	 */
	@Override
	public Object getLexicons()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractStatisticalComponent#setLexicons(java.lang.Object)
	 */
	@Override
	public void setLexicons(Object lexicons)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractStatisticalComponent#createStringFeatureVector(edu.emory.clir.clearnlp.component.state.AbstractState)
	 */
	@Override
	protected StringFeatureVector createStringFeatureVector(SRLState state)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractStatisticalComponent#getAutoLabel(edu.emory.clir.clearnlp.component.state.AbstractState, edu.emory.clir.clearnlp.classification.vector.StringFeatureVector)
	 */
	@Override
	protected String getAutoLabel(SRLState state, StringFeatureVector vector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractStatisticalComponent#initEval()
	 */
	@Override
	protected void initEval()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractStatisticalComponent#onlineTrain(java.util.List)
	 */
	@Override
	public void onlineTrain(List<DEPTree> trees)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractStatisticalComponent#onlineLexicons(edu.emory.clir.clearnlp.dependency.DEPTree)
	 */
	@Override
	protected void onlineLexicons(DEPTree tree)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.emory.clir.clearnlp.component.AbstractComponent#process(edu.emory.clir.clearnlp.dependency.DEPTree)
	 */
	@Override
	public void process(DEPTree tree)
	{
		
		
	}
	
	DEPNode nextPredicate(SRLState state, int beginID)
	{
		DEPNode node;
		
		for (; beginID < state.getTreeSize(); beginID++)
		{
			node = state.getNode(beginID);
			if (isPredicate(node)) return node;
		}
		
		return null;
	}
	
	protected abstract boolean isPredicate(DEPNode node);
}
