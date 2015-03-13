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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.collection.stack.IntPStack;
import edu.emory.clir.clearnlp.collection.triple.ObjectObjectDoubleTriple;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.AbstractFeatureToken;
import edu.emory.clir.clearnlp.util.arc.DEPArc;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPStateBranch extends AbstractState<DEPArc,DEPLabel> implements DEPTransition
{
	static public final int IS_ROOT   = 0;
	static public final int IS_DESC   = 1;
	static public final int IS_DESC_NO_HEAD = 2;
	static public final int NO_HEAD   = 3;
	static public final int LEFT_ARC  = 4;
	static public final int RIGHT_ARC = 5;
	
	private IntPStack i_stack;
	private IntPStack i_inter;
	private int       i_input;
	
	private DEPConfiguration t_configuration;
	private int              num_transitions;
	private double           total_score;
	private boolean          save_branch;
	private List<DEPBranch>  l_branches;
	private int              beam_index;
	
	private ObjectObjectDoubleTriple<DEPArc[],List<StringInstance>> best_tree;
	
//	====================================== Initialization ======================================
	
	public DEPStateBranch()
	{
		super();
	}
	
	public DEPStateBranch(DEPTree tree, CFlag flag, DEPConfiguration configuration)
	{
		super(tree, flag);
		init(configuration);
	}
	
	private void init(DEPConfiguration configuration)
	{
		i_stack = new IntPStack(t_size);
		i_inter = new IntPStack();
		i_input = 0;
		shift();
		
		t_configuration = configuration;
		num_transitions = 0;
		total_score = 0;
		save_branch = configuration.useBranching();
		l_branches  = new ArrayList<>();
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
	
//	public void save2ndHead(StringPrediction[] ps)
//	{
//		StringPrediction fst = ps[0];
//		StringPrediction snd = ps[1];
//		
//		if (fst.getScore() - snd.getScore() < 1 && fst.getLabel().startsWith(ARC_NO))
//		{
//			DEPNode  stack = getStack();
//			DEPNode  input = getInput();
//			DEPLabel label = new DEPLabel(snd.getLabel());
//			
//			if (label.isArc(ARC_LEFT)) 
//			{
//				snd_heads[stack.getID()].add(new ObjectDoublePair<DEPArc>(new DEPArc(input, label.getDeprel())));
//			}
//			else if (label.isArc(ARC_RIGHT))
//			
//			
//		}
//	}
	
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
	
//	====================================== BRANCH ======================================

	public boolean startBranching()
	{
		if (l_branches.isEmpty() || c_flag == CFlag.TRAIN) return false;
		
		if (l_branches.size() > t_configuration.getBeamSize()-1)
			l_branches = l_branches.subList(0, t_configuration.getBeamSize()-1);
		
		best_tree   = new ObjectObjectDoubleTriple<>(d_tree.getHeads(), null, getScore());
		save_branch = false;
		beam_index  = 0;
		return true;
	}
	
	public boolean nextBranch()
	{
		if (beam_index < l_branches.size())
		{
			l_branches.get(beam_index++).reset();
			return true;
		}
		
		return false;
	}
	
	public void saveBranch(StringPrediction[] ps, DEPLabel autoLabel)
	{
		if (save_branch)
		{
			StringPrediction fst = ps[0];
			StringPrediction snd = ps[1];
			
			if (fst.getScore() - snd.getScore() < t_configuration.getMarginThreshold())
				addBranch(autoLabel, new DEPLabel(snd));
		}
	}
	
	private void addBranch(DEPLabel fstLabel, DEPLabel sndLabel)
	{
		if (!fstLabel.isArc(sndLabel) || !fstLabel.isList(sndLabel))
			l_branches.add(new DEPBranch(sndLabel));
	}
	
	public void saveBest(List<StringInstance> instances)
	{
		double score = getScore();
		
		if (score > best_tree.d)
			best_tree.set(d_tree.getHeads(), instances, score);
	}
	
	public List<StringInstance> setBest()
	{
		d_tree.setHeads(best_tree.o1);
		return best_tree.o2;
	}
	
	private double getScore()
	{
		return (c_flag == CFlag.BOOTSTRAP) ? (double)d_tree.getScoreCounts(g_oracle, t_configuration.evaluatePunctuation())[1] : total_score / num_transitions;
	}
	
	private class DEPBranch
	{
		private DEPArc[]  heads;
		private IntPStack stack;
		private IntPStack inter;
		private int       input;
		private DEPLabel  label;
		private double    totalScore;
		private int       numTransitions;
		
		public DEPBranch(DEPLabel nextLabel)
		{
			heads = d_tree.getHeads(i_input+1);
			stack = new IntPStack(i_stack);
			inter = new IntPStack(i_inter);
			input = i_input;
			label = nextLabel;
			totalScore = total_score;
			numTransitions = num_transitions;
		}
		
		public void reset()
		{
			d_tree.setHeads(heads);
			i_stack = stack;
			i_inter = inter;
			i_input = input;
			total_score = totalScore;
			num_transitions = numTransitions;
			next(label);
		}
	}
	
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