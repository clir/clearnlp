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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.component.AbstractComponent;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.nlp.NLPMode;
import edu.emory.clir.clearnlp.nlp.NLPUtils;
import edu.emory.clir.clearnlp.nlp.configuration.DecodeConfiguration;
import edu.emory.clir.clearnlp.reader.AbstractReader;
import edu.emory.clir.clearnlp.reader.LineReader;
import edu.emory.clir.clearnlp.reader.RawReader;
import edu.emory.clir.clearnlp.reader.TReader;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPDecode
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<string>")
	protected String s_configurationFile;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	protected String s_inputPath;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<string>")
	protected String s_inputExt = "*";
	@Option(name="-oe", usage="output file extension (default: cnlp)", required=false, metaVar="<string>")
	protected String s_outputExt = "cnlp";
	@Option(name="-mode", usage="pos|morph|dep|srl", required=true, metaVar="<string>")
	protected String s_mode;
	
	@Option(name="-depBeamSize", usage="beam size for dependency parsing (default: 16)", required=false, metaVar="<integer>")
	protected int dep_beam_size = 16;
	
	public NLPDecode() {}
	
	public NLPDecode(String[] args)
	{
		BinUtils.initArgs(args, this);
		NLPMode mode = NLPMode.valueOf(s_mode);
		List<String> inputFiles = FileUtils.getFileList(s_inputPath, s_inputExt, false);
		DecodeConfiguration config = new DecodeConfiguration(IOUtils.createFileInputStream(s_configurationFile));
		decode(inputFiles, s_outputExt, config, mode);
	}
	
	public void decode(List<String> inputFiles, String ouputExt, DecodeConfiguration config, NLPMode mode)
	{
		AbstractReader<?> reader = config.getReader();
		AbstractTokenizer tokenizer = null;
		AbstractComponent[] components;
		PrintStream fout;
		
		if (reader.isReaderType(TReader.TSV))
		{
			components = getComponents((TSVReader)reader, config.getLanguage(), mode, config);
		}
		else
		{
			tokenizer  = NLPUtils.getTokenizer(config.getLanguage());
			components = getComponents(config.getLanguage(), mode, config);
		}
		
		BinUtils.LOG.info("Decoding:\n");
		
		for (String inputFile : inputFiles)
		{
			BinUtils.LOG.info(inputFile+"\n");
			reader.open(IOUtils.createFileInputStream(inputFile));
			fout =  IOUtils.createBufferedPrintStream(inputFile + StringConst.PERIOD + ouputExt);
			
			switch (reader.getReaderType())
			{
			case TSV : process((TSVReader) reader, fout, mode, components);				break;
			case RAW : process((RawReader) reader, fout, mode, components, tokenizer);	break;
			case LINE: process((LineReader)reader, fout, mode, components, tokenizer);	break;
			}
			
			reader.close();
			fout.close();
		}
	}
	
	public void process(RawReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components, AbstractTokenizer tokenizer)
	{
		List<List<String>> tokens = tokenizer.segmentize(reader.getInputStream());
		int i, size = tokens.size();
		DEPTree tree;
		
		for (i=0; i<size; i++)
		{
			tree = new DEPTree(tokens.get(i), 0);
			process(tree, fout, mode, components);
		}
	}
	
	public void process(LineReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components, AbstractTokenizer tokenizer)
	{
		DEPTree tree;
		String  line;
		
		while ((line = reader.next()) != null)
		{
			tree = new DEPTree(tokenizer.tokenize(line), 0);
			process(tree, fout, mode, components);
		}
	}
	
	public void process(TSVReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components)
	{
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
			process(tree, fout, mode, components);
	}
	
	public void process(DEPTree tree, PrintStream fout, NLPMode mode, AbstractComponent[] components)
	{
		for (AbstractComponent component : components)
			component.process(tree);

		fout.println(toString(tree, mode)+StringConst.NEW_LINE);
	}
	
	private AbstractComponent[] getComponents(TLanguage language, NLPMode mode, DecodeConfiguration config)
	{
		List<AbstractComponent> list = new ArrayList<>();
		
		switch (mode)
		{
		case srl  :
		case dep  : list.add(NLPUtils.getDEPParser(language, config.getModelPath(NLPMode.dep), config.getDecodeBeamSize(mode)));
		case morph: list.add(NLPUtils.getMPAnalyzer(language));
		case pos  : list.add(NLPUtils.getPOSTagger(language, config.getModelPath(NLPMode.pos)));
		case seq  : list.add(NLPUtils.getSequenceClassifier(language, config.getModelPath(NLPMode.seq)));
		}

		return toReverseArray(list);
	}
	
	private AbstractComponent[] getComponents(TSVReader reader, TLanguage language, NLPMode mode, DecodeConfiguration config)
	{
		List<AbstractComponent> list = new ArrayList<>();
		
		switch (mode)
		{
		case seq: break; // TO DO::::::::::
		case srl:
		case dep:
			if (!reader.hasDependencyHeads())
				list.add(NLPUtils.getDEPParser(language, config.getModelPath(NLPMode.dep), config.getDecodeBeamSize(mode)));
		case morph:
			if (!reader.hasLemmas())
				list.add(NLPUtils.getMPAnalyzer(language));
		case pos:
			if (!reader.hasPOSTags())
				list.add(NLPUtils.getPOSTagger(language, config.getModelPath(NLPMode.pos)));
		}
		
		return toReverseArray(list);
	}
	
	private AbstractComponent[] toReverseArray(List<AbstractComponent> list)
	{
		AbstractComponent[] array = new AbstractComponent[list.size()];
		Collections.reverse(list);
		return list.toArray(array);
	}
	
	private String toString(DEPTree tree, NLPMode mode)
	{
		switch (mode)
		{
		case seq  : return tree.toStringSRL();
		case srl  : return tree.toStringSRL();
		case dep  : return tree.toStringDEP();
		case morph: return tree.toStringMorph();
		case pos  : return tree.toStringPOS();
		}

		throw new IllegalArgumentException("Invalid mode: "+mode.toString());
	}
		
	static public void main(String[] args)
	{
		new NLPDecode(args);
	}
}
