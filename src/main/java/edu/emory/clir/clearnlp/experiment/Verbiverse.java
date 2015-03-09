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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Verbiverse
{
	private ObjectIntHashMap<String> m_indices;
	private double[][] d_similarities;
	private List<Triad> l_triads;
	
	public void initTriadList(InputStream in) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		Set<String> set = new HashSet<>(); 
		String line, key;
		Triad triad;
		String[] t;
		
		l_triads = new ArrayList<>();
		reader.readLine();	// skip the title row
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitCommas(line, true);
			triad = new Triad(t);
			key = triad.getKey();
			
			if (!triad.isRedundant() && !set.contains(key))
			{
				set.add(key);
				l_triads.add(triad);
			}
		}
		
//		System.out.println("# of triads: "+l_triads.size());
	}
	
	public void initSimilarityMatrix(InputStream in) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		String[] t;
		String line = reader.readLine();
		
		m_indices = getIndexMap(Splitter.splitCommas(line, true));
		int i, j, size = m_indices.size();
		double d;
		
		d_similarities = new double[size][size];
		
		for (i=0; i<size; i++)
		{
			t = Splitter.splitCommas(reader.readLine(), true);
			
			for (j=0; j<=i; j++)
			{
				d = Double.parseDouble(t[j]);
				d_similarities[i][j] = d;
				d_similarities[j][i] = d;
			}
		}
		
//		System.out.println("# of verbs : "+size);
	}
	
	public void evaluate(String filename)
	{
		int correct = 0, total = l_triads.size();
		
		for (Triad triad : l_triads)
		{
			if (evaluateAux(triad))
				correct++;
		}
		
		System.out.printf("%s\t%5.2f\n", filename, 100d*correct/total);
	}
	
	private boolean evaluateAux(Triad triad) 
	{
		int top   = m_indices.get(triad.top);
		int left  = m_indices.get(triad.left);
		int right = m_indices.get(triad.right);
		
		double ls = d_similarities[top][left];
		double rs = d_similarities[top][right];
		
//		return (ls > rs && triad.leftSimilarity > triad.rightSimilarity) || (ls < rs && triad.leftSimilarity < triad.rightSimilarity);
		return (ls > rs && triad.leftSimilarity < triad.rightSimilarity) || (ls < rs && triad.leftSimilarity > triad.rightSimilarity);
	}
	
	private ObjectIntHashMap<String> getIndexMap(String[] t)
	{
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		int i, len = t.length;
		
		for (i=0; i<len; i++)
			map.put(t[i], i);
		
		return map;
	}
	
	class Triad
	{
		public String top;
		public String left;
		public String right;
		public int leftSimilarity;
		public int rightSimilarity;
		
		public Triad(String[] t)
		{
			top   = t[3];
			left  = t[5];
			right = t[7];
			
			if (t[14].equals("L"))	// 14, 17
				leftSimilarity = 1;
			else
				rightSimilarity = 1;
		}
		
		public String getKey()
		{
			return top+" "+left+" "+right;
		}
		
		public boolean isRedundant()
		{
			return top.equals(left) || top.equals(right);
		}
	}
	
	static public void main(String[] args) throws Exception
	{
		final String triadFile  = args[0];
		final String matrixPath = args[1];
		
		Verbiverse v = new Verbiverse();
		v.initTriadList(new FileInputStream(triadFile));
		
		List<String> filenames = FileUtils.getFileList(matrixPath, "csv", false);
		
		for (String matrixFile : filenames)
		{
			v.initSimilarityMatrix(new FileInputStream(matrixFile));
			v.evaluate(matrixFile);
		}
	}
}
