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
package edu.emory.clir.clearnlp.classification.instance;

import java.io.IOException;
import java.io.InputStream;

import edu.emory.clir.clearnlp.classification.vector.AbstractFeatureVector;
import edu.emory.clir.clearnlp.reader.AbstractReader;
import edu.emory.clir.clearnlp.util.adapter.Adapter1;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractInstanceReader<I extends AbstractInstance<F>, F extends AbstractFeatureVector> extends AbstractReader<I>
{
	public AbstractInstanceReader(InputStream in)
	{
		super(in);
	}

	abstract protected F createFeatureVector(String[] col);
	abstract protected I getInstance(String label, F vector);
	abstract protected void addFeature(F vector, String[] col);
	
	@Override
	public I next()
	{
		String line = readLine();
		if (line == null) return null;
		
		String[] cols = AbstractFeatureVector.SPLIT_FEATURE.split(line);
		String[] col  = AbstractFeatureVector.SPLIT_WEIGHT .split(cols[1]);
		int i, size = cols.length;
		
		F vector = createFeatureVector(col);
		addFeature(vector, col);
				
		for (i=2; i<size; i++)
			addFeature(vector, AbstractFeatureVector.SPLIT_WEIGHT.split(cols[i]));
		
		return getInstance(cols[0], vector);
	}
	
	private String readLine()
	{
		try
		{
			return b_reader.readLine();
		}
		catch (IOException e) {e.printStackTrace();}
		
		return null;
	}

	public void applyAll(Adapter1<I> adapter)
	{
		I item;
		
		while ((item = next()) != null)
			adapter.apply(item);
		
		close();
	}
}