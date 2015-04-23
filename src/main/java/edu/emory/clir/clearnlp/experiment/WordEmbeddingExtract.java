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
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.util.CharUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class WordEmbeddingExtract
{
	public Map<String,Set<String>> getWordEmbeddingsNorm(InputStream in, int size, int norm) throws Exception
	{
		List<Pair<String,float[]>> embeddings = readEmbeddings(in, size);
		Pair<float[],float[]> maxMin = getMaxMin(embeddings, size);
		Map<String,Set<String>> map = new HashMap<>();
		int i, j, n, len = embeddings.size();
		float[] max = maxMin.o1;
		float[] min = maxMin.o2;
		Pair<String,float[]> p;
		Set<String> set;
		float[] d;
		
		for (i=0; i<len; i++)
		{
			p = embeddings.get(i);
			set = new HashSet<>();
			map.put(p.o1, set);
			d = p.o2;
			
			for (j=0; j<size; j++)
			{
				n = (int)Math.round((d[j] - min[j]) * norm / (max[j] - min[j]));
				set.add(j+":"+n);
			}
		}
		
		return map;
	}
	
	public Map<String,Set<String>> getWordEmbeddingsStdev(InputStream in, int size, int norm) throws Exception
	{
		List<Pair<String,float[]>> embeddings = readEmbeddings(in, size);
		Pair<double[],double[]> meanStdev = getMeanStdev(embeddings, size);
		Map<String,Set<String>> map = new HashMap<>();
		int i, j, n, len = embeddings.size();
		double[] mean  = meanStdev.o1;
		double[] stdev = meanStdev.o2;
		Pair<String,float[]> p;
		Set<String> set;
		float[] d;
		
		for (i=0; i<len; i++)
		{
			p = embeddings.get(i);
			set = new HashSet<>();
			map.put(p.o1, set);
			d = p.o2;
			
			for (j=0; j<size; j++)
			{
				n = (int)Math.round((d[j] - mean[j]) * norm / stdev[j]);
				set.add(j+":"+n);
			}
		}
		
		return map;
	}
	
	public List<Pair<String,float[]>> readEmbeddings(InputStream in, int size) throws Exception
	{
		List<Pair<String,float[]>> embeddings = new ArrayList<>();
		BufferedReader reader = IOUtils.createBufferedReader(in);
		Pair<String,float[]> p;
		
		while ((p = readEmbedding(reader, size)) != null)
			embeddings.add(p);
		
		return embeddings;
	}

	private Pair<String,float[]> readEmbedding(BufferedReader reader, int size) throws Exception
	{
		float[] vector = new float[size];
		int[] buffer = new int[128];
		String s, word = null;
		int i, b, ch;
		
		for (i=-1; i<size; i++)
		{
			b = 0;
			
			while (true)
			{
				ch = reader.read();
				if (ch == -1) return null;
				if (CharUtils.isWhiteSpace((char)ch)) break;
				else buffer[b++] = ch;
			}
			
			s = new String(buffer, 0, b).trim();
			if (i < 0) word = s;
			else  vector[i] = (float)Double.parseDouble(s);
		}
		
		return new Pair<String,float[]>(word, vector);
     }
	
	private Pair<float[],float[]> getMaxMin(List<Pair<String,float[]>> embeddings, int size)
	{
		float[] max = Arrays.copyOf(embeddings.get(0).o2, size);
		float[] min = Arrays.copyOf(max, size);
		int i, j, len = embeddings.size();
		float[] d;
		
		for (i=1; i<len; i++)
		{
			d = embeddings.get(i).o2;
			
			for (j=0; j<size; j++)
			{
				max[j] = Math.max(max[j], d[j]);
				min[j] = Math.min(min[j], d[j]);
			}
		}
		
		return new Pair<>(max, min);
	}
	
	private Pair<double[],double[]> getMeanStdev(List<Pair<String,float[]>> embeddings, int size)
	{
		int i, j, len = embeddings.size(), den = len * size;
		double[] mean = new double[size];
		float[] d;
		
		for (i=0; i<len; i++)
		{
			d = embeddings.get(i).o2;
			
			for (j=0; j<size; j++)
				mean[j] += d[j];
		}
		
		for (j=0; j<size; j++)
			mean[j] /= den;
		
		double[] stdev = new double[size];
		
		for (i=0; i<len; i++)
		{
			d = embeddings.get(i).o2;
			
			for (j=0; j<size; j++)
				stdev[j] += MathUtils.sq(d[j] - mean[j]);
		}
		
		for (j=0; j<size; j++)
			stdev[j] = Math.sqrt(stdev[j] / den);
		
		return new Pair<>(mean, stdev);
	}
	
	static public void main(String[] args)
	{
		String filename = args[0];
		int size = Integer.parseInt(args[1]);
		int norm = 5;
		
		try
		{
			WordEmbeddingExtract emb = new WordEmbeddingExtract();
			Map<String,Set<String>> tree = emb.getWordEmbeddingsStdev(new FileInputStream(filename), size, norm);
			ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(filename+".xz"+"."+norm);
			out.writeObject(tree);
			out.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
