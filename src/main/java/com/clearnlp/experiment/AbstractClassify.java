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
package com.clearnlp.experiment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kohsuke.args4j.Option;

import com.clearnlp.classification.configuration.AbstractTrainConfiguration;
import com.clearnlp.classification.instance.AbstractInstance;
import com.clearnlp.classification.instance.AbstractInstanceReader;
import com.clearnlp.classification.instance.SparseInstanceReader;
import com.clearnlp.classification.instance.StringInstanceReader;
import com.clearnlp.classification.model.AbstractModel;
import com.clearnlp.classification.model.SparseModel;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.prediction.StringPrediction;
import com.clearnlp.classification.train.AbstractTrainer;
import com.clearnlp.classification.vector.AbstractFeatureVector;
import com.clearnlp.util.BinUtils;
import com.clearnlp.util.IOUtils;
import com.clearnlp.util.MathUtils;
import com.clearnlp.util.adapter.Adapter1;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractClassify
{
	static public final byte TYPE_SPARSE = 0;
	static public final byte TYPE_STRING = 1;
	
	@Option(name="-trainFile", usage="the training file (optional)", required=false, metaVar="<filename>")
	protected String s_trainFile;
	@Option(name="-modelFile", usage="the model file (optional)", required=false, metaVar="<filename>")
	protected String s_modelFile;
	@Option(name="-testFile", usage="the test filename (optional)", required=false, metaVar="<filename>")
	protected String s_testFile = null;
	@Option(name="-lcutoff", usage="label frequency cutoff (default: 0)\n"+"exclusive, string vector space only", required=false, metaVar="<integer>")
	protected int i_labelCutoff = 0; 
	@Option(name="-fcutoff", usage="feature frequency cutoff (default: 0)\n"+"exclusive, string vector space only", required=false, metaVar="<integer>")
	protected int i_featureCutoff = 0;
	@Option(name="-threads", usage="the number of threads to be used (default: 1)", required=false, metaVar="<integer>")
	protected int i_numberOfThreads = 1;
	@Option(name="-binary", usage="if set, train a binary model (default: false)", required=false, metaVar="<boolean>")
	protected boolean b_binary = false;
	@Option(name="-type", usage="the type of vector space (default: "+AbstractClassify.TYPE_STRING+")\n"+
			AbstractClassify.TYPE_SPARSE+": sparse vector space\n"+
			AbstractClassify.TYPE_STRING+": string vector space\n",
			required=false, metaVar="<byte>")
	protected byte i_vectorType = AbstractClassify.TYPE_STRING;
	
	/** Called by {@link #AbstractClassify(String[])}. */
	abstract protected AbstractTrainConfiguration createTrainConfiguration();
	/** Called by {@link #train(AbstractTrainConfiguration, String)}. */
	abstract protected AbstractTrainer getTrainer(AbstractTrainConfiguration trainConfiguration, AbstractModel<?,?> model);
	
	@SuppressWarnings("unchecked")
	/** Called by {@link #trainModel(AbstractTrainConfiguration, String)}. */
	public <I extends AbstractInstance<F>, F extends AbstractFeatureVector>AbstractModel<I,F> createModel(byte vectorType, boolean binary)
	{
		return (AbstractModel<I,F>)(vectorType == AbstractClassify.TYPE_SPARSE ? new SparseModel(binary) : new StringModel(binary));
	}
	
	public AbstractModel<?,?> loadModel(String modelFile, byte vectorType)
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(modelFile)));
			AbstractModel<?,?> model = vectorType == AbstractClassify.TYPE_SPARSE ? new SparseModel(in) : new StringModel(in);
			in.close();
			return model;
		}
		catch (Exception e) {e.printStackTrace();}
		
		return null;
	}
	
	public void saveModel(AbstractModel<?,?> model, String modelFile)
	{
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(modelFile)));
			model.save(out);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public <I extends AbstractInstance<F>, F extends AbstractFeatureVector>double evaluate(AbstractModel<I,F> model, String testFile)
	{
		BinUtils.LOG.info("Evaluating: "+testFile+"\n");
		
		EvaluateAdapter<I,F> adapter = new EvaluateAdapter<>(model);
		process(adapter, testFile, isSparseModel(model));
		
		double acc = adapter.getAccuracy();
		BinUtils.LOG.info(String.format("- Accuracy: %7.4f (%d/%d)\n", acc, adapter.getCorrect(), adapter.getTotal()));
		return acc;
	}
	
	/** Called by {@link #train(AbstractTrainConfiguration, String)}. */
	protected <I extends AbstractInstance<F>, F extends AbstractFeatureVector>void readInstances(AbstractModel<I,F> model, String trainFile)
	{
		BinUtils.LOG.info("Reading: "+trainFile+"\n");
		
		InstanceAdapter<I,F> adapter = new InstanceAdapter<>(model);
		process(adapter, trainFile, isSparseModel(model));
		
		BinUtils.LOG.info("- "+adapter.getTotal()+" instances\n");
	}
	
	protected <I extends AbstractInstance<F>, F extends AbstractFeatureVector>void process(AbstractAdapter<I,F> adapter, String filename, boolean sparse)
	{
		AbstractInstanceReader<I,F> reader = getInstanceReader(filename, sparse);
		reader.applyAll(adapter);
		reader.close();
	}
	
	@SuppressWarnings({ "unchecked" })
	/** Called by {@link #process(AbstractAdapter, String, boolean)}. */
	private <I extends AbstractInstance<F>, F extends AbstractFeatureVector>AbstractInstanceReader<I,F> getInstanceReader(String filename, boolean sparse)
	{
		InputStream in = IOUtils.createFileInputStream(filename);
		return (AbstractInstanceReader<I,F>)(sparse ? new SparseInstanceReader(in) : new StringInstanceReader(in));
	}
	
	protected boolean isSparseModel(AbstractModel<?,?> model)
	{
		return model instanceof SparseModel;
	}
	
	protected class ArgsReader extends AbstractArgsReader
	{
		public ArgsReader(String[] args, Object obj)
		{
			super(args, obj);
		}

		@Override
		protected String getErrorMessage()
		{
			if (s_trainFile == null && s_modelFile == null)
				return "Either a \"training filename\" or a \"model filename\" must be specified.";
			
			return null;
		}
	}
	
	abstract private class AbstractAdapter<I extends AbstractInstance<F>, F extends AbstractFeatureVector> implements Adapter1<I>
	{
		protected AbstractModel<I,F> a_model;
		protected int n_total; 
		
		public AbstractAdapter(AbstractModel<I,F> model)
		{
			a_model = model;
			n_total = 0;
		}
		
		public int getTotal()
		{
			return n_total;
		}
	}
	
	private class InstanceAdapter<I extends AbstractInstance<F>, F extends AbstractFeatureVector> extends AbstractAdapter<I,F>
	{
		public InstanceAdapter(AbstractModel<I,F> model)
		{
			super(model);
		}
		
		@Override
		public void apply(I instance)
		{
			a_model.addInstance(instance);
			n_total++;
		}
	}
	
	private class EvaluateAdapter<I extends AbstractInstance<F>, F extends AbstractFeatureVector> extends AbstractAdapter<I,F>
	{
		private int n_correct; 
		
		public EvaluateAdapter(AbstractModel<I,F> model)
		{
			super(model);
			n_correct = 0;
		}
		
		@Override
		public void apply(I instance)
		{
			StringPrediction p = a_model.predictBest(instance.getFeatureVector());
			
			if (instance.isLabel(p.getLabel()))
				n_correct++;
			
			n_total++;
		}
		
		public int getCorrect()
		{
			return n_correct;
		}
		
		public double getAccuracy()
		{
			return MathUtils.accuracy(n_correct, n_total);
		}
	}
}