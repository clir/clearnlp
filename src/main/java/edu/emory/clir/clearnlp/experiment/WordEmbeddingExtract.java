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
package edu.emory.clir.clearnlp.experiment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WordEmbeddingExtract
{
	static private Map<String,Set<String>> getBrownClusters(InputStream in) throws IOException
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		Map<String,Set<String>> map = new HashMap<>();
		List<double[]> vectors = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		double[] d, max = null, min = null;
		Set<String> set;
		int i, j, len;
		String line;
		String[] t;
		int a;
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitSpace(line);
			len = t.length - 1;
			d = new double[len];
			
			if (max == null)
			{
				max = new double[len];
				min = new double[len];
			}
			
			for (i=0; i<len; i++)
			{
				d[i] = Double.parseDouble(t[i+1]);
				max[i] = Math.max(d[i], max[i]);
				min[i] = Math.min(d[i], min[i]);
			}
			
			tokens.add(t[0]);
			vectors.add(d);
		}
		
		for (i=tokens.size()-1; i>=0; i--)
		{
			set = new HashSet<>();
			d = vectors.get(i);
			
			for (j=d.length-1; j>=0; j--)
			{
				a = (int)Math.round(5d * (d[j] - min[j]) / (max[j] - min[j]));
				set.add(j+":"+a);
			}

			map.put(tokens.get(i), set);
		}
		
		return map;
	}
	
	static public void main(String[] args)
	{
		String filename = args[0];
		
		try
		{
			Map<String,Set<String>> tree = getBrownClusters(new FileInputStream(filename));
			ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(filename+".xz");
			out.writeObject(tree);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
