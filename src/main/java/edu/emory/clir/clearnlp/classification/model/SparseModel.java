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
package edu.emory.clir.clearnlp.classification.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.IntInstance;
import edu.emory.clir.clearnlp.classification.instance.SparseInstance;
import edu.emory.clir.clearnlp.classification.instance.SparseInstanceCollector;
import edu.emory.clir.clearnlp.classification.map.LabelMap;
import edu.emory.clir.clearnlp.classification.vector.AbstractWeightVector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SparseModel extends AbstractModel<SparseInstance,SparseFeatureVector>
{
	/** Initializes this model for training. */
	public SparseModel(boolean binary)
	{
		super(binary);
		init();
	}
	
	public SparseModel(ObjectInputStream in)
	{
		super(in);
	}
	
	private void init()
	{
		i_collector = new SparseInstanceCollector();
	}
	
// =============================== Serialization ===============================
	
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		init();
		w_vector = (AbstractWeightVector)in.readObject();
		m_labels = (LabelMap)in.readObject();
	}
	
	public void save(ObjectOutputStream out) throws IOException
	{
		out.writeObject(w_vector);
		out.writeObject(m_labels);
	}
	
// =============================== Training ===============================
	
	public void addInstance(SparseInstance instance)
	{
		i_collector.addInstance(instance);
	}

	/** Initializes this model with the collected list of training instances. */
	public List<IntInstance> initializeForTraining()
	{
		int labelSize   = m_labels.expand(i_collector.getLabelMap(), 0);
		int featureSize = i_collector.getFeatureSize();
		
		w_vector.expand(labelSize, featureSize);
		
		List<IntInstance> instances = toIntInstanceList(i_collector.getInstanceList());
		i_collector.init();
		
		return instances;
	}
	
// =============================== Conversion ===============================
	
	@Override
	public IntInstance toIntInstance(SparseInstance instance)
	{
		int label = m_labels.getLabelIndex(instance.getLabel());
		return new IntInstance(label, instance.getFeatureVector());
	}
	
// =============================== Predictions ===============================
	
	@Override
	public double[] getScores(SparseFeatureVector x)
	{
		return w_vector.getScores(x);
	}
}