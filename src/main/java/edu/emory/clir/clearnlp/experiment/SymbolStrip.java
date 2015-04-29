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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.PatternConst;
import edu.emory.clir.clearnlp.vector.Term;
import edu.emory.clir.clearnlp.vector.VectorSpaceModel;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SymbolStrip
{
	final int[] CATEGORIES = {2,3,6,8,10,12,13,18};
	private List<List<DoubleIntPair>> category_list; 
	private VectorSpaceModel vs_model;
	List<List<Term>> train_vectors;
	public SymbolStrip()
	{
		category_list = IntStream.range(0, CATEGORIES.length).mapToObj(i -> new ArrayList<DoubleIntPair>()).collect(Collectors.toList());
		vs_model = new VectorSpaceModel();
	}
	
	public void initVectors(InputStream in, int ngram, BiFunction<Term,Integer,Double> f, boolean normalize) throws Exception
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		AbstractTokenizer tokenizer = new EnglishTokenizer();
		List<List<String>> documents = new ArrayList<>();
		int id, i, j, c, len = CATEGORIES.length;
		String line = reader.readLine();
		List<String> document;
		String[] t;
		String s;
		
		for (i=0; (line = reader.readLine()) != null; i++)
		{
			line = StringUtils.toLowerCase(line);
			t = PatternConst.TAB.split(line);
			document = StringUtils.stripPunctuation(tokenizer.tokenize(t[1]));
		
			if (document.isEmpty())
				System.err.println("Empty document: "+i);
			else
			{
				id = documents.size();
				documents.add(document);
				
				for (j=0; j<len; j++)
				{
					c = CATEGORIES[j];
					
					if (c >= t.length)
						break;
					
					if (!(s = t[c]).isEmpty())
						category_list.get(j).add(new DoubleIntPair(Double.parseDouble(s), id));
				}
			}
		}
		
		reader.close();
		train_vectors = vs_model.toTFIDFs(documents, ngram, f);
		
		if (normalize)
		{
			for (List<DoubleIntPair> list : category_list)
				normalize(list);	
		}
		
//		for (i=0; i<len; i++)
//			System.out.printf("%2d: %d\n", CATEGORIES[i], category_list.get(i).size());
	}
	
	private void normalize(List<DoubleIntPair> list)
	{
		double max = list.get(0).d, min = list.get(0).d;
		int i, len = list.size();
		DoubleIntPair p;
		
		for (i=1; i<len; i++)
		{
			max = Math.max(max, list.get(i).d);
			min = Math.min(min, list.get(i).d);
		}
		
		for (i=0; i<len; i++)
		{
			p = list.get(i);
			p.d = (p.d - min) / (max - min);
		}			
	}
	
	public void measureCategories(InputStream in, OutputStream out, int ngram, BiFunction<Term,Integer,Double> f) throws Exception
	{
		Map<String,ObjectIntPair<double[]>> map = new HashMap<>();
		BufferedReader reader = IOUtils.createBufferedReader(in);
		int i, len = CATEGORIES.length;
		ObjectIntPair<double[]> p;
		List<String> document;
		List<Term> d1;
		String line;
		String[] t;
		
		while ((line = reader.readLine()) != null)
		{
			t = Splitter.splitTabs(line);
			p = map.computeIfAbsent(t[0], k -> new ObjectIntPair<>(new double[len], 0));
			document = StringUtils.stripPunctuation(Splitter.splitSpace(t[2]));
			d1 = vs_model.getTFIDFs(document, ngram, f);
			if (d1.isEmpty()) continue;
			
			for (i=0; i<len; i++)
				p.o[i] += getScore(category_list.get(i), d1, ngram, f);

			p.i++;
		}
		
		reader.close();
		
		PrintStream fout = IOUtils.createBufferedPrintStream(out);
		List<String> states = new ArrayList<>(map.keySet());
		StringJoiner joiner = new StringJoiner(",");
		Collections.sort(states);
		
		joiner.add("State");
		for (i=0; i<len; i++) joiner.add(Integer.toString(CATEGORIES[i]));
		fout.println(joiner.toString());
		
		for (String state : states)
		{
			joiner = new StringJoiner(",");
			p = map.get(state);
			joiner.add(state);
			
			for (i=0; i<len; i++)
				joiner.add(Double.toString(p.o[i] / p.i));
				
			fout.println(joiner.toString());
		}
		
		fout.close();
	}
	
	private double getScore(List<DoubleIntPair> cluster, List<Term> d1, int ngram, BiFunction<Term,Integer,Double> f)
	{
		double sum = 0;
		List<Term> d2;
		
		for (DoubleIntPair p : cluster)
		{
			d2 = train_vectors.get(p.i);
			sum += VectorSpaceModel.getCosineSimilarity(d1, d2) * p.d;
		}
		
		return sum / cluster.size();
	}
	
	public void split(String inputFile, String outputFile) throws Exception
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(outputFile);
		BufferedReader reader = IOUtils.createBufferedReader(inputFile);
		String line;
		String[] t;
		int i;
		
		for (i=0; (line = reader.readLine()) != null; i++)
		{
			if (i%10000 == 0) System.out.print(".");
			t = Splitter.splitTabs(line.trim());
			
			if (!StringUtils.containsPunctuationOrDigitsOrWhiteSpacesOnly(t[1]))
				fout.println(t[0]+"\t"+i+"\t"+t[1]);
		}
	}
	
	static public void main(String[] args) throws Exception
	{
		final String inputDir   = args[0];
		final String func       = args[1];
		final int ngram         = Integer.parseInt(args[2]);
		final boolean normalize = Boolean.parseBoolean(args[3]);
		
		final String trainFile  = inputDir+"/mind_wandering_and_axiety.txt";
		final String tweetFile  = inputDir+"/tweetsByStateSplittedCleaned.csv.out";
		final String outputFile = inputDir+"/"+func+"-"+ngram+"-"+normalize+".csv";
		final BiFunction<Term,Integer,Double> f = func.equals("tf") ? VectorSpaceModel::getTFIDF : VectorSpaceModel::getWFIDF;
		
		SymbolStrip vs = new SymbolStrip();
		vs.initVectors(IOUtils.createFileInputStream(trainFile), ngram, f, normalize);
		vs.measureCategories(IOUtils.createFileInputStream(tweetFile), IOUtils.createFileOutputStream(outputFile), ngram, f);
	}
}
