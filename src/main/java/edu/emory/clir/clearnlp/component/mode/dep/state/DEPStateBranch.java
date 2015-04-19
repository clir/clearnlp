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

import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.collection.stack.IntPStack;
import edu.emory.clir.clearnlp.collection.triple.ObjectObjectDoubleTriple;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.mode.dep.DEPLabel;
import edu.emory.clir.clearnlp.component.mode.dep.DEPTransition;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.arc.DEPArc;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPStateBranch extends AbstractDEPState implements DEPTransition
{
	private ObjectObjectDoubleTriple<DEPArc[],List<StringInstance>> best_tree;
	private PriorityQueue<DEPBranch>  q_branches;
	private boolean save_branch;
	private int beam_size;
	private int max_heads;
	
//	====================================== Initialization ======================================
	
	public DEPStateBranch() {super();}
	
	public DEPStateBranch(DEPTree tree, CFlag flag, DEPConfiguration configuration)
	{
		super(tree, flag, configuration);
		init(configuration);
	}
	
	private void init(DEPConfiguration configuration)
	{
		save_branch = t_configuration.getBeamSize() > 1;
		if (save_branch) q_branches = new PriorityQueue<>(Collections.reverseOrder());
	}
	
//	====================================== BRANCH ======================================

	public boolean startBranching()
	{
		if (q_branches == null || q_branches.isEmpty()) return false;
		best_tree   = new ObjectObjectDoubleTriple<>(d_tree.getHeads(), null, getScore());
		beam_size   = Math.min(beam_size - 1, q_branches.size());
		max_heads   = d_tree.countHeaded();
		save_branch = false;
		return true;
	}
	
	public boolean nextBranch()
	{
		if (0 < beam_size--)
		{
			q_branches.poll().reset();
			return true;
		}
		
		return false;
	}
	
	public void saveBest(List<StringInstance> instances)
	{
		int heads = d_tree.countHeaded();
		double score = getScore();
		
		if (heads >= max_heads && score > best_tree.d)
		{
			best_tree.set(d_tree.getHeads(), instances, score);
			max_heads = heads;
		}
	}
	
	public List<StringInstance> setBest()
	{
		d_tree.setHeads(best_tree.o1);
		return best_tree.o2;
	}
	
	private double getScore()
	{
		return (c_flag == CFlag.BOOTSTRAP) ? (double)d_tree.getScoreCounts(g_oracle, t_configuration.evaluatePunctuation())[1]
										   : total_score / num_transitions; 
	}
	
	public void saveBranch(StringPrediction[] ps)
	{
		if (save_branch)
		{
			StringPrediction fst = ps[0];
			StringPrediction snd = ps[1];
			
			if (fst.getScore() - snd.getScore() < 1)
				addBranch(new DEPLabel(fst), new DEPLabel(snd));
		}
	}
	
	private void addBranch(DEPLabel fstLabel, DEPLabel sndLabel)
	{
		if (!fstLabel.isArc(sndLabel) || !fstLabel.isList(sndLabel))
			q_branches.add(new DEPBranch(fstLabel, sndLabel));
	}
	
	private class DEPBranch implements Comparable<DEPBranch>
	{
		private DEPArc[]  heads;
		private IntPStack stack;
		private IntPStack inter;
		private int       input;
		private DEPLabel  fstLabel;
		private DEPLabel  sndLabel;
		private double    totalScore;
		private int       numTransitions;
		
		public DEPBranch(DEPLabel fstLabel, DEPLabel sndLabel)
		{
			heads = d_tree.getHeads(i_input+1);
			stack = new IntPStack(i_stack);
			inter = new IntPStack(i_inter);
			input = i_input;
			this.fstLabel = fstLabel;
			this.sndLabel = sndLabel;
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
			next(sndLabel);
		}

		@Override
		public int compareTo(DEPBranch o)
		{
			return sndLabel.compareTo(o.sndLabel);
		}
	}
}