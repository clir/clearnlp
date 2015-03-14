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
package edu.emory.clir.clearnlp.component.mode.dep;

import java.util.Arrays;
import java.util.PriorityQueue;

import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.collection.stack.IntPStack;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.arc.DEPArc;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPState extends AbstractState<DEPArc,DEPLabel> implements DEPTransition
{
	static public final int IS_ROOT			= 0;
	static public final int IS_DESC			= 1;
	static public final int IS_DESC_NO_HEAD	= 2;
	static public final int NO_HEAD			= 3;
	static public final int LEFT_ARC		= 4;
	static public final int RIGHT_ARC		= 5;
	
	private IntPStack i_stack;
	private IntPStack i_inter;
	private int       i_input;
	
	private PriorityQueue<ObjectDoublePair<DEPArc>>[] snd_heads;
	private DEPConfiguration t_configuration;
	private int              num_transitions;
	private double           total_score;
	
//	====================================== Initialization ======================================
	
	public DEPState()
	{
		super();
	}
	
	public DEPState(DEPTree tree, CFlag flag, DEPConfiguration configuration)
	{
		super(tree, flag);
		init(configuration);
	}
	
	@SuppressWarnings("unchecked")
	private void init(DEPConfiguration configuration)
	{
		i_stack = new IntPStack(t_size);
		i_inter = new IntPStack();
		i_input = 0;
		shift();
		
		snd_heads = (PriorityQueue<ObjectDoublePair<DEPArc>>[])DSUtils.createEmptyPriorityQueueArray(t_size, false);
		t_configuration = configuration;
		num_transitions = 0;
		total_score = 0;
	}

//	====================================== LABEL ======================================

	@Override
	protected void initOracle()
	{
		g_oracle = d_tree.getHeads();
 		d_tree.clearDependencies();
	}
	
	@Override
	public void resetOracle()
	{
		d_tree.setHeads(g_oracle);
	}
	
	@Override
	public DEPLabel getGoldLabel()
	{
		int     stack = getStackID();
		DEPNode input = getInput();
		DEPArc  oracle;
		String  list;
		
		oracle = getOracle(stack);
		
		if (oracle.getNode() == input)
		{
			list = isGoldReduce(true) ? LIST_REDUCE : LIST_PASS;
			return new DEPLabel(ARC_LEFT, list, oracle.getLabel());
		}
					
		oracle = getOracle(i_input);
		
		if (oracle.getNode() == getNode(stack))
		{
			list = isGoldShift() ? LIST_SHIFT : LIST_PASS;
			return new DEPLabel(ARC_RIGHT, list, oracle.getLabel());
		}
		
		if      (isGoldShift())			list = LIST_SHIFT;
		else if (isGoldReduce(false))	list = LIST_REDUCE;
		else							list = LIST_PASS;
		
		return new DEPLabel(ARC_NO, list, StringConst.EMPTY);
	}
	
	/** Called by {@link #getGoldLabel()}. */
	private boolean isGoldShift()
	{
		// if head(input) < stack
		int stack = getStackID();
		
		if (getOracle(i_input).getNode().getID() < stack)
			return false;
		
		// if child(input) < stack
		DEPNode input = getInput();
		int i = 1;

		while ((stack = i_stack.peek(i++)) >= 0)
		{
			if (getOracle(stack).getNode() == input)
				return false;
		}
		
		return true;
	}
	
	/** Called by {@link #getGoldLabel()}. */
	private boolean isGoldReduce(boolean hasHead)
	{
		// if stack has no head
		DEPNode stack = getStack();
		
		if (!hasHead && !stack.hasHead())
			return false;
		
		// if child(input) > stack 
		for (int i=i_input+1; i<t_size; i++)
		{
			if (getOracle(i).getNode() == stack)
				return false;
		}
		
		return true;
	}
	
	public int[][] initLabelIndices(String[] labels)
	{
		int i, size = labels.length;
		DEPLabel label;
		
		IntArrayList isRoot       = new IntArrayList();
		IntArrayList isDesc       = new IntArrayList();
		IntArrayList isDescNoHead = new IntArrayList();
		IntArrayList noHead       = new IntArrayList();
		IntArrayList leftArc      = new IntArrayList();
		IntArrayList rightArc     = new IntArrayList();
		
		for (i=0; i<size; i++)
		{
			label = new DEPLabel(labels[i]);
			
			if (label.isList(LIST_SHIFT))
				isRoot.add(i);
			
			if (label.isArc(ARC_NO))
			{
				isDesc.add(i);
				if (!label.isList(LIST_REDUCE)) isDescNoHead.add(i);
			}
			else if (label.isArc(ARC_LEFT))
				leftArc.add(i);
			else if (label.isArc(ARC_RIGHT))
				rightArc.add(i);
			
			if (!(label.isArc(ARC_NO) && label.isList(LIST_REDUCE)))
				noHead.add(i);
		}
		
		int[][] indices = new int[6][];
		
		initLabelIndices(indices, isRoot      , IS_ROOT);
		initLabelIndices(indices, isDesc      , IS_DESC);
		initLabelIndices(indices, isDescNoHead, IS_DESC_NO_HEAD);
		initLabelIndices(indices, noHead      , NO_HEAD);
		initLabelIndices(indices, leftArc     , LEFT_ARC);
		initLabelIndices(indices, rightArc    , RIGHT_ARC);
		
		return indices;
	}
	
	private void initLabelIndices(int[][] indices, IntArrayList list, int index)
	{
		indices[index] = list.toArray();
		Arrays.sort(indices[index]);
	}
	
	public int[] getLabelIndices(int[][] indices)
	{
		DEPNode stack = getStack();
		DEPNode input = getInput();
		
		if (stack.getID() == DEPLib.ROOT_ID)
			return indices[IS_ROOT];
		else if (stack.isDescendantOf(input))
			return indices[IS_DESC];
		else if (input.isDescendantOf(stack))
			return stack.hasHead() ? indices[IS_DESC] : indices[IS_DESC_NO_HEAD];
		else if (!stack.hasHead())
			return indices[NO_HEAD];
		else
			return null;
	}
	
//	====================================== NODE ======================================
	
	@Override
	public DEPNode getNode(AbstractFeatureToken token)
	{
		DEPNode node = null;
		int index;
		
		switch (token.getSource())
		{
		case i:
			index = getStackID() + token.getOffset();
			if (index < i_input) node = getNode(index); break;
		case j:
			index = i_input + token.getOffset();
			if (index > getStackID()) node = getNode(index); break;
		case k:
			index = (token.getOffset() <= 0) ? i_stack.peek(-token.getOffset()) : i_inter.peek(token.getOffset()-1);
			node = getNode(index); break;
		}
		
		return getNodeRelation(token, node);
	}
	
	public int getStackID()
	{
		return i_stack.peek();
	}
	
	public int getInputID()
	{
		return i_input;
	}
	
	public DEPNode getStack()
	{
		return getNode(getStackID());
	}
	
	public DEPNode getInput()
	{
		return getNode(i_input);
	}
	
	public void reset(int stackID, int inputID)
	{
		i_stack.clear();
		i_inter.clear();
		i_stack.push(stackID);
		i_input = inputID;
	}
	
//	====================================== TRANSITION ======================================
	
	@Override
	public void next(DEPLabel label)
	{
		DEPNode stack = getStack();
		DEPNode input = getInput();
		
		total_score += label.getScore();
		num_transitions++;
		
		if (label.isArc(ARC_LEFT))
		{
			stack.setHead(input, label.getDeprel());
			if (label.isList(LIST_REDUCE)) reduce();
			else pass();
		}
		else if (label.isArc(ARC_RIGHT))
		{
			input.setHead(stack, label.getDeprel());
			if (label.isList(LIST_SHIFT)) shift();
			else pass();
		}
		else
		{
			if (label.isList(LIST_SHIFT)) shift();
			else if (label.isList(LIST_REDUCE)) reduce();
			else pass();
		}
	}
	
	private void shift()
	{
		if (!i_inter.isEmpty())
		{
			for (int i=i_inter.size()-1; i>=0; i--)
				i_stack.push(i_inter.get(i));
			
			i_inter.clear();
		}
		
		i_stack.push(i_input++);
	}
	
	private void reduce()
	{
		i_stack.pop();
	}
	
	private void pass()
	{
		i_inter.push(i_stack.pop());
	}
	
	@Override
	public boolean isTerminate()
	{
		return i_input >= t_size;
	}
	
	public double getScore()
	{
		return (c_flag == CFlag.BOOTSTRAP) ? (double)d_tree.getScoreCounts(g_oracle, t_configuration.evaluatePunctuation())[1] : total_score / num_transitions;
	}
	
//	====================================== 2nd Heads ======================================

	/** PRE: ps[0].isArc("NO"). */
	public void save2ndHead(StringPrediction[] ps)
	{
		if (ps[0].getScore() - ps[1].getScore() < 1)
		{
			DEPLabel label = new DEPLabel(ps[1].getLabel());
			if (label.isArc(ARC_NO)) return;
			DEPNode curr, head;
			
			if (label.isArc(ARC_LEFT))
			{
				curr = getStack();
				head = getInput();
			}
			else
			{
				head = getStack();
				curr = getInput();
			}
			
			snd_heads[curr.getID()].add(new ObjectDoublePair<DEPArc>(new DEPArc(head, label.getDeprel()), ps[1].getScore()));
		}
	}
	
	/** @param node has no head. */
	public boolean find2ndHead(DEPNode node)
	{
		DEPArc head;
		
		for (ObjectDoublePair<DEPArc> p : snd_heads[node.getID()])
		{
			head = p.o;
			
			if (!head.getNode().isDescendantOf(node))
			{
				node.setHead(head.getNode(), head.getLabel());
				return true;
			}
		}
		
		return false;
	}
	
//	====================================== FEATURES ======================================

	@Override
	public boolean extractWordFormFeature(DEPNode node)
	{
		return true;
	}
	
	public int distanceBetweenStackAndInput()
	{
		int sID = getStackID();
		if (sID == DEPLib.ROOT_ID) return -1;
		
		int d = i_input - sID; 
		return (d > 6) ? 6 : d;
	}
}