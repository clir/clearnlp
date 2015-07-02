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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import edu.emory.clir.clearnlp.component.mode.srl.SRLTransition;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;
import edu.emory.clir.clearnlp.util.arc.SRLArc;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractSRLState extends AbstractState<SRLArc[],String> implements SRLTransition
{
	private DEPNode d_predicate;
	
	private Deque<DEPNode> argument_candidates;
	private int argument_count;

//	====================================== INITIALIZATION ======================================
	
	public AbstractSRLState(DEPTree tree, CFlag flag)
	{
		super(tree, flag);
		init();
	}
	
	private void init()
	{
		d_predicate = getNode(0);
		initNextPredicate();
	}
	
	private void initNextPredicate()
	{
		d_predicate = nextPredicate();
		
		if (d_predicate != null)
		{
			argument_candidates = getArgumentCandidates();
			argument_count = 0;
		}
	}
	
//	====================================== ORACLE ======================================

	@Override
	protected void initOracle()
	{
		g_oracle = d_tree.getSemanticHeads();
		d_tree.clearSemanticHeads();
	}

	@Override
	public void resetOracle()
	{
		d_tree.setSemanticHeads(g_oracle);
	}

//	====================================== LABEL ======================================

	@Override
	public String getGoldLabel()
	{
		for (SRLArc arc : g_oracle[getArgument().getID()])
		{
			if (arc.isNode(d_predicate))
				return getModelIndex()+arc.getLabel();
		}
				
		return NO_ARC;
	}

	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		int index = -1;
		
		switch (token.getSource())
		{
		case  i: index = getArgument().getID() + token.getOffset(); break;
		case  j: index = d_predicate  .getID() + token.getOffset(); break;
		default: new IllegalArgumentException();
		}
		
		return getNodeRelation(token, getNode(index));
	}

//	====================================== TRANSITION ======================================

	@Override
	public void next(String label)
	{
		if (label.equals(NO_ARC))
		{
			DEPNode arg = nextArgument();
			
			if (arg == null)
			{
				if (argument_count > 0) d_predicate.putFeat(DEPLib.FEAT_PB, d_predicate.getLemma());
				initNextPredicate();
			}
		}
		else
		{
			DEPNode arg = getArgument();
			arg.addSemanticHead(d_predicate, label.substring(1));
			argument_count++;
			nextArgument();
		}
	}

	@Override
	public boolean isTerminate()
	{
		return d_predicate == null;
	}
	
//	====================================== NODE ======================================

	public DEPNode getPredicate()
	{
		return d_predicate;
	}
	
	public DEPNode getArgument()
	{
		return argument_candidates.getFirst();
	}
	
	public int getModelIndex()
	{
		return (d_predicate.getID() > getArgument().getID()) ? 0 : 1;
	}
	
//	====================================== PREDICATE ======================================

	private DEPNode nextPredicate()
	{
		int i, size = getTreeSize();
		DEPNode node;
		
		for (i=d_predicate.getID()+1; i<size; i++)
		{
			node = getNode(i);
			
			if (c_flag == CFlag.DECODE)
			{
				if (isPredicate(node))
					return node;
			}
			else if (node.getFeat(DEPLib.FEAT_PB) != null)
				return node;
		}
		
		return null;
	}
	
	protected abstract boolean isPredicate(DEPNode node);
	
//	====================================== ARGUMENT ======================================
	
	private DEPNode nextArgument()
	{
		return argument_candidates.isEmpty() ? null : argument_candidates.poll();
	}
	
	private Deque<DEPNode> getArgumentCandidates()
	{
		List<DEPNode> list = d_predicate.getSubNodeList();
		getArgumentCandidatesAncestors(list, d_predicate.getHead());
		Collections.sort(list);
		
		Deque<DEPNode> deque = new ArrayDeque<DEPNode>();
		int idx = Collections.binarySearch(list, d_predicate), i, size = list.size();
		for (i=idx-1; i>=0; i--) deque.add(list.get(i));
		idx = Collections.binarySearch(list, d_predicate, Collections.reverseOrder());
		for (i=idx+1; i<size; i++) deque.add(list.get(i));
		
		return deque;
	}
	
	private void getArgumentCandidatesAncestors(List<DEPNode> list, DEPNode node)
	{
		if (node == null) return;
		list.add(node);
		list.addAll(node.getDependentList());
		getArgumentCandidatesAncestors(list, node.getHead());
	}
}
