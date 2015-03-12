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
package edu.emory.clir.clearnlp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.ngram.Unigram;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.constant.PatternConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TFIDF
{
	Unigram<String> term_frequencies;
	Unigram<String> document_frequencies;
	
	public TFIDF()
	{
		term_frequencies = new Unigram<>();
		document_frequencies = new Unigram<>();
	}
	
	static public ObjectIntHashMap<String> getDocumentFrequencyCounts(List<String> filenames) throws FileNotFoundException
	{
		ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
		
		for (String filename : filenames)
			for (String s : DSUtils.getBagOfWords(new FileInputStream(filename), PatternConst.WHITESPACES))
				map.add(s);
		
		return map;
	}
	
	static public void main(String[] args) throws FileNotFoundException
	{
		List<String> filenames = FileUtils.getFileList(args[0], ".txt", false);
		ObjectIntHashMap<String> map = getDocumentFrequencyCounts(filenames);
		List<ObjectIntPair<String>> list = map.toList();
		DSUtils.sortReverseOrder(list);
		
		PrintStream fout = IOUtils.createBufferedPrintStream(args[1]);
		int size = filenames.size();
		System.out.println(size);
		
		for (ObjectIntPair<String> p : list)
			fout.printf("%s\t%d\t%6.4f\n", p.o, p.i, MathUtils.divide(p.i,size));
	}
}
