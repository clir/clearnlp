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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.component.AbstractComponent;
import edu.emory.clir.clearnlp.component.configuration.DecodeConfiguration;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.component.utils.NLPMode;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
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
	@Option(name="-mode", usage="pos|morph|dep|ner", required=true, metaVar="<string>")
	protected String s_mode;
	@Option(name="-threads", usage="number of threads (default: 1)", required=false, metaVar="<integer>")
	protected int n_threads = 1;
	
//	private long time = 0, tokens = 0, trees = 0;
	
	public NLPDecode() {}
	
	public NLPDecode(String[] args)
	{
		BinUtils.initArgs(args, this);
		NLPMode mode = NLPMode.valueOf(s_mode);
		List<String> inputFiles = FileUtils.getFileList(s_inputPath, s_inputExt, false);
		if (n_threads > 2)	decode(inputFiles, s_outputExt, s_configurationFile, n_threads, mode);
		else				decode(inputFiles, s_outputExt, s_configurationFile, mode);
//		System.out.printf("Tokens / Sec.: %d\n", Math.round(MathUtils.divide(tokens*1000, time)));
//		System.out.printf("Sents. / Sec.: %d\n", Math.round(MathUtils.divide(trees *1000, time)));
	}
	
	public void decode(List<String> inputFiles, String outputExt, String configurationFile, NLPMode mode)
	{
		DecodeConfiguration config = new DecodeConfiguration(IOUtils.createFileInputStream(configurationFile));;
		GlobalLexica.init(IOUtils.createFileInputStream(configurationFile));
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
			BinUtils.LOG.info(FileUtils.getBaseName(inputFile)+"\n");
			reader.open(IOUtils.createFileInputStream(inputFile));
			fout = IOUtils.createBufferedPrintStream(inputFile + StringConst.PERIOD + outputExt);
			
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
	
	public void decode(List<String> inputFiles, String outputExt, String configurationFile, int nThreads, NLPMode mode)
	{
		DecodeConfiguration config = new DecodeConfiguration(IOUtils.createFileInputStream(s_configurationFile));;
		GlobalLexica.init(IOUtils.createFileInputStream(configurationFile));
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		AbstractReader<?> reader = config.getReader();
		AbstractTokenizer tokenizer = null;
		AbstractComponent[] components;
		String outputFile;
		
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
			outputFile = inputFile + StringConst.PERIOD + outputExt;
			executor.submit(new NLPTask(tokenizer, components, reader, mode, inputFile, outputFile));
		}
		
		executor.shutdown();
	}
	
	class NLPTask implements Runnable
	{
		private AbstractComponent[] components;
		private AbstractTokenizer tokenizer;
		private AbstractReader<?> reader;
		private String input_file;
		private PrintStream fout;
		private NLPMode mode;
		
		public NLPTask(AbstractTokenizer tokenizer, AbstractComponent[] components, AbstractReader<?> reader, NLPMode mode, String inputFile, String outputFile)
		{
			this.mode = mode;
			this.tokenizer = tokenizer;
			this.input_file = inputFile;
			this.components = components;
			this.reader = reader.clone();
			this.reader.open(IOUtils.createFileInputStream(inputFile));
			this.fout = IOUtils.createBufferedPrintStream(outputFile);
		}
		
		@Override
		public void run()
		{
			try
			{
				BinUtils.LOG.info(FileUtils.getBaseName(input_file)+"\n");
				
				switch (reader.getReaderType())
				{
				case TSV : process((TSVReader) reader, fout, mode, components);				break;
				case RAW : process((RawReader) reader, fout, mode, components, tokenizer);	break;
				case LINE: process((LineReader)reader, fout, mode, components, tokenizer);	break;
				}
				
				reader.close();
				fout.close();
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public void process(RawReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components, AbstractTokenizer tokenizer)
	{
		List<List<String>> tokens = tokenizer.segmentize(reader.getInputStream());
		int i, size = tokens.size();
		DEPTree tree;
		
		for (i=0; i<size; i++)
		{
			tree = new DEPTree(tokens.get(i));
			process(tree, fout, mode, components);
		}
	}
	
	public void process(LineReader reader, PrintStream fout, NLPMode mode, AbstractComponent[] components, AbstractTokenizer tokenizer)
	{
		DEPTree tree;
		String  line;
		
		while ((line = reader.next()) != null)
		{
			tree = new DEPTree(tokenizer.tokenize(line));
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
//		long st, et;
		
		for (AbstractComponent component : components)
		{
//			st = System.currentTimeMillis();
			component.process(tree);
//			et = System.currentTimeMillis();
//			time += et - st;
		}

//		tokens += tree.size() - 1;
//		trees++;
		fout.println(toString(tree, mode)+StringConst.NEW_LINE);
	}
	
	private AbstractComponent[] getComponents(TLanguage language, NLPMode mode, DecodeConfiguration config)
	{
		List<AbstractComponent> list = new ArrayList<>();
		
		switch (mode)
		{
		case srl  :
		case ner  : list.add(NLPUtils.getNERecognizer(language, config.getModelPath(NLPMode.ner)));
		case dep  : list.add(NLPUtils.getDEPParser(language, config.getModelPath(NLPMode.dep), new DEPConfiguration(IOUtils.createFileInputStream(s_configurationFile))));
		case morph: list.add(NLPUtils.getMPAnalyzer(language));
		case pos  : list.add(NLPUtils.getPOSTagger(language, config.getModelPath(NLPMode.pos)));
		}

		return toReverseArray(list);
	}
	
	private AbstractComponent[] getComponents(TSVReader reader, TLanguage language, NLPMode mode, DecodeConfiguration config)
	{
		List<AbstractComponent> list = new ArrayList<>();
		
		switch (mode)
		{
		case srl:
		case ner:
			if (!reader.hasNamedEntityTags())
				list.add(NLPUtils.getNERecognizer(language, config.getModelPath(NLPMode.ner)));
		case dep:
			if (!reader.hasDependencyHeads())
				list.add(NLPUtils.getDEPParser(language, config.getModelPath(NLPMode.dep), new DEPConfiguration(IOUtils.createFileInputStream(s_configurationFile))));
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
		case srl  : return tree.toString(DEPNode::toStringSRL);
		case ner  : return tree.toString(DEPNode::toStringNER);
		case dep  : return tree.toString(DEPNode::toStringDEP);
		case morph: return tree.toString(DEPNode::toStringMorph);
		case pos  : return tree.toString(DEPNode::toStringPOS);
		}

		throw new IllegalArgumentException("Invalid mode: "+mode.toString());
	}
		
	static public void main(String[] args)
	{
		new NLPDecode(args);
	}
}
