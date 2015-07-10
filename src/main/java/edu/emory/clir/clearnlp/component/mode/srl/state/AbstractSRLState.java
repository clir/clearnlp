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

import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.component.mode.srl.SRLConfiguration;
import edu.emory.clir.clearnlp.component.mode.srl.SRLTransition;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;
import edu.emory.clir.clearnlp.feature.type.FieldType;
import edu.emory.clir.clearnlp.lexicon.propbank.PBLib;
import edu.emory.clir.clearnlp.util.arc.SRLArc;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractSRLState extends AbstractState<SRLArc[],String> implements SRLTransition
{
	private SRLConfiguration t_configuration;
	private DEPNode d_predicate;
	
	private List<Pair<DEPNode,DEPNode>> argument_candidates;
	private List<String> numbered_arguments;
	private int argument_index;
	private int argument_count;

//	====================================== INITIALIZATION ======================================
	
	public AbstractSRLState(DEPTree tree, CFlag flag, SRLConfiguration configuration)
	{
		super(tree, flag);
		init(configuration);
	}
	
	private void init(SRLConfiguration configuration)
	{
		t_configuration = configuration;
		d_predicate = getNode(0);
		shift();
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
	
	public int getModelIndex()
	{
		return (d_predicate.getID() > getArgument().getID()) ? 0 : 1;
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
				
		return getModelIndex()+NO_ARC;
	}

	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		int index = -1;
		
		switch (token.getSource())
		{
		case  i: index = d_predicate  .getID() + token.getOffset(); break;
		case  j: index = getArgument().getID() + token.getOffset(); break;
		default: new IllegalArgumentException();
		}
		
		return getNodeRelation(token, getNode(index));
	}

//	====================================== TRANSITION ======================================

	@Override
	public void next(String label)
	{
		if (!label.equals(NO_ARC))
		{
			argument_count++;
			getArgument().addSemanticHead(d_predicate, label);
			if (PBLib.isNumberedArgument(label)) numbered_arguments.add(label);
		}
		
		if (!pass())
		{
			if (argument_count > 0) d_predicate.putFeat(DEPLib.FEAT_PB, d_predicate.getLemma());
			shift();
		}
	}

	@Override
	public boolean isTerminate()
	{
		return d_predicate == null;
	}
	
	private void shift()
	{
		d_predicate = nextPredicate();
		
		if (d_predicate != null)
		{
			argument_candidates = d_predicate.getArgumentCandidateList(t_configuration.getMaxDepth(), t_configuration.getMaxHeight());
			
			if (argument_candidates.isEmpty())
				shift();
			else
			{
				numbered_arguments = new ArrayList<>();
				argument_index = 0;
				argument_count = 0;
			}
		}
	}
	
	private boolean pass()
	{
		return ++argument_index < argument_candidates.size();
	}
	
//	====================================== NODE ======================================

	public DEPNode getPredicate()
	{
		return d_predicate;
	}
	
	public DEPNode getArgument()
	{
		return argument_candidates.get(argument_index).o1;
	}
	
	public DEPNode getLowestCommonAncestor()
	{
		return argument_candidates.get(argument_index).o2;
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
	
//	====================================== FEATURES ======================================
	
	public String distanceBetweenPredicateAndArgument()
	{
		int dist = Math.abs(d_predicate.getID() - getArgument().getID());
		
		if      (dist <=  5)	return "0";
		else if (dist <= 10)	return "1";
		else if (dist <= 15)	return "2";
		else					return "3";
	}
	
	public String getNumberedArgument(int index)
	{
		int idx = numbered_arguments.size() - index - 1;
		return (idx >= 0) ? numbered_arguments.get(idx) : null;
	}
	
	public String getPath(FieldType field)
	{
		return d_predicate.getPath(getArgument(), getLowestCommonAncestor(), field);
	}
}
