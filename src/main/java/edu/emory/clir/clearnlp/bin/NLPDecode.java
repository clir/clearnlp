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
package edu.emory.clir.clearnlp.bin;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.component.AbstractComponent;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.nlp.NLPUtils;
import edu.emory.clir.clearnlp.reader.AbstractReader;
import edu.emory.clir.clearnlp.reader.LineReader;
import edu.emory.clir.clearnlp.reader.RawReader;
import edu.emory.clir.clearnlp.reader.TReader;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPDecode
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<string>")
	private String s_configurationFile;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String s_inputPath;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<regex>")
	private String s_inputExt = "*";
	@Option(name="-oe", usage="output file extension (default: *)", required=false, metaVar="<regex>")
	private String s_outputExt = "*";
	@Option(name="-m", usage="model file (optional)", required=false, metaVar="<filename>")
	private String s_modelFile = null;
	@Option(name="-mode", usage="pos|dep|srl", required=true, metaVar="<string>")
	private String s_mode = ".*";
	
	public NLPDecode() {}
	
	public NLPDecode(String[] args)
	{
		BinUtils.initArgs(args, this);
//		NLPMode mode = NLPMode.valueOf(s_mode);
//		List<String> inputFiles = FileUtils.getFileList(s_inputPath, s_inputExt, false);
	}
	
	public void process(RawReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components, AbstractTokenizer tokenizer)
	{
		List<List<String>> tokens = tokenizer.segmentize(reader.getInputStream());
		int i, size = tokens.size();
		DEPTree tree;
		
		for (i=0; i<size; i++)
		{
			tree = new DEPTree(tokens.get(i), true);
			process(fout, mode, components, tree);
		}
	}
	
	public void process(LineReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components, AbstractTokenizer tokenizer)
	{
		DEPTree tree;
		String line;
		
		while ((line = reader.next()) != null)
		{
			tree = new DEPTree(tokenizer.tokenize(line), true);
			process(fout, mode, components, tree);
		}
	}
	
	public void process(TSVReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components)
	{
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
			process(fout, mode, components, tree);
	}
	
	public void process(PrintStream fout, NLPMode mode, AbstractComponent[] components, DEPTree tree)
	{
		for (AbstractComponent component : components)
			component.process(tree);
		
		fout.println(toString(tree, mode)+StringConst.NEW_LINE);
	}
	
	public AbstractComponent[] getComponents(AbstractReader<?> reader, TLanguage language, NLPMode mode, String modelPath) throws IOException
	{
		List<AbstractComponent> list;
		
		if (reader.isReaderType(TReader.TSV))
			list = getComponents((TSVReader)reader, language, mode, modelPath);
		else
			list = getComponents(language, mode, modelPath);

		AbstractComponent[] array = new AbstractComponent[list.size()];
		list.toArray(array);
		return array;
	}
	
	@SuppressWarnings("incomplete-switch")
	public List<AbstractComponent> getComponents(TLanguage language, NLPMode mode, String modelPath)
	{
		List<AbstractComponent> list = new ArrayList<>();
		
		switch (mode)
		{
		case dep: list.add(NLPUtils.getDEPParser(language, modelPath));
		case pos: list.add(NLPUtils.getMPAnalyzer(language));
		          list.add(NLPUtils.getPOSTagger(language, modelPath));
		}

		Collections.reverse(list);
		return list;
	}
	
	public List<AbstractComponent> getComponents(TSVReader reader, TLanguage language, NLPMode mode, String modelPath) throws IOException
	{
		List<AbstractComponent> list = new ArrayList<>();
		
		if (mode == NLPMode.pos)
		{
			list.add(NLPUtils.getPOSTagger(language, modelPath));
			list.add(NLPUtils.getMPAnalyzer(language));
		}
		else if (mode == NLPMode.dep)
		{
			if (!reader.hasPOSTags())
				list.add(NLPUtils.getPOSTagger(language, modelPath));
			
			if (!reader.hasLemmas())
				list.add(NLPUtils.getMPAnalyzer(language));
			
			list.add(NLPUtils.getDEPParser(language, modelPath));
		}
		
		return list;
	}
	
	private String toString(DEPTree tree, NLPMode mode)
	{
		switch (mode)
		{
		case pos: return tree.toStringMorph();
		case dep: return tree.toStringDEP();
		case srl: return tree.toStringSRL();
		default : throw new IllegalArgumentException("Invalid mode: "+mode.toString());
		}
	}
		
	static public void main(String[] args)
	{
		new NLPDecode(args);
	}
}
