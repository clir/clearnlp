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

import java.io.InputStream;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.reader.AbstractReader;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class StringInstanceReader extends AbstractInstanceReader<StringInstance,StringFeatureVector>
{
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in));}. */
	public StringInstanceReader(InputStream in)
	{
		super(in);
	}

	@Override
	protected StringFeatureVector createFeatureVector(String[] col)
	{
		return new StringFeatureVector(col.length > 2);
	}
	
	@Override
	protected StringInstance getInstance(String label, StringFeatureVector vector)
	{
		return new StringInstance(label, vector);
	}

	@Override
	protected void addFeature(StringFeatureVector vector, String[] col)
	{
		if (vector.hasWeight())
			vector.addFeature(Integer.parseInt(col[0]), col[1], Double.parseDouble(col[2]));
		else	
			vector.addFeature(Integer.parseInt(col[0]), col[1]);
	}

	@Override
	public AbstractReader<StringInstance> clone()
	{
		return null;
	}
}