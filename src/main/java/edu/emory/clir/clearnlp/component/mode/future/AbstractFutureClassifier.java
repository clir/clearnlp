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
package edu.emory.clir.clearnlp.component.mode.future;

import java.io.ObjectInputStream;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.component.AbstractStatisticalComponent;
import edu.emory.clir.clearnlp.dependency.DEPLib;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.feature.common.CommonFeatureExtractor;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractFutureClassifier extends AbstractStatisticalComponent<String, FCState, FCEval, CommonFeatureExtractor<FCState>>
{
	/** Creates a pos tagger for train. */
	public AbstractFutureClassifier(Object lexicons)
	{
		super(null, null, lexicons, false, 1);
	}
	
	/** Creates a pos tagger for bootstrap or evaluate. */
	public AbstractFutureClassifier(Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(null, null, lexicons, models, bootstrap);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractFutureClassifier(ObjectInputStream in)
	{
		super(null, in);
	}
	
	/** Creates a pos tagger for decode. */
	public AbstractFutureClassifier(byte[] models)
	{
		super(null, models);
	}
	
//	====================================== LEXICONS ======================================
	
	@Override
	public Object getLexicons() {return null;}
	
	@Override
	public void setLexicons(Object lexicons) {}
	
//	====================================== EVAL ======================================

	@Override
	protected void initEval()
	{
		c_eval = new FCEval(DEPLib.FEAT_FUTURE);
	}
	
//	====================================== PROCESS ======================================
	
	@Override
	public void process(DEPTree tree)
	{
//		DEPNode node = tree.get(FCEval.INFO_NODE);
//		String f = node.getFeat(DEPLib.FEAT_FUTURE);
//		if (f.equals("0") || f.equals("1")) node.putFeat(DEPLib.FEAT_FUTURE, "0");

		FCState state = new FCState(tree, c_flag);
		List<StringInstance> instances = process(state);
		
		if (isTrainOrBootstrap())
			s_models[0].addInstances(instances);
		else if (isEvaluate())
			c_eval.countCorrect(tree, state.getOracle()); 
	}

	@Override
	protected StringFeatureVector createStringFeatureVector(FCState state)
	{
		StringFeatureVector vector = new StringFeatureVector();
		String delim = StringConst.UNDERSCORE;
		DEPTree tree = state.getTree();
		
		int type = 0;
		for (String s : tree.getNgrams(DEPNode::getSimplifiedWordForm, delim, 2)) vector.addFeature(type, s);

		type++;
		for (String s : tree.getNgrams(DEPNode::getSimplifiedWordForm, DEPNode::getPOSTag, delim, 2)) vector.addFeature(type, s);
		
		type++;
		DEPNode node = tree.get(FCEval.INFO_NODE);
		String feat, s;
		
		for (int i=3; i<100; i++)
		{
			s = Integer.toString(i);
			feat = node.getFeat(s);
			if (feat != null) vector.addFeature(type, s);
		}
		
		return vector;
	}
	
//	private Set<String> getRuleFeatures(DEPTree tree, StringFeatureVector vector)
//	{
//		Set<String> set = new HashSet<>();
//		
//		for (DEPNode node : tree)
//		{
//			if (POSLibEn.isVerb(node.getPOSTag()))
//			{
//				switch (node.getLowerSimplifiedWordForm())
//				{
//				case "going"   : if (followedByTO(node)) set.add("going_to"); break;
//				case "supposed": if (followedByTO(node)) set.add("supposed_to"); break;
//				}
//				
//				switch (node.getLemma())
//				{
//				case "have": if (followedByTO(node)) set.add("have_to"); break;
//				case "want": set.add("want"); break;
//				case "hope": set.add("hope"); break;
//				}
//			}
//			else if (POSLibEn.isAdjective(node.getPOSTag()))
//			{
//				switch (node.getLemma())
//				{
//				case "able"  : if (followedByTO(node)) set.add("able_to"); break;
//				case "unable": if (followedByTO(node)) set.add("unable_to"); break;
//				}
//			}
//			else
//			{
//				switch (node.getLemma())
//				{
//				case "need": set.add("need"); break;
//				case "plan": set.add("plan"); break;
//				}
//			}
//		}
//		
//		return set;
//	}
//	
//	private boolean followedByTO(DEPNode verb)
//	{
//		DEPNode xcomp = verb.getFirstDependentByLabel(DEPLibEn.DEP_XCOMP);
//		return xcomp != null && xcomp.containsDependentPOS(POSTagEn.POS_TO);
//	}
	
	@Override
	protected String getAutoLabel(FCState state, StringFeatureVector vector)
	{
		StringPrediction p = s_models[0].predictBest(vector);
		return p.getLabel();
	}
	
//	====================================== ONLINE TRAIN ======================================
	
	@Override
	public void onlineTrain(List<DEPTree> trees)
	{
		onlineTrainSingleAdaGrad(trees);
	}
	
	@Override
	protected void onlineLexicons(DEPTree tree)
	{
			
	}
}