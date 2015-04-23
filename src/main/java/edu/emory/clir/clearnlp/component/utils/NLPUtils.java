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
package edu.emory.clir.clearnlp.component.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tukaani.xz.XZInputStream;

import edu.emory.clir.clearnlp.collection.tree.PrefixTree;
import edu.emory.clir.clearnlp.component.mode.dep.AbstractDEPParser;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.mode.dep.DefaultDEPParser;
import edu.emory.clir.clearnlp.component.mode.dep.EnglishDEPParser;
import edu.emory.clir.clearnlp.component.mode.morph.AbstractMPAnalyzer;
import edu.emory.clir.clearnlp.component.mode.morph.DefaultMPAnalyzer;
import edu.emory.clir.clearnlp.component.mode.morph.EnglishMPAnalyzer;
import edu.emory.clir.clearnlp.component.mode.pos.AbstractPOSTagger;
import edu.emory.clir.clearnlp.component.mode.pos.DefaultPOSTagger;
import edu.emory.clir.clearnlp.component.mode.pos.EnglishPOSTagger;
import edu.emory.clir.clearnlp.conversion.AbstractC2DConverter;
import edu.emory.clir.clearnlp.conversion.EnglishC2DConverter;
import edu.emory.clir.clearnlp.conversion.headrule.HeadRuleMap;
import edu.emory.clir.clearnlp.ner.NERInfoSet;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPUtils
{
	private NLPUtils() {}
	
	/** @param in the inputstream for a headrule file. */
	static public AbstractC2DConverter getC2DConverter(TLanguage language, InputStream in)
	{
		HeadRuleMap headrules = new HeadRuleMap(in);
		return new EnglishC2DConverter(headrules);
	}
	
	static public AbstractTokenizer getTokenizer(TLanguage language)
	{
		return new EnglishTokenizer();
	}
	
	static public AbstractMPAnalyzer getMPAnalyzer(TLanguage language)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishMPAnalyzer();
		default     : return new DefaultMPAnalyzer();
		}
	}
	
	static public AbstractPOSTagger getPOSTagger(TLanguage language, ObjectInputStream in)
	{
		BinUtils.LOG.info("Loading part-of-speech tagging models.\n");
		
		switch (language)
		{
		case ENGLISH: return new EnglishPOSTagger(in);
		default     : return new DefaultPOSTagger(in);
		}
	}
	
	static public AbstractPOSTagger getPOSTagger(TLanguage language, String modelPath)
	{
		return getPOSTagger(language, getObjectInputStream(modelPath));
	}
	
	static public AbstractDEPParser getDEPParser(TLanguage language, ObjectInputStream in, DEPConfiguration configuration)
	{
		BinUtils.LOG.info("Loading dependency parsing models.\n");
		
		switch (language)
		{
		case ENGLISH: return new EnglishDEPParser(configuration, in);
		default     : return new DefaultDEPParser(configuration, in);
		}
	}
	
	static public AbstractDEPParser getDEPParser(TLanguage language, String modelPath, DEPConfiguration configuration)
	{
		return getDEPParser(language, getObjectInputStream(modelPath), configuration);
	}
	
//	static public AbstractNERecognizer getNERecognizer(TLanguage language, ObjectInputStream in)
//	{
//		BinUtils.LOG.info("Loading named entity recognition models.\n");
//		
//		switch (language)
//		{
//		case ENGLISH: return new EnglishNERecognizer(in);
//		default     : return new DefaultNERecognizer(in);
//		}
//	}
//	
//	static public AbstractNERecognizer getNERecognizer(TLanguage language, String modelPath)
//	{
//		return getNERecognizer(language, getObjectInputStream(modelPath));
//	}
	
	@SuppressWarnings("unchecked")
	static public PrefixTree<String,NERInfoSet> getNERDictionary(ObjectInputStream in)
	{
		BinUtils.LOG.info("Loading named entity dictionary.\n");
		PrefixTree<String,NERInfoSet> tree = null;
		
		try
		{
			tree = (PrefixTree<String,NERInfoSet>)in.readObject();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return tree;
	}
	
	static public PrefixTree<String,NERInfoSet> getNERDictionary(String modelPath)
	{
		return getNERDictionary(NLPUtils.getObjectInputStream(modelPath));
	}
	
	@SuppressWarnings("unchecked")
	static public Map<String,Set<String>> getDistributionalSemantics(ObjectInputStream in)
	{
		BinUtils.LOG.info("Loading distributional semantics.\n");
		Map<String,Set<String>> map = null;
		
		try
		{
			map = (HashMap<String,Set<String>>)in.readObject();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return map;
	}
	
	static public Map<String,Set<String>> getDistributionalSemantics(String modelPath)
	{
		return getDistributionalSemantics(getObjectInputStream(modelPath));
	}
	
	static public ObjectInputStream getObjectInputStream(String modelPath)
	{
		try
		{
			return new ObjectInputStream(new XZInputStream(new BufferedInputStream(IOUtils.getInputStreamsFromClasspath(modelPath))));
		}
		catch (IOException e) {e.printStackTrace();}

		return null;
	}
}
