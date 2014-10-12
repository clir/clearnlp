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
import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.instance.StringInstanceCollector;
import edu.emory.clir.clearnlp.classification.map.FeatureMap;
import edu.emory.clir.clearnlp.classification.map.LabelMap;
import edu.emory.clir.clearnlp.classification.vector.AbstractWeightVector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringModel extends AbstractModel<StringInstance,StringFeatureVector>
{
	private static final long serialVersionUID = -5836424308513378097L;
	protected StringInstanceCollector i_collector;
	protected FeatureMap m_features;

	/** Initializes this model for training. */
	public StringModel(boolean binary)
	{
		super(binary);
		init();
		m_features = new FeatureMap();
	}
	
	public StringModel(ObjectInputStream in)
	{
		super(in);
	}
	
	private void init()
	{
		i_collector = new StringInstanceCollector();
	}
	
	/** Reinitializes the label map, the feature map, and the weight vector of this model. */
	public void reset()
	{
		m_labels.reset();
		m_features.reset();
		w_vector.reset();
	}
	
// =============================== Serialization ===============================
	
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		init();
		w_vector   = (AbstractWeightVector)in.readObject();
		m_labels   = (LabelMap)in.readObject();
		m_features = (FeatureMap)in.readObject();
	}
	
	public void save(ObjectOutputStream out) throws IOException
	{
		out.writeObject(w_vector);
		out.writeObject(m_labels);
		out.writeObject(m_features);
	}
	
// =============================== Training ===============================
	
	@Override
	public void addInstance(StringInstance instance)
	{
		i_collector.addInstance(instance);
	}

	/** Initializes this model with the collected list of training instances. */
	public List<IntInstance> initializeForTraining(int labelCutoff, int featureCutoff)
	{
		int labelSize   = m_labels  .expand(i_collector.getLabelMap()  , labelCutoff);
		int featureSize = m_features.expand(i_collector.getFeatureMap(), featureCutoff);
		w_vector.expand(labelSize, featureSize);
		
		List<IntInstance> instances = toIntInstanceList(i_collector.getInstanceList());
		i_collector.init();
		
		return instances;
	}

// =============================== Conversion ===============================

	@Override
	public IntInstance toIntInstance(StringInstance instance)
	{
		int label = m_labels.getLabelIndex(instance.getLabel());
		if (label < 0) return null;
		
		SparseFeatureVector vector = toSparseFeatureVector(instance.getFeatureVector());
		if (vector.isEmpty()) return null;
		
		return new IntInstance(label, vector);
	}
	
	public SparseFeatureVector toSparseFeatureVector(StringFeatureVector vector)
	{
		SparseFeatureVector x = new SparseFeatureVector(vector.hasWeight());
		int i, index, size = vector.size();
		
		for (i=0; i<size; i++)
		{
			index = getFeatureIndex(vector, i);
			
			if (0 < index)
			{
				if (vector.hasWeight())
					x.addFeature(index, vector.getWeight(i));
				else
					x.addFeature(index);
			}
		}
		
		x.trimToSize();
		return x;
	}
	
	public int getFeatureIndex(StringFeatureVector x, int i)
	{
		return m_features.getFeatureIndex(x.getType(i), x.getValue(i));
	}
	
// =============================== Predictions ===============================

	@Override
	public double[] getScores(StringFeatureVector x)
	{
		return w_vector.getScores(toSparseFeatureVector(x));
	}
}